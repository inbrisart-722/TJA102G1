document.addEventListener("DOMContentLoaded", function() {

	//	document.querySelector("a.icon-profile").click();

	// 登出 這個特別用把事件冒泡改 capturing 套用同架構但避免 tabs.js 先取並且丟錯誤（沒section-5 等等）

	document.querySelector("#logout").addEventListener("click", function(e) {
		e.preventDefault();
		e.stopPropagation(); // 阻止傳到 tabs.js

		const wants_to_logout = confirm("確認登出嗎？");

		if (!wants_to_logout) return;

		// 免 csrf 有放行
		fetch("/api/auth/logout/member", {
			method: "POST",
			credentials: "include",
		})
			.then((res) => {
				if (!res.ok) throw new Error("Logout failed");
				location.href = "/front-end/login";
			})
			.catch((error) => {
				console.log(error);
			});

	}, true);

	let ordersByStatus = {};

	// fetch 1 次而已: 載入用戶所有訂單 => 後續動態呼叫一堆 addEventListener
	csrfFetchToRedirect("/api/front-end/protected/order/getAllOrder", {
		method: "GET"
	}).then((res) => {
		if (!res.ok) throw new Error("getAllOrder: Not 2XX or 401");
		return res.json();
	})
		.then((orders) => {
			ordersByStatus = {  // ALL, PAYING, FAILED, EXPIRED, PAID, REFUNDED;
				"全部": orders,
				"付款中": orders.filter(order => order.orderStatus === "付款中"),
				"付款失敗": orders.filter(order => order.orderStatus === "付款失敗"),
				"付款逾時": orders.filter(order => order.orderStatus === "付款逾時"),
				"已付款": orders.filter(order => order.orderStatus === "已付款"),
				"已退款": orders.filter(order => order.orderStatus === "已退款"),
			}
			const btn_default = document.querySelector("button.tab_order:nth-child(1)");
			btn_default.click();
		})
		.catch((error) => {
			console.log(error);
		});

	//  用來開發時調整預設顯示第幾頁使用
	//  new CBPFWTabs(document.getElementById("tabs"), { start: 3 });
	//  document.querySelector('#tabs nav a[href="#section-4"]')?.click();

	// 1. 不用動態渲染任何東西就可以觸發
	document.addEventListener("click", function(e) {
		const btn = e.target.closest("button.tab_order");
		if (!btn) return;
		e.preventDefault();

		console.log(btn.textContent + ": btn is clicked");
		clear_section1();
		// 所有 btn 取消 -on
		btn.classList.remove("-on");
		document.querySelectorAll("button.tab_order")
			.forEach(b => {
				b.classList.remove("-on")
				if (b.className === btn.className) b.classList.add("-on");
			});
		// 目標 btn 開啟 -on
		btn.classList.add("-on");
		// 清空頁面內容

		// 塞入 符合 tab 訂單狀態 的訂單
		insert_order_by_status(btn.textContent);
		// 啟動 訂單可展開
		// 啟動 訂單可拿 qrcode
	});

	// 1-2. 給 activate_order_type_btns 呼叫的，把內容清空，準備塞入對應訂單狀態內容
	const clear_section1 = function() {
		const section1 = document.querySelector("#section-1");
		section1.innerHTML = `		<nav id="tablist_order" class="col-12">
		  <button class="tab_order tab1">全部</button>
		  <button class="tab_order tab2">付款中</button>
		  <button class="tab_order tab3" style="background-color: #CE0000; color:white">付款失敗</button>
		  <button class="tab_order tab4">付款逾時</button>
		  <button class="tab_order tab5">已付款</button>
		  <button class="tab_order tab6">已退款</button>
		</nav>`;
	}

	// 改事件委派
	// 1-3. 給 activate_order_type_btns 呼叫，塞入不同訂單狀態內容
	const insert_order_by_status = function(status) {
		console.log(status + ": inserting orders...");
		const section1 = document.querySelector("#section-1");
		ordersByStatus[status].forEach((order) => {
			const order_row_el = document.createElement("div");
			order_row_el.className = "order_row col-12";
			const order_head_el = document.createElement("div");
			order_head_el.className = "order_head";
			order_head_el.innerHTML = `
				  <span class="order_id">訂單編號：<span>${order.orderUlid}</span></span>
				  <span class="order_qty">數量：<span>${order.totalQuantity}</span></span>
				  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				  <span class="order_amount">總金額：<span>${order.totalAmount}</span></span>
				  <span class="order_status">${order.orderStatus}</span>
				  <button class="expand">
				    展開<i class="icon-angle-double-down"></i>
				  </button>
				`;
			order_row_el.appendChild(order_head_el);
			////////////////////////////////////////////////////////////////
			// .groups
			order.groups.forEach((group) => {
				const order_body_el = document.createElement("div");
				order_body_el.className = "order_body"; // -expanded
				// .groups.exhibitionDTO
				if (order.orderStatus === "付款失敗") {
					const order_repay_btn_el = document.createElement("button");
					order_repay_btn_el.className = "order_repay";
					order_repay_btn_el.innerHTML = `再次嘗試付款`;
					order_body_el.appendChild(order_repay_btn_el);
				}
				const order_item_head_el = document.createElement("div");
				order_item_head_el.className = "order_item_head";
				order_item_head_el.innerHTML = ` 
				<img
				  src="img/0_exhibition/ChatGPT_exhibition_1.png"
				  alt="推薦圖片"
				/>
				<div>
				  <span><strong>${group.exhibitionDTO.exhibitionName}</strong></span>
				  <span>
				    ${group.exhibitionDTO.location}<span
				      >${group.exhibitionDTO.startTime.substring(0, 10) + " ~ " + group.exhibitionDTO.endTime.substring(0, 10)}</span
				    ></span
				  >
				</div>
				`;
				order_body_el.appendChild(order_item_head_el);
				// .groups.orderItemsDTO (list)
				group.orderItemsDTO.forEach(item => {
					const order_item_body_el = document.createElement("div");
					order_item_body_el.className = "order_item_body";
					order_item_body_el.innerHTML = `
					<div class="order_item_ticket">
					  <span>訂單明細編號：<span>${item.orderItemUlid}</span></span>
					  <span>${item.ticketTypeName}</span>
					  ${order.orderStatus === '已付款' ? `<span class="qrcode_id">${item.ticketCode}
					  </span><button class="qrcode">取得入場 QR Code</button>` : ''}
					</div>
					`;
					order_body_el.appendChild(order_item_body_el);
				})
				order_row_el.appendChild(order_body_el);
			})
			section1.appendChild(order_row_el);
		})
		// 把第一個預設展開！
		section1.querySelector("div.order_row > div.order_head > button.expand").click();
	}

	// 改事件委派
	// 2. 處理訂單展開畫面（事件委派）
	// 只需註冊一次，之後動態新增的按鈕也能用
	document.addEventListener("click", function(e) {
		const btn = e.target.closest("div.order_head > button.expand");
		if (!btn) return;
		const row = btn.closest("div.order_row");
		if (!row) return;
		const bodies = row.querySelectorAll("div.order_body");
		bodies.forEach((body) => body.classList.toggle("-expanded"));

		if (btn.classList.contains("-expanded")) {
			btn.classList.remove("-expanded");
			btn.innerHTML = `展開<i class="icon-angle-double-down"></i>`;
		} else {
			btn.classList.add("-expanded");
			btn.innerHTML = `收起<i class="icon-angle-double-up"></i>`;
		}
	});


	// 改事件委派
	// 3. 處理 QR Code 產生畫面（搭配 qrcanvas.js 第三方 API）（給 1 呼叫）
	// 套件官網：https://gera2ld.github.io/qrcanvas/docs/
	// Examples：https://gera2ld.github.io/qrcanvas/examples/#logo
	// 3. 處理 QR Code 產生畫面（事件委派）
	const qrcode_modal = document.getElementById("qrcode_modal");
	const qrcode_content = document.getElementById("modal-content");

	// 產生 QR Code（點票券上的按鈕）
	document.addEventListener("click", function(e) {
		const btn = e.target.closest("div.order_item_ticket > button.qrcode");
		if (!btn) return;
		e.preventDefault();

		if (qrcode_modal.classList.contains("open")) return;

		const qr_img = new Image();
		qr_img.src =
			"https://user-images.githubusercontent.com/3139113/38300650-ed2c25c4-382f-11e8-9792-d46987eb17d1.png";

		qr_img.addEventListener("load", () => {
			const canvas = qrcanvas.qrcanvas({
				cellSize: 8,
				correctLevel: "H",
				data: "https://www.opentix.life/event/1925501807699976193",
				logo: { qr_img },
			});
			qrcode_content.appendChild(canvas);
			canvas.setAttribute("role", "img");
			canvas.setAttribute("aria-label", "QR Code 圖片，請掃描以開啟活動連結");
		});

		qrcode_modal.classList.add("open");
		qrcode_modal.classList.remove("fade");
		qrcode_content.style.display = "flex";
	});

	// 關閉 QR Code（關閉鈕）
	document.addEventListener("click", function(e) {
		const closeBtn = e.target.closest("div#modal-content button.close-modal-btn");
		if (!closeBtn) return;

		qrcode_modal.classList.remove("open");
		qrcode_modal.classList.add("fade");
		const canvas = qrcode_content.querySelector("canvas");
		if (canvas) canvas.remove();
	});

	

	document.addEventListener("click", function(e) {
		const btn_repay = e.target.closest("button.order_repay")
		if (!btn_repay) return;
		const order_ulid = btn_repay.closest("div.order_row").querySelector("span.order_id > span").innerText;
		console.log(order_ulid);
		
		// fetch1 : 先判斷此訂單本來是哪個金流服務提供商
		csrfFetchToRedirect("/api/front-end/protected/order/getOrderProvider?orderUlid=" + order_ulid, {
			method: "GET"
		})
			.then(res => {
				if (!res.ok) throw new Error("getOrderProvider: NOT 2XX or 401")
				else return res.json();
			})
			.then(data => {
				// fetch2 : 重送金流 /api/order/ECPay/resending       ||       /api/front-end/protected/linepay/resending-payment-request
				console.log(data);
				if (data === "ECPay") ECPay_resending(order_ulid);
				else if (data === "LINEPay") LINEPay_resending(order_ulid);
		})
			.catch(error => console.log(error));

		// 以下 function 目的是為了副作用，就先不另外處理 promise 結果部分了
			
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		const LINEPay_resending = function(order_ulid) {
			return csrfFetchToRedirect("/api/front-end/protected/linepay/resending-payment-request", {
				method: "POST",
				headers:  {
					"Content-Type": "text/plain" // 純字串
				},
				body: order_ulid
			}).then((res) => {
					if (!res.ok) throw new Error("linepay/resending-payment-request: Not 2XX or 401");
					return res.json();
			}).then((data) => {
				//	console.log(data.status);
					if(data.status?.toUpperCase() !== "SUCCESS")
						alert(data.message); // 失敗的 message 是拿來 alert 用戶
					else window.location.href = data.message; // 只有成功的 message 才是拿來轉導
			}).catch(error => {
				console.log(error);
			})
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		const ECPay_resending = function(order_ulid) {
			return csrfFetchToRedirect("/api/front-end/protected/order/ECPay/resending", {
				method: "POST",
				headers: {
					"CONTENT-TYPE": "text/plain" // 純字串
				},
				body: order_ulid
			}).then((res) => {
				if (!res.ok) throw new Error("ECPay/resending: Not 2XX or 401");
				return res.json();
			}).then((result) => {
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
					console.log("ECPay Re-sending failed")
				}
			}).catch((error) => {
				console.log(error);
			});
		}


	})





});

