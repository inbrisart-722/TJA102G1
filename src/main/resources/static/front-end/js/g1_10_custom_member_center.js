document.addEventListener("DOMContentLoaded", function() {

	let ordersByStatus = {};

	// fetch 1 次而已: 載入用戶所有訂單 => 後續動態呼叫一堆 addEventListener
	fetch("/api/order/getAllOrder", {
		method: "GET"
	}).then((res) => {
		if (!res.ok) throw new Error("NOT OK");
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
			console.log("error");
			console.log(error);
		});

	//  用來開發時調整預設顯示第幾頁使用
	//  new CBPFWTabs(document.getElementById("tabs"), { start: 3 });
	//  document.querySelector('#tabs nav a[href="#section-4"]')?.click();

	// 1. 不用動態渲染任何東西就可以觸發
	document.addEventListener("click", function(e) {
		const btn = e.target.closest("button.tab_order");
		if (!btn) return;
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
	
	// fetch 2 : 重送金流 /api/order/ECPay/resending
	
	document.addEventListener("click", function(e){
		const btn_repay = e.target.closest("button.order_repay")
		if(!btn_repay) return;
		const order_ulid = btn_repay.closest("div.order_row").querySelector("span.order_id > span").innerText;
		console.log(order_ulid);
		fetch("/api/order/ECPay/resending", {
			method: "POST",
			headers: {
				"CONTENT-TYPE": "text/plain" // 純字串
			},
			body: order_ulid
		}).then((res) => {
			if(!res.ok) throw new Error("Resending failed");
			return res.json();
		}).then((result) => {
			if(result.status === "success"){
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
			}else{
				console.log("ECPay Re-sending failed")
			}
		}).catch((error) => {
			console.log("error");
			console.log(error);		
		});

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
		const file = e.target.files[0];
		if (file === null) return;

		const reader = new FileReader();
		reader.addEventListener("load", function() {
			user_img.src = `${reader.result}`;
		});
		reader.readAsDataURL(file);
	});

	// 綁定恢復預設按鈕
	const btn_recover = document.querySelector("#btn_recover");
	const nickname = document.querySelector("#nickname");
	const phone_number = document.querySelector("#phone_number");
	const birth_date = document.querySelector("#birth_date");
	const address = document.querySelector("#address");

	btn_recover.addEventListener("click", function() {
		nickname.value = phone_number.value = birth_date.value = address.value = "";
	});

	// 綁定儲存變更按鈕
	const btn_save = document.querySelector("#btn_save");
	const saved_nickname = document.querySelector("#saved_nickname");
	const saved_phone_number = document.querySelector("#saved_phone_number");
	const saved_birth_date = document.querySelector("#saved_birth_date");
	const saved_address = document.querySelector("#saved_address");
	btn_save.addEventListener("click", function() {
		saved_nickname.innerText = nickname.value;
		saved_phone_number.innerText = phone_number.value;
		saved_birth_date.innerText = birth_date.value;
		saved_address.innerText = address.value;
	});
});



