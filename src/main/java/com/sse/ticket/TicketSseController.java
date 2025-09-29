package com.sse.ticket;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// SSE (Server-Sent Events)
	// 單向推送技術（客戶端無法推）
	// 瀏覽器（Client）先跟伺服器建立一條「持久化連線」（HTTP 長連線）
	// 伺服器（Server）可以在 任何時候 主動傳資料給瀏覽器
	// 瀏覽器會透過 EventSource API 監聽事件（和 WebSocket 不同，不需要新協定）

// 1. 客戶端用 GET 發一個請求到 /subscribe 等端點
// 2. 伺服器回應 Content-Type: text/event-stream
// 3. 連線保持打開（不會馬上結束）
// 4. 伺服器隨時可以傳送事件

@RestController
@RequestMapping("/api/sse/exhibition-ticket")
public class TicketSseController {

	private final TicketSseEmitterService sseService;
	
	public TicketSseController(TicketSseEmitterService sseService) {
		this.sseService = sseService;
	}
	
	// @ SSE 訂閱端點 
	// Client 端會透過 EventSource API 呼叫
		// const es = new EventSource("/api/sse/exhibition-ticket/subscribe");
	// 這會建立一條長連線（HTTP long-lived connection)
	// Spring 會回傳一個 SseEmitter 物件
		// 讓 Server 可以持續透過這個 emitter 把事件推送給 Client，而不是一次性回傳就結束
	
	@GetMapping("/subscribe/{exhibitionId}")
	public SseEmitter subscribe(@PathVariable("exhibitionId") Integer exhibitionId) {
		// 呼叫 service，建立一個新的 SseEmitter 並回傳給客戶端
        // 這個 emitter 後續就會被存在 service 中，用來推送票務更新
		// text/event-stream 是 HTTP 回應的 Content-Type → 告訴瀏覽器這是一條 SSE 連線
			// Spring MVC 看到我們回傳 SseEmitter
				// 1. 建立一個 HTTP Response
				// 2. 設定 Header -> Content-Type: text/event-source
				// 3. 把這個 Response 維持開啟（不結束，不 flush 關閉）
				// 4. 把 SseEmitter 綁定到這個 Response 上，讓你之後 .send() 的內容寫進這條 Response
			// 前端看到的是 標準 SSE 資料流
			// 後端程式碼裡看到的是 SseEmitter 物件（本身不是 text/event-stream，而是 Spring 對這個流的封裝）
		
		// SseEmitter 是 Spring 封裝的 Java 物件 → 幫你操作這條 SSE 連線（send、complete、timeout）
		// 連線維持與中斷條件
		// 這條「資料流」會一直存在，直到以下情況：
			// 正常結束 → 你在後端呼叫 emitter.complete()
				// 會觸發.onCompletion() callback
			// 逾時 → SseEmitter 建構子有個 timeout（預設 30 秒，如果沒指定）
				// 會觸發 .onTimeout() callback
			// 錯誤 → 傳送資料時丟 exception（例如 client 斷網）
				// 會觸發 .onError() callback
			// 前端手動關閉 → 呼叫 es.close()
		return sseService.createEmitter(exhibitionId);
	}
}
