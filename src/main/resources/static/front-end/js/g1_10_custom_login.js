document.addEventListener("DOMContentLoaded", function () {
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
        console.log(result);
		
		// api - redirect
		const redirect_api = sessionStorage.getItem("redirect");
		sessionStorage.removeItem("redirect");
		
		// ssr/page - redirect
		const params = new URLSearchParams(window.location.search);
		let redirect_ssr = params.get("redirect");
		if(redirect_ssr && !redirect_ssr.startsWith("/front-end/"))
			redirect_ssr = null;
		
		if(!redirect_ssr && !redirect_api)
			window.location.href = "/front-end/index";
		else if(redirect_api) window.location.href = redirect_api;
		else if(redirect_ssr) window.location.href = redirect_ssr;
      })
      .catch((error) => {
        console.log(error);
      });
  });
});
