
const all_tr_el = document.querySelectorAll("tr[data-cart-item-id]");
	console.log(all_tr_el);
	
    let send_data = {};
    send_data["cartItemIds"] = [];
	
	all_tr_el.forEach(tr => {
		send_data["cartItemIds"].push(tr.dataset.cartItemId);
	})	
	
	conseol.log(send_data["cartItemIds"]);
	
document.addEventListener("DOMContentLoaded", function () {
  const btn_pay = document.querySelector("a#pay");
  btn_pay.addEventListener("click", function (e) {
    e.preventDefault();

	

    fetch("http://localhost:8088/api/order/ECPay/sending", {
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
        // action, method, fields, status
        if (result.status === "success") {
          const form = document.createElement("form");
          form.method = (result.method || "POST").toUpperCase();
          form.action = result.action;

          Object.entries(result.fields).forEach(([k, v]) => {
            const input = document.createElement("input");
            input.type = "hidden";
            input.name = k;
            input.value = (v ?? "").toString();
            form.appendChild(input);
          });

          // noscript 保險（可選）
          const btn = document.createElement("button");
          btn.type = "submit";
          btn.style.display = "none";
          form.appendChild(btn);

          document.body.appendChild(form);
          form.submit();
        } else {
          console.log("login failed");
        }
      })
      .catch((error) => {
        console.log("error");
        console.log(error);
      });
  });
});
