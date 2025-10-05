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
		return csrfFetch("/api/front-end/protected/member/getMyMemberId", { method: "GET" })
			.then((res) => {
				if (!res.ok) return res.text().then(text => {throw new Error("getMyMemberId: " + text);})
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
		const socket = new SockJS("/front-end/ws-chat");
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