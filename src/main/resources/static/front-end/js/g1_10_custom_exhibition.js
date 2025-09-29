let my_profile_pic;
// 分開包了很多事件委派（但其實應該包一起）
document.addEventListener("DOMContentLoaded", function() {

	// 馬上打api去取頭像（不重導向）-> 可能會有 bug（取得比留言等處慢的時候 -> then 可解）
	fetch("/api/front-end/protected/member/getMyProfilePic", {
		method: "GET"
	})
	.then(res => {
		if(!res.ok) throw new Error("getMyProfilePic: NOT 2XX");
		else return res.text();
	})
	.then(pic => {
		// 只有 default_pic 或 實際 member_pic 2種狀況
		my_profile_pic = pic;
		// 可以在此處理重新 refresh
		const img = document.querySelector("section.top-reply > form.form_reply > img.replier_self");
		img.src = my_profile_pic;
	})
	.catch(error => {
		console.log(error);
		my_profile_pic = "img/tourist_guide_pic.jpg";
	});
	
	// 父層留言 + 子層回覆 新增 start
	document.addEventListener("click", function(e) {
		const btn_send = e.target.closest("form.form_reply > button");
		if (btn_send) {
			e.preventDefault();
			const input_comment_el = btn_send.closest("form").querySelector("input");
			// 檢驗 value 是否合理
			const comment_value = input_comment_el.value;
			if (comment_value.trim() === "") return; // 防止空內容
			if (comment_value.length >= 200) return; // 防止空字串
			// 缺少防止 html 注入 => 底下 p 標籤有

			// 收集數據
			// 收集數據 -> 1. exhibition_id;
//			const path_name = window.location.pathname;
//			const last_slash_index = path_name.lastIndexOf("/");
//			const exhibition_id = path_name.substring(last_slash_index);
			const params = new URLSearchParams(window.location.search);
			const exhibitionId = params.get("exhibitionId");
			// 收集數據 -> 2. parent_comment_id
			const parent_comment = btn_send.closest("div.review_strip_single");
			let parent_comment_id;
			if (parent_comment)
				parent_comment_id = Number(parent_comment.dataset.commentId);

			// exhibition_id, parent_comment_id, content
			send_data = {
				exhibitionId: Number(exhibitionId), // test
				parentCommentId: parent_comment_id,
				content: comment_value,
			};

			// fetch 1 => 新增父子留言
			csrfFetchToRedirect("/api/front-end/protected/comment/addComment", {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify(send_data),
			})
				.then((res) => {
					if (!res.ok) throw new error("addComment: Not 2XX or 401");
					return res.json();
				})
				.then((result) => {
					console.log(result);
					// result => status, commentVO, commentCount, replyCount

					// 判斷是父層還是子層
					if (btn_send.closest("form").classList.contains("sub")) {
						// 子層
						const reply_body_html = document.createElement("div");
						reply_body_html.className = "reply_body";
						reply_body_html.dataset.commentId = result.comment.commentId;
						// member 圖片、member 暱稱 or 姓名
						reply_body_html.innerHTML = `<!-- 單個回覆 -->
                        <img
                          src="${result.comment.member.profilePic}"
                          alt="Image"
                          class="rounded-circle replier_others"
                        />
                        <h6>${result.comment.member.nickname}</h6>
                        <small>${result.comment.createdAt}</small>
                        <div class="report_block">
                          <button>...</button>
                          <ul>
                            <li>
                              <button class="btn_report">檢舉留言</button>
                            </li>
                          </ul>
                        </div>
                        <button class="btn_like">
                          <i class="icon-thumbs-up"></i>&nbsp;<span>0</span>
                        </button>
                        <button class="btn_dislike">
                          <i class="icon-thumbs-down"></i>&nbsp;<span>0</span>
                        </button>
                      <!-- 單個回覆 end -->`;
						const report_block = reply_body_html.querySelector(".report_block");
						const p = document.createElement("p");
						p.innerText = result.comment.content; // comment_value
						report_block.insertAdjacentElement("afterend", p);
						// 找到插入區塊
						const sub_reply_block = btn_send
							.closest("section.replies")
							.querySelector("article.reply_block");
						sub_reply_block.insertAdjacentElement(
							"afterbegin",
							reply_body_html
						);
					} else {
						// 待插入的整份父層留言 html 按讚評價留言都帶 0 但會員日期等內容要另外抓
						const review_strip_single_html = document.createElement("div");
						review_strip_single_html.className = "review_strip_single";
						review_strip_single_html.dataset.commentId =
							result.comment.commentId; // data-comment-id 設值
						review_strip_single_html.innerHTML = `
                  <!-- 留言 header 部分 -->
                  <img
                    src="${result.comment.member.profilePic}"
                    alt="Image"
                    class="rounded-circle comment-avatar"
                  />
                  <h4>${result.comment.member.nickname}</h4>
                  <small> ${result.comment.createdAt}&nbsp;&nbsp;</small>
                  <div class="report_block">
                    <button>...</button>
                    <ul>
                      <li><button class="btn_report">檢舉留言</button></li>
                    </ul>
                  </div>
                  <!-- 留言 主內容 -->
                  <!-- 留言 footer 部分 -->
                  <footer>
                    <button class="btn_like">
                      <i class="icon-thumbs-up"></i>&nbsp;<span>0</span>
                    </button>
                    <button class="btn_dislike">
                      <i class="icon-thumbs-down"></i>&nbsp;<span>0</span>
                    </button>
                    <button class="btn_reply">
                      回覆&nbsp;<span>&nbsp;0&nbsp;&nbsp;&nbsp;</span
                      ><i class="icon-down-open-big"></i>
                    </button>
                  </footer>
                  <!-- 此留言下面的回覆區塊 -->
                  <section class="replies">
                    <!-- 本人回覆輸入 -->
                    <form action="" class="form_reply sub">
                      <img
                        src="${result.comment.member.profilePic}"
                        alt="Image"
                        class="rounded-circle replier_self"
                      />
                      <input type="text" placeholder="輸入回覆" />
                      <button type="submit">
                        <i class="icon-direction-1"></i>
                      </button>
                    </form>
                    <article class="reply_block">
					<!-- 單個回覆 插入 start -->
					                          <button class="btn_full btn_more_comments_child">
					                          查看更多回覆
					                          </button>
					                          <!-- 單個回覆 插入 end -->
                    </article>
                    </section>`;
						const report_block =
							review_strip_single_html.querySelector(".report_block");
						const p = document.createElement("p");
						p.className = "review_content";
						p.innerText = result.comment.content; // comment_value
						report_block.insertAdjacentElement("afterend", p);
						// 找到插入區塊
						const top_reply = document.querySelector("section.top-reply");
						top_reply.insertAdjacentElement(
							"afterend",
							review_strip_single_html
						);
					}

					// 渲染 子層回覆 總數
					if (parent_comment) {
						const sum_total_replies = btn_send
							.closest("div.review_strip_single")
							.querySelector("button.btn_reply > span");
						sum_total_replies.innerHTML = `&nbsp;${result.replyCount}&nbsp;&nbsp;&nbsp;`;
					}

					// 渲染 父層留言 總數
					const sum_total_comments = document.querySelector(
						"#general_rating > span"
					);
					sum_total_comments.innerText = result.commentCount;

					// 清空 input 並 blur
					input_comment_el.value = "";
					input_comment_el.blur();
				})
				.catch((error) => {
					console.log(error);
				});
		}
	});
	// 父層留言 + 子層回覆 新增 end
	// 展商資訊按鈕 start
	const btn_exhibitor = document.querySelector(".exhibitor-link");
	btn_exhibitor.addEventListener("mouseover", function() {
		btn_exhibitor.style.backgroundColor = "#e1e1e1";
	});
	btn_exhibitor.addEventListener("mouseleave", function() {
		btn_exhibitor.style.backgroundColor = "#efefef";
	});

	const el = document.querySelector("div.search-overlay-menu");
	const search_input = document.querySelector("#searchform input");
	const search_block_el = document.querySelector("#nav_icon_1");
	// 展商資訊按鈕 end

	search_block_el.addEventListener("click", function() {
		el.classList.add("open");
		setTimeout(() => search_input.focus(), 50);
	});

	// 檢舉留言
	document.addEventListener("click", function(e) {
		const trigger = e.target.closest("div.report_block > button");
		if (!trigger) {
			document.querySelectorAll("div.report_block ul").forEach((ul) => {
				ul.style.display = "none";
			});
			return;
		}

		const report_ul = trigger.closest("div.report_block").querySelector("ul");
		report_ul.style.display =
			report_ul.style.display !== "block" ? "block" : "none";
	});

	/* ---- (2) 內頁錨點導覽平滑捲動 ---- */
	document.querySelectorAll(".section_nav a").forEach((a) => {
		a.addEventListener("click", (e) => {
			e.preventDefault();
			const target = document.querySelector(a.getAttribute("href"));
			if (target) {
				target.scrollIntoView({ behavior: "smooth", block: "start" });
			}
		});
	});

	// gpt 收藏
	//  const favBtn = document.querySelector(".btn_fav");
	//  favBtn.addEventListener("click", function () {
	//    this.classList.toggle("active");
	//  });

	// gpt sticky search bar 切換顏色
	const searchInput = document.getElementById("nav_search");
	const sentinel = document.createElement("div");
	sentinel.style.height = "1px";
	document.body.prepend(sentinel);

	const observer = new IntersectionObserver(
		(entries) => {
			if (entries[0].boundingClientRect.y < 0) {
				searchInput.classList.add("is-sticky");
			} else {
				searchInput.classList.remove("is-sticky");
			}
		},
		{ threshold: [1] }
	);

	observer.observe(sentinel);

	// 調整購票 block
	const table_body = document.querySelector("tbody#table_body");
	const button_incs = document.querySelectorAll("div.inc");
	const button_decs = document.querySelectorAll("div.dec");
	button_incs.forEach(function(inc) {
		console.log(inc);
		inc.addEventListener("click", function() {
			console.log(this);
		});
	});

	// 訂票 => 重算總和
	const recalcTotal = function() {
		let sum = 0;
		document.querySelectorAll(".numbers-row-parent").forEach((row) => {
			const q = Number(row.querySelector(".numbers-row input").value);
			const price = Number(row.querySelector("span").innerText.substring(2));
			sum += q * price;
		});
		const totalEl = document.querySelector("tr.total > td.text-end");
		totalEl.innerText = "$" + sum;
	};

	const clearTable = function() {
		document.querySelectorAll("tbody#table_body > tr").forEach((row) => {
			if (row.classList.contains("total")) return;
			const td = row.querySelector("td.text-end");
			td.innerText = 0;
			row.style.display = "none";
		});
	};

	// 訂票
	document.addEventListener("click", (e) => {
		const inc = e.target.closest(".inc");
		const dec = e.target.closest(".dec");
		if (!inc && !dec) return;
		const num_val_el = e.target
			.closest("div.numbers-row")
			.querySelector("input");
		const num_val_el_numVal = Number(num_val_el.value);
		const num_label_el = e.target
			.closest("div.numbers-row-parent")
			.querySelector("label");

		const summary_labels = document.querySelectorAll(
			"#table_body > tr > td.label"
		);

		// 1. 找到目標 td
		let summary_label_el;
		summary_labels.forEach(function(label) {
			if (label.innerText === num_label_el.innerText) summary_label_el = label;
		});
		const summary_tr_el = summary_label_el.closest("tr");
		const summary_val_el = summary_tr_el.querySelector("td.text-end");
		summary_val_el.innerText = num_val_el_numVal;
		// 2. 重新計算總計
		recalcTotal();
		// 3. inc
		if (inc && num_val_el_numVal === 1)
			summary_tr_el.style.display = "table-row";
		// 4. dec
		if (dec && num_val_el_numVal === 0) summary_tr_el.style.display = "none";
	});

	// 處理 載入更多留言、回覆
	document.addEventListener("click", (e) => {
		// 點擊父留言的「查看更多」
		if (e.target.closest(".btn_more_comments_parent")) {
			const btn_more_parent = e.target.closest(".btn_more_comments_parent");
			btn_more_parent.innerText = "載入中...";
			// 收集數據
			// 收集數據 -> 1. exhibition_id;
//			const path_name = window.location.pathname;
//			const last_slash_index = path_name.lastIndexOf("/");
//			const exhibition_id = path_name.substring(last_slash_index);
			const params = new URLSearchParams(window.location.search);
			const exhibitionId = params.get("exhibitionId");
			// 收集數據 -> 2. created_at;
			// const created_at = btn_more_parent.dataset.createdAt;
			// 收集數據 -> 3. comment_id;
			const comment_id = btn_more_parent.dataset.commentId;
			// 收集數據 -> 4. is_parent;

			const send_data = {
				exhibitionId: Number(exhibitionId), // test
				// createdAt: created_at,
				commentId: comment_id,
			};
			csrfFetch("/api/front-end/comment/loadComment", {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify(send_data),
			})
				.then((res) => {
					if (!res.ok) throw new Error("loadComment: Not 2XX");
					return res.json();
				})
				.then((result) => {
					console.log(result);
					// status, message, list, mapReaction, hasNextPage
					const container = btn_more_parent.closest(".col-lg-12"); // 父留言容器
					// let to_created_at;
					let to_comment_id;
					result.list.forEach((c) => {
						console.log(c);
						const div = document.createElement("div");
						div.className = "review_strip_single";
						div.dataset.commentId = c.commentId;

						// 圖片與會員姓名還沒處理 !!!!!!!
						div.innerHTML = `
                      <img
                        src="${c.member.profilePic}"
                        alt="Image"
                        class="rounded-circle comment-avatar"
                      />
                      <h4>${c.member.nickname}</h4>
                      <small> ${c.createdAt}&nbsp;&nbsp;</small>
                      <div class="report_block">
                        <button>...</button>
                        <ul>
                          <li><button class="btn_report">檢舉留言</button></li>
                        </ul>
                      </div>
                      <!-- 留言 主內容 -->
                      <p class="review_content">
                        ${c.content}
                      </p>
                      <!-- 留言 footer 部分 -->
                      <footer>
                        <button class="btn_like">
                          <i class="icon-thumbs-up"></i>&nbsp;<span>${c.likeCount
							}</span>
                        </button>
                        <button class="btn_dislike">
                          <i class="icon-thumbs-down"></i>&nbsp;<span>${c.dislikeCount
							}</span>
                        </button>
                        <button class="btn_reply">
                          回覆&nbsp;<span>&nbsp;${c.childCommentsCount
							}&nbsp;&nbsp;&nbsp;</span
                          ><i class="icon-down-open-big"></i>
                        </button>
                      </footer>
                      <!-- 此留言下面的回覆區塊 -->
                      <section class="replies">
                        <!-- 本人回覆輸入 -->
                        <form action="" class="form_reply sub">
                          <img
                            src="${my_profile_pic}"
                            alt="Image"
                            class="rounded-circle replier_self"
                          />
                          <input type="text" placeholder="輸入回覆" />
                          <button type="submit">
                            <i class="icon-direction-1"></i>
                          </button>
                        </form>
                        <!-- 查看他人回覆列表 -->
                        <article class="reply_block">
                          <!-- 單個回覆 插入 start -->
                          <button class="btn_full btn_more_comments_child">
                          查看更多回覆
                          </button>
                          <!-- 單個回覆 插入 end -->
                        </article>
                      </section>
          `;
						container.insertBefore(div, btn_more_parent); // 插入在「查看更多」之前
						// to_created_at = c.createdAt;
						to_comment_id = c.commentId;

						// 調整 member_id 此人的按讚與倒讚狀況
						const icon_like = div.querySelector("i.icon-thumbs-up");
						const icon_dislike = div.querySelector("i.icon-thumbs-down");

						if(result.status === "member"){
							// undefined / LIKE / DISLIKE
							const member_reaction = result.mapReaction[c.commentId];
							if (member_reaction === "LIKE") icon_like.classList.add("-on");
							else if (member_reaction === "DISLIKE")
								icon_dislike.classList.add("-on");
						}
					});

					btn_more_parent.innerText = "查看更多留言";
					// btn_more_parent.dataset.createdAt = to_created_at;
					btn_more_parent.dataset.commentId = to_comment_id;

					// 如果沒下一頁就處理
					if (!result.hasNextPage) {
						btn_more_parent.innerText = "沒有更多留言了！";
						btn_more_parent.disabled = true;
					}
				})
				.catch((error) => {
					console.log("error");
					console.log(error);
				});
		}

		if (e.target.closest(".btn_more_comments_child")) {
			const btn_more_child = e.target.closest(".btn_more_comments_child");
			btn_more_child.innerText = "載入中...";

			// 收集數據
			// 收集數據 -> 1. exhibition_id;
//			const path_name = window.location.pathname;
//			const last_slash_index = path_name.lastIndexOf("/");
//			const exhibition_id = path_name.substring(last_slash_index);
			const params = new URLSearchParams(window.location.search);
			const exhibitionId = params.get("exhibitionId");
			// 收集數據 -> 2. created_at;
			// const created_at = btn_more_child.dataset.createdAt;
			// 收集數據 -> 3. comment_id;
			const comment_id = btn_more_child.dataset.commentId;
			// 收集數據 -> 4. is_parent;
			const parent_comment_id = btn_more_child.closest(
				"div.review_strip_single"
			).dataset.commentId;

			const send_data = {
				exhibitionId: Number(exhibitionId), // test
				// createdAt: created_at, // cursor 用
				commentId: comment_id, // cursor 用
				parentCommentId: parent_comment_id, // 抓 div.reivew_strip_single 的值
			};
			console.log("send_data");
			console.log(send_data);

			csrfFetch("/api/front-end/comment/loadComment", {
				method: "POST",
				headers: {
					"CONTENT-TYPE": "application/json",
				},
				body: JSON.stringify(send_data),
			})
				.then((res) => {
					if (!res.ok) throw new Error("loadComment: Not 2XX");
					return res.json();
				})
				.then((result) => {
					// status, message, list, mapReaction, hasNextPage
					console.log(result);
					const replyBlock = btn_more_child.closest(".reply_block");
					// let to_created_at;
					let to_comment_id;
					result.list.forEach((r) => {
						console.log(r);
						const div = document.createElement("div");
						div.className = "reply_body";
						div.dataset.commentId = r.commentId; // div.reply_body
						// 圖片與會員姓名還沒處理 !!!!!!!
						div.innerHTML = `
          <!-- 單個回覆 -->
                        <img
                          src="${r.member.profilePic}"
                          alt="Image"
                          class="rounded-circle replier_others"
                        />
                        <h6>${r.member.nickname}</h6>
                        <small>${r.createdAt}</small>
                        <div class="report_block">
                          <button>...</button>
                          <ul>
                            <li>
                              <button class="btn_report">檢舉留言</button>
                            </li>
                          </ul>
                        </div>
                        <p>${r.content}</p>
                        <button class="btn_like">
                          <i class="icon-thumbs-up"></i>&nbsp;<span>${r.likeCount
							}</span>
                        </button>
                        <button class="btn_dislike">
                          <i class="icon-thumbs-down"></i>&nbsp;<span>${r.dislikeCount
							}</span>
                        </button>
      `;
						replyBlock.insertBefore(div, btn_more_child);
						// to_created_at = r.createdAt;
						to_comment_id = r.commentId;

						// 調整 member_id 此人的按讚與倒讚狀況
						const icon_like = div.querySelector("i.icon-thumbs-up");
						const icon_dislike = div.querySelector("i.icon-thumbs-down");

						if(result.status === "member"){
						// undefined / LIKE / DISLIKE
							const member_reaction = result.mapReaction[r.commentId];
							if (member_reaction === "LIKE") icon_like.classList.add("-on");
							else if (member_reaction === "DISLIKE")
								icon_dislike.classList.add("-on");
						}
						// 小心 max-height 問題...
					});

					// 現在按鈕都會先變成 true -> false 看要不要再調整
					btn_more_child.innerText = "查看更多回覆";
					// btn_more_child.dataset.createdAt = to_created_at;
					btn_more_child.dataset.commentId = to_comment_id;

					// 如果沒下一頁就執行
					if (!result.hasNextPage) {
						btn_more_child.innerText = "沒有更多回覆了！";
						btn_more_child.disabled = true;
					}
				})
				.catch((error) => {
					console.log(error);
					btn_more_child.innerText = "載入失敗，請重試"; // 顯示錯誤訊息
					btn_more_child.disabled = false; // 允許再次點擊嘗試
				});
		}

		// 點擊回覆 也要相當於點了一次 查看更多回復
		if (e.target.closest(".btn_reply")) {
			const btn_reply = e.target.closest(".btn_reply");
			const section_reply = btn_reply
				.closest("div.review_strip_single")
				.querySelector("section.replies");
			const i_el = btn_reply.querySelector("i");

			if (!section_reply.classList.contains("open")) {
				section_reply.classList.add("open");
				// 展開回覆當下 -> （子層）模擬實際點了一次「查看更多回覆」
				btn_reply
					.closest("div.review_strip_single")
					.querySelector(".btn_more_comments_child")
					.click();
				i_el.classList = "icon-up-open-big";
			} else {
				section_reply.classList.remove("open");
				i_el.classList = "icon-down-open-big";
			}
		}
	});
	// 頁面載入當下 -> （父層）模擬實際點了一次「查看更多留言」
	document.querySelector(".btn_more_comments_parent").click();

	// 側欄 查看更多
	const sidebar_more_related_exhib_btn = document.querySelector(
		"div#more_related_exhib > button.btn_full_exhib"
	);
	sidebar_more_related_exhib_btn.addEventListener("click", function() {
		// fetch
		const more_exhib_block_html = `              <hr />
              <div>
                <div class="sidebar_exhib_block">
                  <a href="">
                    <div class="sidebar_exhib_img">
                      <img
                        src="img/0_exhibition/ChatGPT_exhibition_1.png"
                        alt="推薦圖片"
                      />
                    </div>
                    <span class="sidebar_exhib_title"
                      >當代藝術畫展 - TibaMe</span
                    >
                    <span class="sidebar_exhib_loc"
                      ><i class="icon-location"></i>&nbsp;台北市立美術館</span
                    >
                    <span class="sidebar_exhib_time"
                      ><i class="icon-clock"></i>&nbsp;08/12~08/25</span
                    >
                    <div class="rating">
                      <i class="icon-star voted"></i
                      ><i class="icon-star voted"></i
                      ><i class="icon-star voted"></i
                      ><i class="icon-star voted"></i
                      ><i class="icon-star-half-alt voted"></i>
                      <span><small>(75)</small></span>
                    </div>
                  </a>
                </div>
              </div>`;

		// 只是模擬一次多個展覽（之後改成實際 fetch 到的筆數）
		for (let i = 0; i < 3; i++) {
			this.closest("div#more_related_exhib").insertAdjacentHTML(
				"beforebegin",
				more_exhib_block_html
			);
		}
		this.innerText = "已經到底了！";
		this.disabled = true;
	});

	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////

	// fetch 2: /api/cartItem/* -> 處理 新增購物車 按鈕

	// 以下為購物車 toast 使用
	// 安全強化版：自動消失 + 點擊關閉 + 滑過暫停(可選) + 動畫備援
	function showToast(
		message,
		type = "success",
		{
			duration = 2400, // 顯示時間
			pauseOnHover = true, // 滑過暫停
			withProgress = false, // 是否顯示底部進度條
		} = {}
	) {
		// 確保容器存在，且掛在 <body> 底下避免被 stacking context 影響
		let container = document.getElementById("toast-container");
		if (!container) {
			container = document.createElement("div");
			container.id = "toast-container";
			document.body.appendChild(container);
		}

		const toast = document.createElement("div");
		toast.className = `ct-toast ct-toast--${type}`;
		toast.setAttribute("role", "status");
		toast.setAttribute("aria-live", "polite");

		const icon = document.createElement("span");
		icon.className = "ct-toast__icon";
		icon.textContent = type === "success" ? "✓" : "×";

		const text = document.createElement("div");
		text.className = "ct-toast__msg";
		text.textContent = message;

		toast.append(icon, text);

		// 可選：進度條
		let progressEl,
			startTs = Date.now();
		if (withProgress) {
			progressEl = document.createElement("div");
			progressEl.style.cssText =
				"position:absolute;left:0;bottom:0;height:3px;width:100%;background:rgba(0,0,0,.08);overflow:hidden;border-bottom-left-radius:10px;border-bottom-right-radius:10px;";
			const bar = document.createElement("div");
			bar.style.cssText =
				"height:100%;width:100%;background:#16a34a;transform-origin:left center;transform:scaleX(1);transition:transform linear;";
			// 播放進度動畫
			requestAnimationFrame(() => {
				bar.style.transitionDuration = duration + "ms";
				bar.style.transform = "scaleX(0)";
			});
			progressEl.appendChild(bar);
			toast.style.position = "relative";
			toast.appendChild(progressEl);
		}

		container.appendChild(toast);

		// ---- 自動關閉 + 備援 ----
		let remaining = duration;
		let autoTimer = setTimeout(dismiss, remaining);

		// 滑過暫停
		let hoverTs = 0;
		if (pauseOnHover) {
			toast.addEventListener("mouseenter", () => {
				clearTimeout(autoTimer);
				hoverTs = Date.now();
				// 停止進度條動畫
				if (progressEl) {
					const bar = progressEl.firstChild;
					const elapsed = Date.now() - startTs;
					const doneRatio = Math.min(elapsed / duration, 1);
					bar.style.transitionDuration = "0ms";
					bar.style.transform = `scaleX(${Math.max(1 - doneRatio, 0)})`;
				}
			});
			toast.addEventListener("mouseleave", () => {
				const paused = Date.now() - hoverTs;
				remaining = Math.max(remaining - paused, 0);
				startTs = Date.now(); // 重新計時起點
				autoTimer = setTimeout(dismiss, remaining);
				// 續播進度條
				if (progressEl) {
					const bar = progressEl.firstChild;
					bar.style.transitionDuration = remaining + "ms";
					requestAnimationFrame(() => (bar.style.transform = "scaleX(0)"));
				}
			});
		}

		// 點擊立即關閉
		toast.addEventListener("click", dismiss);

		function dismiss() {
			clearTimeout(autoTimer);
			toast.classList.add("hide");

			// 主要：等動畫結束再移除
			let removed = false;
			toast.addEventListener(
				"animationend",
				() => {
					if (removed) return;
					removed = true;
					toast.remove();
				},
				{ once: true }
			);

			// 備援：若動畫事件沒觸發，保底在 400ms 後移除
			setTimeout(() => {
				if (removed) return;
				removed = true;
				toast.remove();
			}, 400);
		}
	}

	let flag_btn_add_cart_and_go = false;
	
	const addCartItem = function(send_data) {
		const adults_el = document.querySelector("input#adults");
		const students_el = document.querySelector("input#students");
		const elderly_el = document.querySelector("input#elderly");
		const disabled_el = document.querySelector("input#disabled");
		const mili_and_police_el = document.querySelector("input#mili_and_police");

		csrfFetchToRedirect("/api/front-end/protected/cartItem/addCartItem", {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify(send_data),
		})
			.then((res) => {
//				if (res.status === 401) {
					// 存入localStorage
//					sessionStorage.setItem("redirect", window.location.pathname);
//					sessionStorage.setItem("send_data", JSON.stringify(send_data));
					// 再轉導
//					window.location.href = "/front-end/login";
//				}
				if (!res.ok) {
					throw new Error("addCartItem: Not 2XX or 401");
				}
				return res.text();
			})
			.then((result) => {
				adults_el.value =
					students_el.value =
					elderly_el.value =
					disabled_el.value =
					mili_and_police_el.value =
					"0";
				recalcTotal();
				clearTable();
				if (result === "success") {
					showToast("已加入購物車", "success");
					if(flag_btn_add_cart_and_go){
						setTimeout(() => location.href = "/front-end/cart", 50)
					}
				} else if (result === "failure") {
					showToast("剩餘票數不足，加入購物車失敗", "error");
				}
			})
			.catch((error) => {
				console.log("error");
				console.log(error);
				showToast("連線異常，請稍後再試", "error");
			});
	}

	//	const redirect_send_data = sessionStorage.getItem("send_data");
	//	if (redirect_send_data) {
	//		if (!sessionStorage.getItem("redirect")) {
	//			setTimeout(() => {
	//				addCartItem(JSON.parse(redirect_send_data))
	//				sessionStorage.removeItem("send_data");
	//				}, 1000)
	//		}
	//	}

	const btn_add_cart = document.querySelector("a#add_cart");
	
	btn_add_cart.addEventListener("click", function(e) {
		e.preventDefault();

		// 收集數據
		// 收集數據 -> 1. exhibition_id;
//		const path_name = window.location.pathname;
//		const last_slash_index = path_name.lastIndexOf("/");
//		const exhibition_id = path_name.substring(last_slash_index + 1);
		const params = new URLSearchParams(window.location.search);
		const exhibitionId = params.get("exhibitionId");
		// 收集數據 -> 2. ticket_datas;
		const adults_el = document.querySelector("input#adults");
		const students_el = document.querySelector("input#students");
		const elderly_el = document.querySelector("input#elderly");
		const disabled_el = document.querySelector("input#disabled");
		const mili_and_police_el = document.querySelector("input#mili_and_police");
		const adults = Number(adults_el.value);
		const students = Number(students_el.value);
		const elderly = Number(elderly_el.value);
		const disabled = Number(disabled_el.value);
		const mili_and_police = Number(mili_and_police_el.value);
		if (
			adults === 0 &&
			students === 0 &&
			(elderly === 0) & (disabled === 0) & (mili_and_police === 0)
		) {
			showToast("請選擇購買票種數量！", "error");
			return;
		}
		let ticket_datas = {};
		if (adults > 0) ticket_datas[1] = adults;
		if (students > 0) ticket_datas[2] = students;
		if (elderly > 0) ticket_datas[3] = elderly;
		if (disabled > 0) ticket_datas[4] = disabled;
		if (mili_and_police > 0) ticket_datas[5] = mili_and_police;
		// 先寫死給後端取 ticket_type_id

		// const ticket_datas = {
		//   1: adults,
		//   2: students,
		//   3: elderly,
		//   4: disabled,
		//   5: mili_and_police,
		// };
		// 1全票，2學生票，3敬老票，4身心障礙者票，5軍警票

		const send_data = {
			exhibitionId: Number(exhibitionId), // test
			ticketDatas: ticket_datas,
		};

		addCartItem(send_data);
	});

	const btn_add_cart_and_go = document.querySelector("a#add_cart_and_go");

	// 其實這裡沒寫好，沒清掉票數就跑掉，要是 async 去寫比較邏輯正確
	btn_add_cart_and_go.addEventListener("click", function(e) {
		e.preventDefault();
		btn_add_cart.click();
		flag_btn_add_cart_and_go = true;
	});
	// JavaScript 動態取得路徑的方法 by 小吳

	// const pathname = window.location.pathname;
	// const contextPath =
	//   window.location.origin + pathname.substring(0, pathname.lastIndexOf("/") + 1);
	// const container = document.getElementById("book-container");

	// function fetchAllBooks() {
	//   // fetch API
	//   fetch(contextPath + "api/book/all")
	//     .then((response) => {
	//       if (!response.ok) {
	//         throw new Error("Network response was not ok");
	//       }
	//       return response.json();
	//     })

	//     .then((data) => {
	//       console.log(data);
	//       data.forEach((book) => {
	//         const bookCard = document.createElement("div");
	//         bookCard.classList.add("book-card");
	//         bookCard.innerHTML = `
	// 					                <h2>${book.name}</h2>
	// 					                <p>${book.author}</p>
	// 					                <div class="price">${book.price}</div>
	// 									<img src=${contextPath}api/bookpic/${book.id} alt="書籍封面" height="240px" weight="120px">
	// 					                <button>加入購物車</button>
	// 					            `;
	//         container.appendChild(bookCard);
	//       });
	//     })

	//     .catch((error) => {
	//       console.error(
	//         'There was a problem with the fetch "all books" operation:',
	//         error
	//       );
	//     });
	// }

	// fetch 3: /api/comment/* -> 處理 按讚／倒讚 按鈕
	// 同個 member_id 只能按一個，不能同時按讚 + 倒讚。
	document.addEventListener("click", function(e) {
		const btn_like = e.target.closest("button.btn_like");
		const btn_dislike = e.target.closest("button.btn_dislike");
		if (!btn_like && !btn_dislike) return;
		const btn = btn_like ? btn_like : btn_dislike;
		const another_btn = btn_like
			? btn.nextElementSibling
			: btn.previousElementSibling;
		const span = btn.querySelector("span");
		const another_span = another_btn.querySelector("span");

		// comment_id / member_id（後） / reaction
		// 收集數據
		// 收集數據 -> 1. comment_id;
		let comment_id;
		if (btn.closest("div.reply_body"))
			comment_id = btn.closest("div.reply_body").dataset.commentId;
		// ? 好像在 eclipse 不能寫
		else comment_id = btn.closest("div.review_strip_single").dataset.commentId;

		// 收集數據 -> 2. reaction;
		let reaction;
		if (btn_like) reaction = "LIKE";
		else if (btn_dislike) reaction = "DISLIKE";

		const send_data = {
			commentId: comment_id,
			reaction,
		};
		csrfFetchToRedirect("/api/front-end/protected/commentReaction/updateReaction", {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify(send_data),
		})
			.then((res) => {
				if (!res.ok) throw new Error("updateReaction: Not 2XX or 401");
				return res.json();
			})
			.then((result) => {
				// status, currentReaction, like_count, dislike_count;
				console.log(result.currentReaction);
				console.log("like: " + result.likeCount);
				console.log("dislike: " + result.dislikeCount);

				const icon_to_on = btn.querySelector("i");
				const icon_to_off = another_btn.querySelector("i");
				if (result.currentReaction === null) {
					icon_to_on.classList.remove("-on");
				} else {
					icon_to_on.classList.add("-on");
					icon_to_off.classList.remove("-on");
				}

				if (btn === btn_like) {
					span.innerText = result.likeCount;
					another_span.innerText = result.dislikeCount;
				} else if (btn === btn_dislike) {
					span.innerText = result.dislikeCount;
					another_span.innerText = result.likeCount;
				}
			})
			.catch((error) => {
				console.log("error");
				console.log(error);
			});
	});

	//  評價 收藏 anchors
	const modal = document.getElementById("rate_modal");
	const backdrop = document.getElementById("modal_backdrop");
	const btnCancel = modal.querySelector(".btn_cancel");
	const formRate = modal.querySelector("#rate_form");

	function openRate() {
		modal.classList.remove("hidden");
		backdrop.classList.add("show");
		// 加一點延遲再加 show class，才能有動畫
		requestAnimationFrame(() => {
			modal.classList.add("show");
		});
	}

	function closeRate() {
		modal.classList.remove("show");
		backdrop.classList.remove("show");
		// 等動畫結束再真正隱藏
		setTimeout(() => modal.classList.add("hidden"), 300);
	}

	// 2) 關閉
	btnCancel.addEventListener("click", closeRate);
	backdrop.addEventListener("click", closeRate);
	document.addEventListener("keydown", (e) => {
		if (e.key === "Escape") closeRate();
	});

	// 評價彈窗打開以後的邏輯

	// Elements
	const btnSubmitRate = document.querySelector("#rate_form .btn_submit");
	const divCannotRate = document.querySelector("#cannot_rate");
	const divOriginalRating = document.querySelector("#original_rating");
	const divStars = document.querySelector("#rate_form .stars");

	/**
	 * 顯示「不能評價」狀態
	 */
	function showCannotRate() {
		divOriginalRating.style.display = "none";
		btnSubmitRate.disabled = true;
		divCannotRate.classList.add("-on");
		divStars.classList.remove("-on");
	}

	/**
	 * 顯示「可以評價」狀態
	 * @param {number|null} originalRating - 使用者之前的評分
	 */
	function showCanRate(originalRating) {
		if (originalRating !== null) {
			divOriginalRating.style.display = "block";
			divOriginalRating.innerHTML = `您上次給此展覽 <strong><span>${originalRating}</span></strong> 顆星!`;
		} else {
			divOriginalRating.style.display = "none";
		}

		btnSubmitRate.disabled = false;
		divCannotRate.classList.remove("-on");
		divStars.classList.add("-on");
	}

	// 確保星星按下去後可以固定住（ 解決無法觸發 checked 問題 ）
	divStars.addEventListener("click", (e) => {
		const label = e.target.closest("label");
		if (!label || !divStars.classList.contains("-on")) return; // 只有啟用時才作用
		const id = label.getAttribute("for");
		const radio = document.getElementById(id);
		if (radio) {
			radio.checked = true; // 真的切 checked
			radio.dispatchEvent(
				new Event("change", {
					// 若你後面要用 change 事件
					bubbles: true,
				})
			);
		}
	});

	// fetch: /api/rating/* -> 處理 取評價 按鈕
	document.addEventListener("click", function(e) {
		// 評價按鈕
		const btn_open_rate_modal = e.target.closest("button#btn_open_rate_modal");
		if (!btn_open_rate_modal) return;
		// 收集數據
		// 收集數據 -> 1. exhibition_id;
//		const path_name = window.location.pathname;
//		const last_slash_index = path_name.lastIndexOf("/");
//		const exhibition_id = path_name.substring(last_slash_index);
		const params = new URLSearchParams(window.location.search);
		const exhibitionId = params.get("exhibitionId");

		csrfFetchToRedirect("/api/front-end/protected/rating/getMyRating?exhibitionId=" + exhibitionId, {
			method: "GET",
		})
			.then((res) => {
				if (!res.ok) throw new Error("getMyRating: Not 2XX");
				return res.json();
			})
			.then((result) => {
				openRate();
				console.log(result);
				// status, canRate, originalRating
				// Main logic
				if (result.canRate === false) {
					showCannotRate();
				} else if (result.canRate === true) {
					showCanRate(result.originalRating);
				}
			})
			.catch((error) => {
				console.log(error);
			});
	});

	// 每次送出評價後重新渲染 展覽頁面的星星（要不要亮...）
	function renderAverageStars(score, stars) {
		let s = Number(score);
		if (!Number.isFinite(s)) s = 0;
		s = Math.max(0, Math.min(5, s)); // 夾在 0..5

		const full = Math.floor(s); // 幾顆全滿
		const hasHalf = s - full > 0; // 你原本是「有小數就半顆」，所以門檻用 > 0
		let usedHalf = false;

		stars.forEach((star, idx) => {
			const pos = idx + 1; // 左到右第幾顆（若你的 DOM 是反的就改成 stars.length - idx）
			// 先清乾淨
			star.classList.remove("voted", "icon-star-half-alt");
			star.classList.add("icon-star");

			if (pos <= full) {
				// 全滿
				star.classList.add("voted");
			} else if (pos === full + 1 && hasHalf && !usedHalf) {
				// 半顆
				star.classList.add("voted");
				star.classList.remove("icon-star");
				star.classList.add("icon-star-half-alt");
				usedHalf = true;
			} // 其餘保持空星（不加 voted）
		});
	}

	document.addEventListener("click", function(e) {
		const btn_submit_rate = e.target.closest("#rate_form .btn_submit");
		if (!btn_submit_rate) return;
		e.preventDefault();

		const ratingScore_el = formRate.querySelector(
			"input[name='rating']:checked"
		);
		if (!ratingScore_el) {
			alert("請選擇星等");
			return;
		}
		const ratingScore = ratingScore_el.value;

		const span_el_originalRatingScore = document.querySelector(
			"#original_rating span"
		);
		let originalRatingScore;
		if (span_el_originalRatingScore)
			originalRatingScore = Number(span_el_originalRatingScore.innerText);

		console.log(originalRatingScore);

		if (ratingScore === originalRatingScore) return;
		
		const params = new URLSearchParams(window.location.search);
		const exhibitionId = params.get("exhibitionId");

		csrfFetchToRedirect(
			"/api/front-end/protected/rating/upsertRating?exhibitionId=" +
			exhibitionId +
			"&ratingScore=" +
			ratingScore,
			{
				method: "PUT",
			}
		)
			.then((res) => {
				if (!res.ok) throw new Error("upsertRating: Not 2XX or 401");
				// 其實前端已經擋不該評價的（包含未登入），但還是寫一次邏輯怕有人路徑亂送，後端取不到 Authentication 可能出錯
				return res.json();
			})
			.then((result) => {
				// status, totalRatingCount, averageRatingScore
				console.log(result);
				closeRate();

				const span_rating = document.querySelector("span#span_rating");

				const rating_el = span_rating.querySelector("small");
				rating_el.innerHTML = `${result.averageRatingScore.toFixed(
					1
				)}&nbsp;&nbsp;(${result.totalRatingCount})`;

				const stars = span_rating.querySelectorAll("i");

				renderAverageStars(result.averageRatingScore, stars);
				// ...
			})
			.catch((error) => {
				console.log("error");
				console.log(error);
			});
	});
});

