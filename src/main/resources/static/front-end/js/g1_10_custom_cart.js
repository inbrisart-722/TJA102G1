document.addEventListener("DOMContentLoaded", function () {
  // 2nd part: 計算勾選票卷 把內容取出放到 sticky aside

  // 只計算主要購物車表格（不含逾時清單 .overtime）
  const ROWS_SELECTOR = ".cart-list:not(.overtime) tbody tr";

  function isRowSelected(row) {
    const icon = row.querySelector(".buy_checked i");
    return icon && icon.classList.contains("icon-check");
  }

  function parseIntSafe(text) {
    const n = parseInt(String(text).replace(/[^0-9]/g, ""), 10);
    return isNaN(n) ? 0 : n;
  }

  function updateCartSummary() {
    const rows = document.querySelectorAll(ROWS_SELECTOR);
    let totalQty = 0;
    let totalAmount = 0;

    rows.forEach((row) => {
      if (!isRowSelected(row)) return;

      const subEl = row.querySelector(".ticket_subtotal");

      const amount = parseIntSafe(subEl?.textContent || 0);

      totalQty += 1;
      totalAmount += amount;
    });

    // 右側 aside：第一列=總數量、第二列=總金額（你的現有 HTML 結構）
    const qtyCell = document.querySelector(
      ".table_summary tr:nth-child(1) td.text-end"
    );
    const amtCell = document.querySelector(
      ".table_summary tr:nth-child(2) td.text-end"
    );
    if (qtyCell) qtyCell.textContent = totalQty;
    if (amtCell) amtCell.textContent = `$ ${totalAmount}`;

    toggleCheckoutButton(totalQty > 0);
  }

  function toggleCheckoutButton(enabled) {
    // 你的「立即結帳」按鈕（aside 裡第一個 .btn_full）
    const payBtn =
      document.querySelector('aside .btn_full[href*="payment"]') ||
      document.querySelector("aside .btn_full");
    if (!payBtn) return;

    if (enabled) {
      payBtn.setAttribute("aria-disabled", "false");
      payBtn.style.pointerEvents = "";
      payBtn.style.opacity = "";
    } else {
      payBtn.setAttribute("aria-disabled", "true");
      payBtn.style.pointerEvents = "none";
      payBtn.style.opacity = "0.5";
    }
  }

  // 阻止被停用的結帳按鈕導航
  document.addEventListener("click", (e) => {
    const a = e.target.closest("aside .btn_full[href]");
    if (a && a.getAttribute("aria-disabled") === "true") {
      e.preventDefault();
    }
  });

  // 事件委派：切換勾選 / 釋出
  document.addEventListener("click", (e) => {
    // 勾選切換
    const toggleBtn = e.target.closest(".buy_checked");
    if (toggleBtn) {
      const icon = toggleBtn.querySelector("i");
      if (icon) {
        icon.classList.toggle("icon-check");
        icon.classList.toggle("icon-check-empty");
        updateCartSummary();
      }
      return;
    }

    // 釋出（刪除該列）
    // const releaseBtn = e.target.closest(".buy_release");
    // if (releaseBtn) {
    //   const row = releaseBtn.closest("tr");
    //   if (row) row.remove();
    //   updateCartSummary();
    //   return;
    // }
  });

  // 若其他程式會改動數量/小計，可呼叫 window.updateCartSummary() 重新計算
  window.updateCartSummary = updateCartSummary;

  // // 3rd: 處理逾時清單 放回購物車
  // // 放回購物車：把 .overtime 表格的列搬回主購物車表格
  // document.addEventListener("click", (e) => {
  //   const putBackBtn = e.target.closest(".buy_putback");
  //   if (!putBackBtn) return;

  //   const overtimeRow = putBackBtn.closest("tr");
  //   if (!overtimeRow) return;

  //   // 主要購物車（不是 .overtime 那張）
  //   const mainTbody = document.querySelector(".cart-list:not(.overtime) tbody");
  //   if (!mainTbody) return;

  //   // 複製這一列，並把動作欄改成主清單的按鈕們
  //   const newRow = overtimeRow.cloneNode(true);

  //   // 重置保留時間
  //   newRow.querySelector("span.ticket_left_time").innerHTML = "30:00";

  //   // 找到/建立動作欄
  //   let actionsTd = newRow.querySelector("td.actions");
  //   if (!actionsTd) {
  //     actionsTd = document.createElement("td");
  //     actionsTd.className = "actions";
  //     newRow.appendChild(actionsTd);
  //   }

  //   // 主清單需要的按鈕（✔/□切換 與 釋出）。預設勾選為「結帳」可自行改為 icon-check-empty
  //   actionsTd.innerHTML = `
  //   <button class="buy_checked">
  //                     <i class="icon-check"></i></button
  //                   ><button class="buy_release">
  //                     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;釋出
  //                   </button>
  // `;

  //   // 插入主清單並移除逾時清單的原列
  //   mainTbody.appendChild(newRow);
  //   overtimeRow.remove();

  //   // 重新計算總數量/總金額（若你前面有那支函式）
  //   if (typeof window.updateCartSummary === "function") {
  //     window.updateCartSummary();
  //   }
  // });
  // 以下為清空購物車 modal 操作
  const clearCartBtn = document.querySelector("button#clear_cart");
  const overlay = document.getElementById("modal_overlay");
  const modal = document.getElementById("clear_cart_modal");
  const closeBtn = document.getElementById("close_modal_btn");
  const cancelBtn = document.getElementById("cancel_btn");
  const clearBtn = document.getElementById("clear_btn");

  // 開啟 modal
  function openModal() {
    overlay.style.display = "flex"; // 顯示遮罩
    setTimeout(() => {
      modal.classList.add("show"); // 加入動畫 class
    }, 10); // 延遲一點點觸發動畫
  }

  // 關閉 modal
  function closeModal() {
    modal.classList.remove("show"); // 移除動畫 class
    setTimeout(() => {
      overlay.style.display = "none"; // 動畫跑完才隱藏
    }, 300); // 300ms 對應 CSS transition
  }

  // 綁定事件
  clearCartBtn.addEventListener("click", openModal);
  closeBtn.addEventListener("click", closeModal);
  cancelBtn.addEventListener("click", closeModal);
  clearBtn.addEventListener("click", () => {
    console.log("購物車已清空！"); // TODO: 呼叫清空購物車 API
    closeModal();
  });
  window.addEventListener("click", (e) => {
    if (e.target === overlay) closeModal(); // 點擊遮罩關閉
  });
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") closeModal(); // 按 ESC 關閉
  });
});

