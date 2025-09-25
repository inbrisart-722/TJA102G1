document.addEventListener("DOMContentLoaded", () => {
	const emailInput = document.getElementById("email");
	const emailError = document.getElementById("emailError");
	const getVerificationCodeBtn = document.getElementById("getVerificationCode");

	// Email 驗證的正規表達式
	const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

	// 顯示彈窗的函數
	const showToast = () => {
		toast.classList.add("show");
		// 3 秒後自動隱藏
		setTimeout(() => {
			toast.classList.remove("show");
		}, 3000);
	};

	const formatSeconds = function(seconds) {
		const mm = String(Math.floor(seconds / 60)).padStart(2, "0");
		const ss = String(seconds % 60).padStart(2, "0");
		return `${mm}:${ss}`;
	} 
	
	// 檢查是否為會員的函式，回傳 Promise
	const check_member = function(email) {
		return csrfFetch("/api/front-end/member/check-if-member?email=" + email, {
			method: "GET",
		})
			.then((res) => res.json())
			.then((result) => {
				return result[0]; // true, false
			}) // 直接回傳布林值
			.catch((error) => {
				return Promise.reject(error);
			});
	};

	getVerificationCodeBtn.addEventListener("click", (e) => {
		e.preventDefault();

		const email = emailInput.value.trim();

		// 清空上次的錯誤訊息和樣式
		emailError.textContent = "";
		emailInput.classList.remove("is-invalid");

		// 1. 檢查信箱格式
		if (!emailRegex.test(email)) {
			emailError.textContent =
				"請輸入有效的電子信箱格式 (例如: user@example.com)。";
			emailInput.classList.add("is-invalid");
			return;
		}

		// 在發出請求前先停用按鈕
		getVerificationCodeBtn.disabled = true;

		// 2. 檢查是否為會員
		check_member(email)
			.then((isMember) => {
				// 如果是會員，顯示錯誤訊息並停止後續流程
				if (isMember) {
					emailError.textContent = "此信箱已註冊成為會員囉！（一般會員 或 透過第三方登入綁定）";
					emailInput.classList.add("is-invalid");
					getVerificationCodeBtn.disabled = false;
					// 使用 Promise.reject() 停止 Promise 鏈，將控制權交給 .catch
					// 這樣後面的 .then 就不會執行
					return Promise.reject(new Error("check-if-member: Already a member"));
				}

				return csrfFetch("/api/front-end/verif/check-if-sendable?email=" + email, {
					method: "GET",
				})
			})
			.then((res) => {
				if (!res.ok) throw new Error("check-if-sendable: Network response was not ok");
				return res.json();
			})
			.then(checkIfSendable => {
				let remaining = formatSeconds(checkIfSendable.remaining);
				
				if (!checkIfSendable.allowed){
					const error = new Error("您還需要等待 " + remaining + " 才能再次發送驗證信哦！");
					error.remaining = checkIfSendable.remaining; // 把數字塞給 catch 去 send
					throw error;
				} 
				// isallowed, remaining;

				// 可以發驗證信了
				const send_data = { email, authType: "CHANGE_MAIL" };
				return csrfFetch("/api/front-end/protected/verif/send-verif-code/change-mail", {
					method: "POST",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify(send_data),
				})
			})
			.then((res) => {
				// 如果上一步的 fetch 成功，則處理回應
				if (!res.ok) {
					return res.text().then((error) => {
						throw new Error(error || "send-verif-code: Network response was not ok");
					});
				}
				return res.text();
			})
			.then((result) => {
				// 如果驗證碼發送成功
				if (result.toUpperCase() === "SUCCESS") {
					showToast();
				} 
				else { 
//				else if (result.toUpperCase() === "FAILURE") {
					emailError.textContent = "發送驗證碼失敗。";
					emailInput.classList.add("is-invalid");
					getVerificationCodeBtn.disabled = false;
				}
			})
			.catch((error) => {
				// 處理所有錯誤，包括網路錯誤和「已是會員」的錯誤
				// 只有當 error.message 不是 'Already a member' 時才顯示給用戶
				if (error.message.startsWith("您還需要等待")) {
				   emailError.textContent = error.message;
				   emailInput.classList.add("is-invalid");
				   getVerificationCodeBtn.disabled = true;
				   
				   let secondsLeft = error.remaining;
				   // 啟動倒數
				   const interval = setInterval(() => {
					secondsLeft--;
					emailError.textContent =  `您還需要等待 ${formatSeconds(secondsLeft)} 才能再次發送驗證信哦！`
					// 倒數結束，清空計時器，按鈕恢復可按，清空錯誤訊息
					if (secondsLeft <= 0){
						clearInterval(interval);
						getVerificationCodeBtn.disabled = false;
						emailInput.classList.remove("is-invalid");
						emailError.textContent = "";
					}
				   }, 1000);
				 } else if (error.message !== "check-if-member: Already a member") {
				   console.error("Error:", error);
				   emailError.textContent = "伺服器錯誤，請稍後再試。";
				   emailInput.classList.add("is-invalid");
				   getVerificationCodeBtn.disabled = false;
				 }
			});
	});
});
