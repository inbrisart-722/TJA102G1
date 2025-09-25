// instead of setTimeout() short polling, use long polling 30s here
const fetch_order_result = function() {
	const params = new URLSearchParams(window.location.search);
	const merchantTradeNo = params.get("merchantTradeNo");
	console.log(merchantTradeNo);

	csrfFetch(
		"/api/front-end/protected/order/checkOrderStatus?merchantTradeNo=" +
		merchantTradeNo,
		{
			method: "GET",
		}
	)
		.then((res) => {
			if (res.status === 401) {
				sessionStorage.setItem("redirect", window.location.pathname);
				location.href = "/front-end/login";
			}
			if (!res.ok) throw new Error("checkOrderStatus: Not 2XX or 401");
			return res.json();
		})
		.then((result) => {
			// ResponseEntity<Map<String, String>>
			const status = result.orderStatus;
			console.log(status);
			if (status === "已付款") {
				location.href =
					"/front-end/order_success?merchantTradeNo=" +
					merchantTradeNo;
			}
			else if (status === "付款失敗" && status === "付款逾時") {
				location.href =
					"/front-end/order_failure?merchantTradeNo=" +
					merchantTradeNo;
			}
			// ReturnURL 都沒回應的措施
			else {
				console.log("30秒都沒更新，看來該做點什麼了");
				alert("目前訂單狀態正在查詢中，請您稍後至「會員中心 - 訂單頁」查看狀態");
				location.href = "/front-end/admin";
			}
		})
		.catch((error) => {
			console.log("error");
			console.log(error);
		});
};

document.addEventListener("DOMContentLoaded", function() {
	setTimeout(fetch_order_result, 5000);
});

///////////////////////////////////////////////////////////////
// short polling 短輪詢 實現 by setTimeout （不像setInterval 可能會累積 Queue）
///////////////////////////////////////////////////////////////

// const start = Date.now();
// const deadline = 30000; // 30秒
// async function poll() {
//   const r = await fetch(
//     "http://localhost:8088/api/order/checkOrderStatus?merchantTradeNo=" +
//       "merchantTradeNo"
//   );
//   const { status } = await r.json();
//   if (status === "已付款")
//     return window.location.replace(
//       "http://localhost:8088/front-end/order_success?merchantTradeNo=" +
//         "merchantTradeNo"
//     );
//   if (status === "付款失敗（未逾時）")
//     return window.location.replace(
//       "http://localhost:8088/front-end/order_failure?merchantTradeNo=" +
//         "merchantTradeNo"
//     );
//   if (Date.now() - start > deadline) {
//     alert("訂單狀態確認逾時，請至會員中心查看訂單狀態！");
//     // "http://localhost:8088/front-end/order_failure?merchantTradeNo=" +
//     //   "merchantTradeNo";
//   }
//   setTimeout(poll, 2000);
// }
// poll();