document.addEventListener("DOMContentLoaded", function() {
	// 瀏覽器會打開一條 持續不關閉的 HTTP GET 請求。
	// 伺服器（Spring）回應的 body 就是一個 不斷追加資料的文字流（text/event-stream）。
	// 瀏覽器內建解析器會讀這個流，每當遇到 \n\n 就觸發一個 message 事件。
	const params = new URLSearchParams(window.location.search);
	const exhibitionId = params.get("exhibitionId");
	const eventSource = new EventSource("/api/sse/exhibition-ticket/subscribe/" + exhibitionId);

	// 後端 -> emitter.send(SseEmitter.event().name("ticket-update").data(remaining));
	eventSource.addEventListener("ticket-update", function(event) {
		const remaining = event.data; // 後端送來的剩餘票數
		document.querySelector("#sse_ticket_left").textContent = remaining; // 更新頁面
	})

	eventSource.onerror = (err) => {
		console.error("ticket-update SSE error: ", err);
	}
})

/////////////////////////////////////////////
/////////////////////////////////////////////
// 全域大廳聊天室 websocket
/////////////////////////////////////////////
/////////////////////////////////////////////

document.addEventListener("DOMContentLoaded", function() {

	const chatToggle = document.getElementById("chat-toggle");
	const chatWindow = document.getElementById("chat-window");
	const chatClose = document.getElementById("chat-close");
	const chatSend = document.getElementById("chat-send");
	const chatInput = document.getElementById("chat-input");
	const chatBody = document.getElementById("chat-body");
	const chatOverlay = document.getElementById("chat-overlay");
	const chatOnlineCount = document.getElementById("chat-online-count");
	chatWindow.style.display = "none";

	let myMemberId; // 核心! 定義我的 memberId !!
	
	let hasGottenMyMemberId = false;
	let timestampCursor; // 每次拿這個時間去 fetch 更舊的 10 筆！
	let isLoading = false; // 避免重複請求用！
	let oldestReached = false;

	/* ================= 處理 chat window 顯示與 input 處 disabled ================= */
	function toggleChatWindow(memberId) {
		// 對話框 本來是隱藏 就顯示
		if (chatWindow.style.display === "none") {
			// 非會員要處理 輸入框 disabled
			if (!memberId) {
				chatInput.disabled = true;
				chatSend.disabled = true;
				chatOverlay.style.display = "flex";
			}
			else {
				chatInput.disabled = false;
				chatSend.disabled = false;
				chatOverlay.style.display = "none";
			}

			// 不管如何都把 chatWindow 打開
			chatWindow.style.display = "flex";
		}
		// 對話框 本來是顯示 就隱藏
		else chatWindow.style.display = "none";
	}

	/* ================= 處理時間顯示 ================= */
	function formatTime(timestamp) {
		//			const date = new Date(timestamp).toLocaleDateString();
		const date = new Date(timestamp).toLocaleTimeString('zh-TW', {
			hour: '2-digit',
			minute: '2-digit',
			hour12: true
		});
		return date;
	}

	/* ================= 處理滑動 -> 繼續載入舊訊息 ================= */
	chatBody.addEventListener("scroll", async () => {
		// console.log(chatBody.scrollTop); // 上方還能滑動之高度
		// console.log(chatBody.scrollHeight) // 總共可滑動之高度

		// 滑到頂部就繼續載入
		if (chatBody.scrollTop === 0 && !isLoading && !oldestReached) {
			isLoading = true;

			const msgs = await getMessages(timestampCursor);
			if (msgs && msgs.length > 0) {

				//				msgs.forEach((msg, index) => {
				//					if (Number(msg.memberId) === Number(myMemberId)) // 都轉數字比
				//						appendMessage("self", msg);
				//					// 後端送回來這則，是別人發的
				//					else appendMessage("others", msg);
				//					
				//					// 更新 timestamp cursor
				//				})

				for (let i = msgs.length - 1; i >= 0; i--) {
					if (Number(msgs[i].memberId) === Number(myMemberId)) // 都轉數字比
						appendMessage("self", msgs[i], true);
					// 後端送回來這則，是別人發的
					else appendMessage("others", msgs[i], true);

					if (i === 0) {
						timestampCursor = msgs[i].sentTime;
					}
				}
			}
			else {
				oldestReached = true;
				isLoading = false;
				return;
			}
			recordable = true;
			isLoading = false;
		}

	})

	/* ================= 處理第一次點擊 toggle btn 開啟對話窗 ================= */
	// 按鈕打開或關閉 window -> 可優化把 icon 換成 x 但先略
	chatToggle.addEventListener("click", () => {

		// 第一次點擊 打開聊天室 按鈕
		// 打開以後就持續同步聊天室直到斷線了，所以第二次不用再取
		if (!hasGottenMyMemberId) {
			// 1. 馬上先建立連線
			connect();
			// 2. 先取在線人數 -> 改在連線中取，否則會取不到自己的連線數字
			// 3. 取自己的 memberId -> 拿來後續判斷顯示左還右！
			getMyMemberId().then(memberId => {
				myMemberId = memberId;
				toggleChatWindow(myMemberId);
				hasGottenMyMemberId = true;
			})
			// 4. 取過去的聊天記錄
			const timestamp = Date.now(); // 拿到一個 long 類型的數值
			getMessages(timestamp).then(list => {
				list.forEach((msg, index) => {
					// msg -> memberId, agentId, avatarSrc, content, sentTime
					if (Number(msg.memberId) === Number(myMemberId)) // 都轉數字比
						appendMessage("self", msg);
					// 後端送回來這則，是別人發的
					else appendMessage("others", msg);
					if (index === 0) timestampCursor = msg.sentTime;
				})
			});
		}

		// 非第一次點擊 打開聊天室 按鈕 -> 純 toggle
		else toggleChatWindow(myMemberId);

		chatBody.scrollTop = chatBody.scrollHeight;
	});

	/* ================= 部分事件綁定 ================= */
	// window 右上角的關閉按鈕
	chatClose.addEventListener("click", () => {
		chatWindow.style.display = "none";
	});
	// 送出按鈕 -> 送訊息
	chatSend.addEventListener("click", sendMessage);
	// 或按下 Enter -> 送訊息
	chatInput.addEventListener("keyup", (e) => {
		if (e.key === "Enter") sendMessage();
	});

	/* ================= api: 取得初始在線人數 ================= */
	function getInitOnlineCount() {
		return fetch("/api/front-end/chat/initCount", { method: "GET" })
			.then(res => {
				if (!res.ok) throw new Error("Not 2XX");
				return res.text();
			})
			.catch(error => {
				console.log("initCount: " + error);
				return null;
			})
	}

	/* ================= api: 確認自己是誰 ================= */
	function getMyMemberId() {
		return fetch("/api/front-end/protected/member/getMyMemberId", { method: "GET" })
			.then((res) => {
				if (!res.ok) throw new Error("getMyMemberId: Not 2XX");
				return res.text();
			})
			.then((result) => {
				if (result === "") {
					console.log("getMyMemberId: NOT MEMBER!");
					return null;
				}
				else {
					console.log("getMyMemberId: memberId= " + result);
					return result;
				}
			})
			.catch((error) => {
				console.log(error);
				return null;
			});
	}
	/* ================= api: 拿過去的聊天訊息紀錄 ================= */

	function getMessages(timestamp) {
		return fetch("/api/front-end/chat/getMessages?timestamp=" + timestamp, {
			method: "GET"
		})
			.then(res => {
				if (!res.ok) throw new Error("getMessages: NOT 2XX");
				else return res.json();
			})
			.catch(error => {
				console.log(error);
				return null;
			})
	}

	/* ================= SockJS + STOMP + WebSocket ================= */
	let stompClient = null;

	function connect() {
		const socket = new SockJS("/ws-chat");
		// 後端設定的 endpoint -> WebSocketConfig 裡 registry.addEndpoint("/ws-chat")
		// SockJs 為 WebSocket 兼容層 ->「使用 WebSocket，但失敗就自動降級」的連線
		stompClient = Stomp.over(socket);
		// 代表在「SockJS 的連線」上再套用 STOMP 協議，這樣就能用
		// .connect()
		// .subscribe()
		// .send() 這些方法

		// 核心 part1 -> .connect()
		// 參數1: headers, 可選，可用來傳遞認證資訊
		// 參數2: callback function，當連線建立成功時，會立即被執行
		// frame 參數: 包含了連線成功的資訊，包含伺服器端的細節等...
		stompClient.connect({}, function(frame) {
			console.log('Connected: ' + frame);

			getInitOnlineCount().then(onlineCount => {
				chatOnlineCount.innerText = onlineCount;
			})

			// 核心 part2 -> .subscribe()
			// 參數1: 訂閱位址
			// 訂閱後端廣播頻道（配合後端 @SendTo("...") 或 convertAndSend(...))
			// 參數2: callback function，處理接收到的訊息
			stompClient.subscribe('/topic/messages', function(message) {
				// 和 HTTP Protocol res.json() 不同情況，此處為 ws Protocol，message.body 直接是 json 字串而非串流
				// res.json() 和 JSON.parse() 的根本區別在於：
				// res.json() 是一個 "stream reader + JSON parser"：它會先讀取一個串流，然後解析成物件。
				// JSON.parse() 是一個 "JSON parser"：它直接將一個已知的 JSON 字串解析成物件。
				const msg = JSON.parse(message.body);
				// 後端送回來這則，是我本人發的
				console.log("msg.memberId: " + msg.memberId);
				console.log("myMemberId: " + myMemberId);
				if (Number(msg.memberId) === Number(myMemberId)) // 都轉數字比
					appendMessage("self", msg);
				// 後端送回來這則，是別人發的
				else appendMessage("others", msg);
			})

			stompClient.subscribe('/topic/onlineCount', function(message) {
				chatOnlineCount.innerText = message.body;
			})
		})
	}

	function sendMessage() {
		const text = chatInput.value.trim();
		if (!text) return;

		// 核心 part3 -> .send()
		// 往後端 @MessageMapping("...") 送訊息
		stompClient.send("/app/chat", {}, JSON.stringify({
			// text 前端取 -> memberId, agentId, sentTime, avatarSrc 後端取
			content: text,
		}))

		chatInput.value = ""; // 清空輸入框
	}

	/* =================================================== */

	function appendMessage(sender, msg, prepend = false) {
		const msgDiv = document.createElement("div");
		msgDiv.classList.add("message", sender);

		const avatar = document.createElement("img");
		avatar.classList.add("avatar");
		avatar.src = msg.avatarSrc;

		const bubble = document.createElement("div");
		bubble.classList.add("bubble");
		bubble.textContent = msg.content;

		const time = document.createElement("time");
		time.classList.add(sender);
		time.textContent = formatTime(msg.sentTime);

		msgDiv.appendChild(avatar);
		msgDiv.appendChild(bubble);
		msgDiv.appendChild(time);

		if (prepend) {
			// 1. 找到目前最上面那個訊息 (載入前第一個元素)
			const firstMsg = chatBody.firstElementChild;
			const prevTop = firstMsg ? firstMsg.getBoundingClientRect().top : 0;

			// 2. 插入新訊息
			chatBody.prepend(msgDiv);
			requestAnimationFrame(() => msgDiv.classList.add("show"));

			// 3. 計算插入後這個元素的新位置
			const newTop = firstMsg ? firstMsg.getBoundingClientRect().top : 0;

			// 4. 調整 scrollTop → 補回位移
			chatBody.scrollTop += (newTop - prevTop);
		} else {
			chatBody.appendChild(msgDiv);
			// 自動捲到最底
			chatBody.scrollTop = chatBody.scrollHeight;
			requestAnimationFrame(() => msgDiv.classList.add("show"));
		}
	}
})