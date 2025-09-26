document.addEventListener("DOMContentLoaded", function() {
	const btn_ecpay = document.querySelector("a#ecpay");
	btn_ecpay.addEventListener("click", function(e) {
		e.preventDefault();
		const all_tr_el = document.querySelectorAll("tr[data-cart-item-id]");

		let send_data = {};
		send_data["cartItemIds"] = [];

		all_tr_el.forEach(tr => {
			send_data["cartItemIds"].push(tr.dataset.cartItemId);
		})

		csrfFetchToRedirect("/api/front-end/protected/order/ECPay/sending", {
			method: "POST",
			headers: {
				"CONTENT-TYPE": "application/json",
			},
			body: JSON.stringify(send_data),
		})
			.then((res) => {
				if (!res.ok) throw new Error("ECPay/sending: Not 2XX or 401");
				return res.json();
			})
			.then((result) => {
				console.log(result);
				// action, method, fields, status
				if (result.status === "success") {
					const form = document.createElement("form");
					form.method = (result.method || "POST").toUpperCase();
					form.action = result.action;

					// .entries returns an array of key-value pairs
					// Each pair is itself a small array
					Object.entries(result.fields).forEach(([k, v]) => {
						const input = document.createElement("input");
						input.type = "hidden";
						input.name = k;
						// null or undefined return ""
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
					console.log("ECPay sending failed");
				}
			})
			.catch((error) => {
				console.log("error");
				console.log(error);
			});
	});
});

document.addEventListener("DOMContentLoaded", function() {
	const btn_linepay = document.querySelector("a#linepay");
	
	btn_linepay.addEventListener("click", function(e){
		console.log(3);
		e.preventDefault();
		console.log(4);
		csrfFetchToRedirect("/api/front-end/protected/linepay/payment-request", {
			method: "POST",
		}).then( res => {
			if(!res.ok) throw new Error("linepay/payment-request: NOT 2XX");
			else return res.text();
		}).then( data =>{
			console.log(data);
			setTimeout(() => window.location.href = data, 3000);
		}).catch( error => {
			console.log(error);
		})
	})
});

