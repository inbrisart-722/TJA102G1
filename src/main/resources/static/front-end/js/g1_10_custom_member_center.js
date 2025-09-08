document.addEventListener("DOMContentLoaded", function () {
  //  用來開發時調整預設顯示第幾頁使用
  //  new CBPFWTabs(document.getElementById("tabs"), { start: 3 });
  //  document.querySelector('#tabs nav a[href="#section-4"]')?.click();
  // 處理訂單狀態切換頁籤
  const order_type_btns = document.querySelectorAll(
    "nav#tablist_order > button.tab_order"
  );
  order_type_btns.forEach(function (btn) {
    btn.addEventListener("click", function () {
      // 所有 btn 取消 -on
      order_type_btns.forEach((btn) => btn.classList.remove("-on"));
      // 目標 btn 開啟 -on
      this.classList.add("-on");
    });
  });

  // 處理訂單展開畫面
  const order_expand_btns = document.querySelectorAll(
    "div.order_head > button.expand"
  );
  order_expand_btns.forEach(function (btn) {
    btn.addEventListener("click", function () {
      const order_bodys =
        this.closest("div.order_row").querySelectorAll("div.order_body");

      order_bodys.forEach(function (body) {
        body.classList.toggle("-expanded");
      });
      if (btn.classList.contains("-expanded")) {
        btn.classList.remove("-expanded");
        btn.innerHTML = `展開<i class="icon-angle-double-down"></i>`;
      } else {
        btn.classList.add("-expanded");
        btn.innerHTML = `收起<i class="icon-angle-double-up"></i>`;
      }
    });
  });

  // 處理 QR Code 產生畫面（搭配 qrcanvas.js 第三方 API）

  // 套件官網：https://gera2ld.github.io/qrcanvas/docs/
  // Examples：https://gera2ld.github.io/qrcanvas/examples/#logo
  const get_qrcode_btns = document.querySelectorAll(
    "div.order_item_ticket > button.qrcode"
  );
  const qrcode_modal = document.getElementById("qrcode_modal");
  const qrcode_content = document.getElementById("modal-content");

  // 還沒處理實際要產生 qr code 的傳遞數據細節
  get_qrcode_btns.forEach(function (btn) {
    btn.addEventListener("click", function () {
      if (qrcode_modal.classList.contains("open")) return;
      const qr_img = new Image();
      qr_img.src =
        "https://user-images.githubusercontent.com/3139113/38300650-ed2c25c4-382f-11e8-9792-d46987eb17d1.png";

      qr_img.addEventListener("load", () => {
        const canvas = qrcanvas.qrcanvas({
          cellSize: 8,
          correctLevel: "H",
          data: "https://www.opentix.life/event/1925501807699976193",
          logo: {
            qr_img,
            // image: image 可以簡寫，只寫一次
          },
        });
        qrcode_content.appendChild(canvas);
        // 補充設定 canvas 的 aria 屬性以增進無障礙性
        canvas.setAttribute("role", "img");
        canvas.setAttribute("aria-label", "QR Code 圖片，請掃描以開啟活動連結");
      });
      qrcode_modal.classList.add("open");
      qrcode_modal.classList.remove("fade");
      qrcode_content.style.display = "flex";
    });
  });

  // 關閉 qr code
  const close_qrcode_btn = document.querySelector(
    "div#modal-content button.close-modal-btn"
  );
  close_qrcode_btn.addEventListener("click", function () {
    qrcode_modal.classList.remove("open");
    qrcode_modal.classList.add("fade");
    qrcode_content.querySelector("canvas").remove();
  });

  // 處理 明細 modal 收合
  const order_details_btns = document.querySelectorAll(
    "div.order_head > button.detail"
  );
});

////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
// 基本資料
////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////

document.addEventListener("DOMContentLoaded", function () {
  const user_img = document.querySelector("#user_img");
  const file_input = document.querySelector("#js-upload-files");
  const file_input_btn = document.querySelector("#js-upload-submit");

  file_input_btn.addEventListener("click", () => file_input.click());

  // 綁定更新頭像 file_input
  file_input.addEventListener("change", function (e) {
    const file = e.target.files[0];
    if (file === null) return;

    const reader = new FileReader();
    reader.addEventListener("load", function () {
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

  btn_recover.addEventListener("click", function () {
    nickname.value = phone_number.value = birth_date.value = address.value = "";
  });

  // 綁定儲存變更按鈕
  const btn_save = document.querySelector("#btn_save");
  const saved_nickname = document.querySelector("#saved_nickname");
  const saved_phone_number = document.querySelector("#saved_phone_number");
  const saved_birth_date = document.querySelector("#saved_birth_date");
  const saved_address = document.querySelector("#saved_address");
  btn_save.addEventListener("click", function () {
    saved_nickname.innerText = nickname.value;
    saved_phone_number.innerText = phone_number.value;
    saved_birth_date.innerText = birth_date.value;
    saved_address.innerText = address.value;
  });
});
