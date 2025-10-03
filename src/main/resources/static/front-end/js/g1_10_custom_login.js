document.addEventListener("DOMContentLoaded", function () {
	
	function showError(msg) {
	  // 先刪掉舊的錯誤訊息（避免重複）
	  const oldError = document.querySelector("#login-error");
	  if (oldError) oldError.remove();

	  // 建立一個 <div> 或 <p>
	  const errorEl = document.createElement("p");
	  errorEl.id = "login-error";
	  errorEl.textContent = msg;
	  errorEl.style.color = "red"; // 或加 class
	  errorEl.style.margin = "5px 0 10px 0";

	  // 找到目標節點
	  const forgotP = document.getElementById("forgot-password-p");

	  // 插到它前面
	  forgotP.parentNode.insertBefore(errorEl, forgotP);
	}

  // 1. 登入按鈕本身
  const btn_login = document.querySelector("a#btn_login");
  btn_login.addEventListener("click", function (e) {
    e.preventDefault();

    const input_email = document.querySelector("input#email");
    const input_password = document.querySelector("input#password");

    const send_data = {
      username: input_email.value,
      password: input_password.value,
    };
    fetch("/api/auth/login/member", {
      method: "POST",
      headers: {
        "CONTENT-TYPE": "application/json",
      },
      body: JSON.stringify(send_data),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Login failed");
        return res.json();
      })
      .then((result) => {
      	console.log(result.status);
		
		// api - redirect
		const redirect_api = sessionStorage.getItem("redirect");
		sessionStorage.removeItem("redirect");
		
		// ssr/page - redirect
		const params = new URLSearchParams(window.location.search);
		let redirect_ssr = params.get("redirect");
		if(redirect_ssr) {
			params.delete("redirect");
			const remainingParams = params.toString();
			
			if(remainingParams){
				redirect_ssr += "?" + remainingParams;
			}
		}
	
		// 開放重定向攻擊 (Open Redirect Attack);
		// 若為 ssr 但又非 /front-end/ 開頭，可能被攻擊，不導!!!
		if(redirect_ssr && !redirect_ssr.startsWith("/front-end/"))
			redirect_ssr = null;
		
		if(!redirect_ssr && !redirect_api)
			window.location.href = "/front-end/index";
		// api 導向（使用者操作）通常更優先
		else if(redirect_api) window.location.href = redirect_api;
		else if(redirect_ssr) window.location.href = redirect_ssr;
      })
      .catch((error) => {
        console.log(error);
		showError("輸入的帳號或密碼有錯誤！");
      });
  });
  
  // 2. google 第三方登入
  
  
});
