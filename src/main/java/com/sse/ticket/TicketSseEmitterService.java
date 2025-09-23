package com.sse.ticket;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class TicketSseEmitterService {

	// ConcurrentHashMap -> thread-safe version of HashMap
		// 多個 thread（請求）同時存取的時候，不會像 HashMap 那樣出現 race condition 或 ConcurrentModificationException
	// 儲存目前所有已連線的 Client
	// Key: client id（我們用當下時間戳當 Key）
	// Value: 對應的 SseEmitter（代表這個 Client 的連線管道）
	private final Map<String, SseEmitter> clients = new ConcurrentHashMap<>();
	
	public SseEmitter createEmitter() {
		
		// Spring 提供 SseEmitter 物件封裝處理 SSE
		// 代表一個持續的連線（長連線）
		SseEmitter emitter = new SseEmitter(0L); // 毫秒，代表永遠不會超時; 若為 5000L → 代表 5 秒沒事件就斷線。
		String id = UUID.randomUUID().toString();
		clients.put(id, emitter);
		
		// 三種 callback，確保「連線中斷時」可以移除該 client，避免 memory leak
		// 不是必須，但是是最佳實踐，否則會累積很多失效連線，效能下滑
			// 例如使用者關掉瀏覽器 tab → server 端 SseEmitter 物件可能還留在 Map 裡，時間久了會有一堆無效 emitter → 記憶體增加、推播失敗時丟一堆 exception
		
		// 正常結束 
		emitter.onCompletion(() -> clients.remove(id));
		// 逾時 -> SseEmitter 建構子有 timeout 預設 30 秒（如果沒有另外指定），但這邊設定 0L 其實不會有此狀況
		emitter.onTimeout(() -> clients.remove(id));
		// 錯誤 -> 例如 client 網路斷線
		emitter.onError((e) -> clients.remove(id));
		
		// Content-Type: text/event-source
		// 把 emitter（新的 SSE 連線）傳給 controller 讓它可以回傳給某個 client 
		return emitter;
	}
	
	// SSE 有一個規範（W3C EventSource standard）：
		// 每一筆推送的訊息都是 純文字，用 data: 開頭
		// 必須以 兩個換行 \n\n 作為「事件結束」的標記
		// 瀏覽器（EventSource API）才會認得這是一筆完整的訊息
		// 如果沒有最後那個 \n\n，瀏覽器會以為資料還沒送完，整筆訊息會卡住不觸發。
			// 範例: emitter.send("data: Hello\n\n");
	
	// 推送票數給所有的 client
	public void broadcastTicketCount(int remaining) {
		for(SseEmitter emitter : clients.values()) {
			try{
				// .send -> 推送訊息給客戶端
					// .event -> builder
					// .name -> 事件名稱(前端可用 addEventListener 監聽)
					// .data -> 真正傳送的資料（剩餘票數）
				emitter.send(SseEmitter.event() 
						.name("ticket-update")
						.data(remaining));
			} catch (IOException e) {
				// .complete -> 結束 SSE 連線
				// 如果發送失敗（通常是 client 斷線），就關掉 emitter（SSE 連線）並且讓 callback 移除此清單
				emitter.complete();
			}
		}
	}
}