////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
// 基本資料
////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////

document.addEventListener("DOMContentLoaded", function() {
	const user_img = document.querySelector("#user_img");
	const file_input = document.querySelector("#js-upload-files");
	const file_input_btn = document.querySelector("#js-upload-submit");

	file_input_btn.addEventListener("click", () => file_input.click());

	// 綁定更新頭像 file_input
	file_input.addEventListener("change", function(e) {
		const user_photo = e.target.files[0];
		if (user_photo === null) return;

		const form_data = new FormData();
		form_data.append("user_photo", user_photo);

		csrfFetchToRedirect("/api/front-end/protected/member/update-photo", {
			method: "POST",
			body: form_data,
		})
			.then((res) => {
				if (!res.ok) throw new Error("Not 2XX or 401");
				else return res.json();
			})
			.then((result) => {
				console.log(result[0]);
				console.log(result[1]);

				user_img.src = `${result[1]}`;
			})
			.catch(error => console.log(error));
		//		const reader = new FileReader();
		//		reader.addEventListener("load", function() {
		//			user_img.src = `${reader.result}`;
		//		});
		//		reader.readAsDataURL(file);
	});

	///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////

	// 只驗證其中四項
	function validateForm() {
		const full_name = document.querySelector("#full_name").value.trim();
		const nickname = document.querySelector("#nickname").value.trim();
		const phone_number = document.querySelector("#phone_number").value.trim();
		const address = document.querySelector("#address").value.trim();

		let errors = {};


		// 0. full_name: 長度 <= 50
		if (full_name.length > 50) {
			errors.full_name = "姓名不能超過 50 個字元";
		}

		// 1. nickname：必填 + 長度 <= 50
		if (!nickname) {
			errors.nickname = "平台暱稱必填";
		} else if (nickname.length > 50) {
			errors.nickname = "平台暱稱不能超過 50 個字元";
		}

		// 2. phone_number：可選填，但若填必須符合台灣電話格式
		const taiwanPhoneRegex = /^(09\d{8}|0\d{1,2}\d{6,8})$/;
		if (phone_number) {
			if (!taiwanPhoneRegex.test(phone_number)) {
				errors.phone_number = "電話格式必須為台灣電話（09xxxxxxxx 或 市話）";
			}
			if (phone_number.length > 15) {
				errors.phone_number = "電話號碼不能超過 15 碼";
			}
		}

		// 3. address：可選填，但長度 <= 255
		if (address && address.length > 255) {
			errors.address = "地址不能超過 255 個字元";
		}

		return errors;
	}

	function showErrors(errors) {
		// 先清除舊的錯誤訊息
		document.querySelectorAll(".error-msg").forEach(el => el.remove());

		for (const [field, message] of Object.entries(errors)) {
			const input = document.querySelector(`#${field}`);
			const formGroup = input.closest(".form-group");

			// 在該 input 下方加錯誤提示
			const small = document.createElement("small");
			small.classList.add("text-danger", "error-msg");
			small.innerText = message;
			formGroup.appendChild(small);
		}
	}

	// 選取所有可以 update 項目
	const full_name = document.querySelector("#full_name");
	const nickname = document.querySelector("#nickname");
	const gender = document.querySelector("#gender");
	const phone_number = document.querySelector("#phone_number");
	const birth_date = document.querySelector("#birth_date");
	const address = document.querySelector("#address");

	// 綁定恢復預設按鈕
	const btn_recover = document.querySelector("#btn_recover");

	btn_recover.addEventListener("click", function() {
		full_name.value = nickname.value = gender.value = phone_number.value = birth_date.value = address.value = "";
		document.querySelectorAll(".error-msg").forEach(el => el.remove());
	});

	// 綁定儲存變更按鈕
	const btn_save = document.querySelector("#btn_save");

	const saved_full_name = document.querySelector("#saved_full_name");
	const saved_nickname = document.querySelector("#saved_nickname");
	const saved_gender = document.querySelector("#saved_gender");
	const saved_phone_number = document.querySelector("#saved_phone_number");
	const saved_birth_date = document.querySelector("#saved_birth_date");
	const saved_address = document.querySelector("#saved_address");

	let saveable = true;
	btn_save.addEventListener("click", function(e) {
		e.preventDefault();

		const errors = validateForm();

		if (Object.keys(errors).length > 0) {
			showErrors(errors);
			console.log("驗證失敗:", errors);
			return;
		} else {
			console.log("驗證成功，可以送出！");
		}

		const form_data = new FormData();
		form_data.append("fullName", full_name.value);
		form_data.append("nickname", nickname.value);
		form_data.append("gender", gender.value);
		form_data.append("phoneNumber", phone_number.value);
		form_data.append("birthDate", birth_date.value);
		form_data.append("address", address.value);

		saveable = false;
		csrfFetchToRedirect("/api/front-end/protected/member/update-info", {
			method: "POST",
			// FormData 不能搭配自己手動設定 Content-Type
			// 也可採用 application/x-www-form-urlencoded + URLSearchParams();
			body: form_data
		})
			.then((res) => {
				if (!res.ok) throw new Error("Not 2XX or 401");
				else return res.text();
			})
			.then((result) => {
				setTimeout(() => {
					console.log(result);
					saved_full_name.innerText = full_name.value;
					saved_nickname.innerText = nickname.value;
					saved_gender.innerText = gender.value;
					saved_phone_number.innerText = phone_number.value;
					saved_birth_date.innerText = birth_date.value;
					saved_address.innerText = address.value;
					sendable = true;
				}, 500);
			})
			.catch(error => {
				console.log(error)
				sendable = true;
			});
	});
});

