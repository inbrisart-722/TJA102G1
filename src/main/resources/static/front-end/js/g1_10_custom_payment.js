document.addEventListener("DOMContentLoaded", function() {

	const btn_third_party_payment = document.querySelector("#third-party-payment");

	btn_third_party_payment.addEventListener("click", (e) => {
		e.preventDefault();


		// 1. 是否填入載具
		const carrierInput = document.getElementById("carrier_text");
		const value = carrierInput.value.trim();

		// 手機條碼規則
		const regex = /^\/[0-9A-Z.\-\+]{7}$/;

		// 找錯誤訊息元素
		let errorMsg = document.getElementById("carrier_error");
		if (errorMsg) errorMsg.remove();

		if (!value || !regex.test(value)) {
			// 顯示錯誤提示
			errorMsg = document.createElement("div");
			errorMsg.id = "carrier_error";
			errorMsg.style.color = "red";
			errorMsg.style.marginTop = "5px";
			carrierInput.insertAdjacentElement("afterend", errorMsg);
			errorMsg.textContent = "手機條碼／載具格式錯誤：必須是「/」開頭，後面7個字元為大寫英文、數字或 . - +";
			carrierInput.focus();
			return;
		}

		// 2. 是否勾選
		const agreement_el = document.querySelector("input#agreement");
		if (agreement_el.checked === false) {
			alert("請「詳閱並勾選」同意買賣契約書！");
			return;
		}

		///////////////////////////////////////////////////

		// 0. 無論如何都先把 cartItemIds 收集起來（兩種支付方式都會用到）
		const all_tr_el = document.querySelectorAll("tr[data-cart-item-id]");

		let send_data = {};
		send_data["cartItemIds"] = [];

		all_tr_el.forEach(tr => {
			send_data["cartItemIds"].push(tr.dataset.cartItemId);
		})

		// 1. 先判斷走 綠界 還是 LINE PAY
		const pay_method = document.querySelector('input[name="pay_method"]:checked + img + label').innerText;
		console.log(pay_method);

		// 2-1. 走 Line Pay
		if (pay_method === "Line Pay") {
			csrfFetchToRedirect("/api/front-end/protected/linepay/payment-request", {
				method: "POST",
				headers: {
					"Content-Type": "application/json"
				},
				body: JSON.stringify(send_data)
			}).then(res => {
				if (!res.ok) throw new Error("linepay/payment-request: NOT 2XX");
				else return res.json();
			}).then(data => {
				//				console.log(data.status);
				if (data.status?.toUpperCase() !== "SUCCESS")
					alert(data.message); // 失敗的 message 是拿來 alert 用戶
				else window.location.href = data.message; // 只有成功的 message 才是拿來轉導
			}).catch(error => {
				console.log(error);
				alert("由於購物車明細過期等狀況，本次 LINE Pay 訂單發送失敗！請重新選擇結帳明細！");
				window.location.href = "/front-end/cart";
			})
		}

		// 2-2. 走 綠界			
		else if (pay_method === "信用卡 - 綠界支付") {

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
					alert("由於購物車明細過期等狀況，本次 綠界金流 訂單發送失敗！請重新選擇結帳明細！");
					window.location.href = "/front-end/cart";
				});
		}
		// eventListener 內部		
	});
	// eventListener 外部
});