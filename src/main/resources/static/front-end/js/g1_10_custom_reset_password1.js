
document.addEventListener("DOMContentLoaded", () => {

	let password_try = 0;
	
	// 2. 聆聽輸入是否有錯誤
	const password1Input = document.getElementById("password1");
	const password2Input = document.getElementById("password2");
	const passwordOldInput = document.getElementById("passwordOld");

	const password1Error = document.getElementById("password1Error");
	const password2Error = document.getElementById("password2Error");

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

	function checkIfPasswordCorrect(password) {
		return csrfFetch("/api/front-end/protected/member/check-if-password-correct", {
			method: "POST",
			headers: {
				"Content-Type": "application/json"
			},
			body: JSON.stringify({password})
		})
			.then(res => {
				if (!res.ok) throw new Error("check-if-password-correct: Not 2XX");
				else return res.text();
			})
			.catch(error => {
				console.log(error);
				return false;
			})

	}

	// 即時驗證
	password1Input.addEventListener("input", validatePassword);
	password2Input.addEventListener("input", validatePasswordMatch);

	// 送出時最終驗證
	submitBtn.addEventListener("click", (e) => {
		e.preventDefault();

		const isPasswordValid = validatePassword();
		const isPasswordMatchValid = validatePasswordMatch();

		if (isPasswordValid && isPasswordMatchValid) {

			const passwordOld = passwordOldInput.value;
			const passwordNew = password1Input.value;

			checkIfPasswordCorrect(passwordOld).then(res => {
				console.log(res);
				
				if (res === "false") {
					if(password_try < 2){
						password_try++;
						alert("密碼輸入錯誤: " + password_try + "次！(最多嘗試3次）");
					}
					else{
						alert("密碼錯誤已達 3 次，請稍後再試！")
						window.location.href = "/front-end/admin";
					}
				}
				
				else if (res === "true") {
					console.log(passwordNew);
					csrfFetch("/api/front-end/protected/member/reset-password", {
						method: "POST",
						headers: {
							"Content-Type": "application/json",
						},
						body: JSON.stringify({password : passwordNew}),
					})
						.then((res) => {
							if (!res.ok) throw new Error("reset password: NOT 2XX");
							return res.text();
						})
						.then((result) => {
							if (result === "SUCCESS") {
								alert("成功更換密碼！將為您於3秒後導回會員中心頁面！");
								window.location.href = "/front-end/admin";
							}
						})
						.catch((error) => {
							console.log("error");
							console.log(error);
						});
				}
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

