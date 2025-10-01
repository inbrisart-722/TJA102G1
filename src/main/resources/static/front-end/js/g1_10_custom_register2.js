// 顯示 Toast 訊息的函式
function showToast(message) {
	const toastContainer = document.getElementById("toast-container");

	// 建立 Toast 元素
	const toast = document.createElement("div");
	toast.className = "toast-message";

	// 加入成功圖示 (可以使用 emoji 或 SVG)
	toast.innerHTML = `
    <span class="icon">&#10004;</span>
    <span>${message}</span>
  `;

	// 將 Toast 加入到容器中
	toastContainer.appendChild(toast);

	// 設置定時器，在幾秒後移除 Toast
	setTimeout(() => {
		toast.style.animation = "fadeOut 0.5s forwards";
		setTimeout(() => {
			toast.remove();
		}, 500); // 移除元素的時間要與動畫時間一致
	}, 3000); // 顯示 3 秒
}

// 範例：在頁面載入後立即顯示 Toast
// 實際應用時，你可以在後台驗證成功後呼叫這個函式
document.addEventListener("DOMContentLoaded", () => {
	// 假設這是從後台或 URL 參數判斷的成功狀態
	const isVerificationSuccess = true;

	// 1. 先給他一個彈窗
	if (isVerificationSuccess) {
		showToast("信箱驗證成功！請繼續完成註冊！");
	}

	// 2. 聆聽輸入是否有錯誤
	const password1Input = document.getElementById("password1");
	const password2Input = document.getElementById("password2");
	const nicknameInput = document.getElementById("nickname");

	const password1Error = document.getElementById("password1Error");
	const password2Error = document.getElementById("password2Error");
	const nicknameError = document.getElementById("nicknameError");

	const submitBtn = document.getElementById("submitBtn");

	function validatePassword() {
		const p1 = password1Input.value.trim();
		if (p1.length < 6) {
			displayError(password1Input, password1Error, "密碼長度至少6個字元");
			return false;
		} else {
			displaySuccess(password1Input, password1Error, "密碼格式正確");
			return true;
		}
	}

	function validatePasswordMatch() {
		const p1 = password1Input.value.trim();
		const p2 = password2Input.value.trim();

		if (p1 !== p2 || p2.length === 0) {
			displayError(password2Input, password2Error, "兩次輸入的密碼不一致");
			return false;
		} else {
			displaySuccess(password2Input, password2Error, "密碼一致");
			return true;
		}
	}

	function validateNickname() {
		const name = nicknameInput.value.trim();
		if (name.length < 2 || name.length > 50) {
			displayError(nicknameInput, nicknameError, "暱稱長度需介於2到50個字元");
			return false;
		} else {
			displaySuccess(nicknameInput, nicknameError, "暱稱格式正確");
			return true;
		}
	}

	function displayError(input, messageEl, message) {
		messageEl.textContent = message;
		messageEl.className = "validation-message error";
		input.classList.add("is-invalid");
		input.classList.remove("is-valid");
	}

	function displaySuccess(input, messageEl, message) {
		messageEl.textContent = message;
		messageEl.className = "validation-message success";
		input.classList.remove("is-invalid");
		input.classList.add("is-valid");
	}

	// 即時驗證
	password1Input.addEventListener("input", validatePassword);
	password2Input.addEventListener("input", validatePasswordMatch);
	nicknameInput.addEventListener("input", validateNickname);

	// 送出時最終驗證
	submitBtn.addEventListener("click", (e) => {
		e.preventDefault();

		const isPasswordValid = validatePassword();
		const isPasswordMatchValid = validatePasswordMatch();
		const isNicknameValid = validateNickname();

		if (isPasswordValid && isPasswordMatchValid && isNicknameValid) {
			const params = new URLSearchParams(window.location.search);
			const token = params.get("token");

			const password = password1Input.value;
			const nickname = nicknameInput.value;
			const send_data = { token, password, nickname };

			csrfFetch("/api/front-end/member/register", {
				method: "POST",
				headers: {
					"CONTENT-TYPE": "application/json",
				},
				body: JSON.stringify(send_data),
			})
				.then((res) => {
					if (!res.ok) throw new Error("register: NOT OK");
					return res.text();
				})
				.then((result) => {
					console.log("email: " + result);
					// result -> email 
					if (result === "") {
						alert("由於您的 token 錯誤或逾時，請重新進行註冊");
						window.location.href = "/front-end/register1";
					}

					const email = result;
					const send_data = {
						username: email,
						password: password,
					};

					// ver1. 註冊成功，導登入頁面
//					alert("恭喜註冊成功！將為您於3秒後導至登入頁！");
//					
//					setTimeout(() => {
//						location.href = "/front-end/login";
//					}, 3000);

					// ver2. 註冊成功，協助打 login api 取得 member token 登入身份
//					alert("恭喜註冊成功！將為您於3秒後導至首頁！");
					alert("恭喜註冊成功！將為您轉導至首頁！");
					
					fetch("/api/auth/login/member", {
						method: "POST",
						headers: {
							"Content-Type": "application/json"
						},
						body: JSON.stringify(send_data),
					})
						.then((res) => {
							if (!res.ok) throw new Error("Login failed");
							return res.json();
						})
						.then((result) => {
							console.log(result);
							setTimeout(() => {
								location.href = "/front-end/index";
							}, 500);
						})
						.catch(error => console.log(error));


				})
				.catch((error) => {
					console.log("error");
					console.log(error);
				});
		}
	});
});

document.addEventListener("DOMContentLoaded", () => {
	// 找所有 form-group 內的密碼輸入框
	const passwordGroups = document.querySelectorAll(".form-group");

	passwordGroups.forEach(group => {
		const input = group.querySelector("input[type='password']");
		const icon = group.querySelector("i");

		if (input && icon) {
			icon.addEventListener("click", () => {
				if (input.type === "password") {
					input.type = "text";
					icon.classList.remove("icon-eye-7");
					icon.classList.add("icon-eye-off-1");
				} else {
					input.type = "password";
					icon.classList.remove("icon-eye-off-1");
					icon.classList.add("icon-eye-7");
				}
			});
		}
	});
});