// 取得基本資料區塊
document.addEventListener("DOMContentLoaded", function() {

	const profile_summary = document.getElementById("profile_summary");
	const related_account = document.getElementById("related_account");
	// saved 區塊
	let saved_email_el;
	let saved_password_el;
	const saved_full_name_el = document.getElementById("saved_full_name");
	const saved_nickname_el = document.getElementById("saved_nickname");
	const saved_gender_el = document.getElementById("saved_gender");
	const saved_phone_number_el = document.getElementById("saved_phone_number");
	const saved_birth_date_el = document.getElementById("saved_birth_date");
	const saved_address_el = document.getElementById("saved_address");
	// 圖片（user_img)
	const user_img = document.querySelector("#user_img");
	// update 區塊
	const full_name_el = document.querySelector("#full_name");
	const nickname_el = document.querySelector("#nickname");
	const gender_el = document.querySelector("#gender");
	const phone_number_el = document.querySelector("#phone_number");
	const birth_date_el = document.querySelector("#birth_date");
	const address_el = document.querySelector("#address");

	const insert_if_oauth2 = function(oauth2provider) {
		related_account.insertAdjacentHTML("afterbegin", `此帳號透過 <span>${oauth2provider}</span> 綁定`);
	}
	//	<h4 id="related_account">此帳號透過 <span>${oauth2provider}</span> 綁定</h4>
	//			<br />
	// 如果判斷不是 OAuth2 才需要加入的部分
	const insert_if_not_oauth2 = function() {
		// 1. saved 部分要加入信箱密碼

		profile_summary.insertAdjacentHTML("afterbegin", `		<li>信箱 <span id="saved_email"></span>
		</li>
		<li>密碼 <span id="saved_password"></span>
		</li>`);
		// 2. update 部分要加入信箱密碼
		const div_if_not_oauth2 = document.querySelector("div#if_not_oauth2");
		div_if_not_oauth2.insertAdjacentHTML("beforeend", `<div class="col-md-6">
									<div class="form-group">
										<label>信箱</label>
										<div class="btn_change_div">
											<button id="change_mail">更改會員信箱</button>
										</div>
									</div>
								</div>
								<div class="col-md-6">
									<div class="form-group">
										<label>密碼</label>
										<div class="btn_change_div">
											<button id="change_password">重設密碼</button>
										</div>
									</div>
								</div>`)

		saved_email_el = document.getElementById("saved_email");
		saved_password_el = document.getElementById("saved_password");

		const btn_change_mail_el = document.getElementById("change_mail");
		const btn_change_password_el = document.getElementById("change_password");

		btn_change_mail_el.addEventListener("click", () => window.location.href = "/front-end/change-mail1")
		btn_change_password_el.addEventListener("click", () => window.location.href = "/front-end/reset-password1")
	};

	csrfFetchToRedirect("/api/front-end/protected/member/getMemberInfo", {
		method: "GET"
	})
		.then(res => {
			if (!res.ok) throw new Error("getMemberInfo: NOT 2XX");
			else return res.json();
		})
		.then(result => {
			console.log(result);

			// 如果非使用 OAuth2 登入 -> 才渲染更新框的 (1)信箱 (2)密碼 部分
			if (!result.githubId && !result.facebookId && !result.googleId) {
				insert_if_not_oauth2();
				saved_email_el.innerText = result.email ? result.email : "-";
				saved_password_el.innerText = "(已隱藏)";
			}
			else {
				console.log("i used oauth2 to log in !!");
				// 先渲染非 OAuth2 會員才有的值
				let oauth2provider;

				if (result.githubId) oauth2provider = "GitHub";
				if (result.googleId) oauth2provider = "Google";
				if (result.facebookId) oauth2provider = "Facebook";

				insert_if_oauth2(oauth2provider);
			}

			// saved部分 -> 把 innerText 放上 -> 所有會員（不論是否 OAuth2）都有的值
			saved_full_name_el.innerText = result.fullName ? result.fullName : "-";
			saved_nickname_el.innerText = result.nickname ? result.nickname : "-";
			saved_gender_el.innerText = result.gender ? result.gender : "-";
			saved_phone_number_el.innerText = result.phoneNumber ? result.phoneNumber : "-";
			saved_birth_date_el.innerText = result.birthDate ? result.birthDate : "-";
			saved_address_el.innerText = result.address ? result.address : "-";

			// update部分 -> 把 value 放上 -> 所有會員（不論是否 OAuth2）都有的值
			full_name_el.value = result.fullName ? result.fullName : "";
			nickname_el.value = result.nickname ? result.nickname : ""; // 其實一定有 (not null constraint)
			gender_el.value = result.gender ? result.gender : "";
			phone_number_el.value = result.phoneNumber ? result.phoneNumber : "";
			birth_date_el.value = result.birthDate ? result.birthDate : "";
			address_el.value = result.address ? result.address : "";

			// 圖片部分
			user_img.src = result.profilePic ? result.profilePic : "img/tourist_guide_pic.jpg";
		})
		.catch(error => {
			console.log(error);
		})

});