// instead of setTimeout() short polling, use long polling 30s here
const fetch_order_result = function () {
  const params = new URLSearchParams(window.location.search);
  const merchantTradeNo = params.get("merchantTradeNo");
  console.log(merchantTradeNo);

  fetch(
    "http://localhost:8088/api/order/checkOrderStatus?merchantTradeNo=" +
      merchantTradeNo,
    {
      method: "GET",
    }
  )
    .then((res) => {
      if (!res.ok) throw new Error("NOT OK");
      return res.json();
    })
    .then((result) => {
      // ResponseEntity<Map<String, String>>
      const status = result.orderStatus;
      console.log(status);
      if (status === "已付款") {
        console.log(1);
        location.href =
          "http://localhost:8088/front-end/order_success?merchantTradeNo=" +
          merchantTradeNo;
      }
      if (status === "付款失敗（未逾期）") {
        console.log(2);
        location.href =
          "http://localhost:8088/front-end/order_failure?merchantTradeNo=" +
          merchantTradeNo;
      }
      console.log("30秒都沒更新，看來該做點什麼了");
    })
    .catch((error) => {
      console.log("error");
      console.log(error);
    });
};

document.addEventListener("DOMContentLoaded", function () {
  //   let interval = setInterval();
  //   fetch;
  fetch_order_result();
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