// MM:SS => seconds
function parseTime(str) {
  const [mm, ss] = str.split(":").map(Number);
  return mm * 60 + ss;
}

// seconds 轉回 MM:SS 格式
function formatTime(seconds) {
  if (seconds < 0) seconds = 0;
  const mm = String(Math.floor(seconds / 60)).padStart(2, "0");
  const ss = String(seconds % 60).padStart(2, "0");
  return `${mm}:${ss}`;
}

document.addEventListener("DOMContentLoaded", function () {
  // 取得所有購物車明細
  // fetch("http://localhost:8081/eventra/api/cartItem/deleteAll");
  fetch("http://localhost:8088/api/cartItem/getAllCartItem", {
    method: "GET",
    headers: {
      "CONTENT-TYPE": "application/json",
    },
  })
    .then((res) => {
      if (!res.ok) throw new Error("NOT OK");
      return res.json();
    })
    .then((result) => {
      console.log(result);

      // 沒有 result 的時候要給一個索引，導去逛展覽 !!!!!!!!

      const tbody_el = document.getElementById("tbody_to_insert");
      result.forEach((item) => {
        console.log(item);
        // {cartItemId: 4, exhibitionName: '紙上建築設計展', ticketTypeName: '敬老票', quantity: 1, price: 180, createdAt: '24:58'}
        let tr = document.createElement("tr");
        tr.dataset.cartItemId = item.cartItemId;
        tr.className = `cart_item_row`;
        tr.innerHTML = ` <td>
                    <div class="thumb_cart img_block">
                      <img
                        src="img/0_exhibition/ChatGPT_exhibition_1.png"
                        alt="推薦圖片"
                      />
                    </div>
                    <span class="item_cart">${item.exhibitionName}</span>
                  </td>
                  <td>
                    <span class="ticket_type">${item.ticketTypeName}</span>
                  </td>
                  <td><span class="ticket_time_left">${
                    item.createdAt
                  }</span></td>
                  <td class="qty"><span class="quantity">${
                    item.quantity
                  }</span></td>
                  <td><span class="ticket_subtotal">$ ${
                    item.price * item.quantity
                  }</span></td>
                  <td class="actions">
                    <!-- icon-check / icon-check-empty -->
                    <button class="buy_checked">
                      <i class="icon-check"></i></button
                    ><button class="buy_release">
                      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;釋出
                    </button>
                  </td>`;
        tbody_el.insertAdjacentElement("afterbegin", tr);

        let remaining = parseTime(item.expirationTime);
        const span_time_left = tr.querySelector("span.ticket_time_left");
        // 每秒更新
        const intervalId = setInterval(() => {
          remaining--;

          span_time_left.innerText = formatTime(remaining);

          if (remaining <= 0) {
            clearInterval(intervalId);
            // 這裡可以觸發 "倒數結束" 的事件，例如 disable 按鈕 / 自動清空
            span_time_left.innerText = "過期";
          }
        }, 1000);
      });

      // 初始執行
      updateCartSummary();
    })
    .catch((error) => {
      console.log("error");
      console.log(error);
    });

  // 量測高度 → 設置 CSS 變數 → 加 class 觸發動畫 → 動畫結束後移除
  function collapseRow(rowEl, { delay = 0 } = {}) {
    if (!rowEl || rowEl.classList.contains("is-removing")) return;

    // 量測目前實高，寫入 CSS 變數，讓 transition 有起點
    const h = rowEl.getBoundingClientRect().height;
    rowEl.style.setProperty("--row-h", h + "px");

    // 下一個 frame 才加 class，確保瀏覽器先套用起始高度
    setTimeout(() => {
      rowEl.classList.add("is-removing");

      let removed = false;
      // 以 transitionend 為主，確保動畫結束才移除 DOM
      rowEl.addEventListener(
        "transitionend",
        () => {
          if (removed) return;
          removed = true;
          rowEl.remove();
        },
        { once: true }
      );

      // 備援：就算 transitionend 沒觸發，也會在 500ms 後保底移除
      setTimeout(() => {
        if (removed) return;
        removed = true;
        rowEl.remove();
      }, 500);
    }, delay);
  }

  // 刪除（釋出）單筆購物車明細
  document.addEventListener("click", function (e) {
    const btn_buy_release = e.target.closest("button.buy_release");
    if (!btn_buy_release) return;
    const cart_item_id = btn_buy_release.closest("tr").dataset.cartItemId;
    console.log(cart_item_id);

    fetch(
      "http://localhost:8088/api/cartItem/removeOneCartItem?cartItemId=" +
        cart_item_id,
      {
        method: "DELETE",
      }
    )
      .then((res) => {
        if (!res.ok) throw new Error("NOT OK");
        return res.text();
      })
      .then((result) => {
        if (result === "success") {
          console.log("removeOneCartItem " + cart_item_id + ": success");
          const row = btn_buy_release.closest("tr.cart_item_row");
          collapseRow(row);
        } else if (result === "failed")
          console.log("removeOneCartItem " + cart_item_id + ": failed");
      })
      .catch((error) => {
        console.log("error");
        console.log(error);
      });
  });

  // 清空購物車明細
  // fetch("http://localhost:8081/eventra/api/cartItem/deleteAll")
  const btn_clear_cart = document.querySelector("button#clear_btn");
  btn_clear_cart.addEventListener("click", function (e) {
    e.preventDefault();

    fetch("http://localhost:8088/api/cartItem/removeAllCartItem", {
      method: "DELETE",
    })
      .then((res) => {
        if (!res.ok) throw new Error("NOT OK");
        return res.text();
      })
      .then((result) => {
        if (result === "success") {
          console.log("removeAllCartItem success");
          // 1) 清空全部：#clear_btn 觸發，所有 tr.cart_item_row 交錯收合
          const rows = document.querySelectorAll("tr.cart_item_row");
          const stagger = 60; // 交錯間隔 ms；若要同時消失就設 0
          rows.forEach((row, i) => collapseRow(row, { delay: i * stagger }));
        } else if (result === "failed") console.log("removeAllCartItem failed");
      })
      .catch((error) => {
        console.log("error");
        console.log(error);
      });
  });
});
