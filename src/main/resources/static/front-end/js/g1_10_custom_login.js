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
    fetch("/api/auth/login", {
      method: "POST",
      headers: {
        "CONTENT-TYPE": "application/json",
      },
      body: JSON.stringify(send_data),
    })
      .then((res) => {
        if (!res.ok) throw new Error("NOT OK");
        return res.json();
      })
      .then((result) => {
        console.log(result);
        if (result.status === "success") {
          console.log("login success, 預計之後後端 forward？");
        } else if (result.status === "failed") {
          console.log("login failed");
        }
		location.href = "/front-end/cart";
      })
      .catch((error) => {
        console.log("error");
        console.log(error);
      });
  });
});
