package com.sse.ticket;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class TicketSseEmitterService {

	// ConcurrentHashMap -> thread-safe version of HashMap
		// 多個 thread（請求）同時存取的時候，不會像 HashMap 那樣出現 race condition 或 ConcurrentModificationException
	
	// key: exhibitionId
	// value: 這個展覽底下的所有 client
	private final Map<Integer, Map<String, SseEmitter>> exhibitionClients = new ConcurrentHashMap<>();

	
	public SseEmitter createEmitter(Integer exhibitionId) {
		
		// Spring 提供 SseEmitter 物件封裝處理 SSE
		// 代表一個持續的連線（長連線）
		SseEmitter emitter = new SseEmitter(0L); // 毫秒，代表永遠不會超時; 若為 5000L → 代表 5 秒沒事件就斷線。
		String id = UUID.randomUUID().toString();
		
		// computeIfAbsent
			// 如果 exhibitionClients 裡 已經有這個 exhibitionId → 直接拿那個 Map
			// 如果 沒有這個 exhibitionId → 建立一個新的 ConcurrentHashMap 並放進去
			// 最後 .put(id, emitter) = 把這個 client 訂閱存進去
		exhibitionClients
	        .computeIfAbsent(exhibitionId, k -> new ConcurrentHashMap<>())
	        .put(id, emitter);
		
		// 三種 callback，確保「連線中斷時」可以移除該 client，避免 memory leak
		// 不是必須，但是是最佳實踐，否則會累積很多失效連線，效能下滑
			// 例如使用者關掉瀏覽器 tab → server 端 SseEmitter 物件可能還留在 Map 裡，時間久了會有一堆無效 emitter → 記憶體增加、推播失敗時丟一堆 exception
		
		// 正常結束 
		emitter.onCompletion(() -> exhibitionClients.get(exhibitionId).remove(id));
		// 逾時 -> SseEmitter 建構子有 timeout 預設 30 秒（如果沒有另外指定），但這邊設定 0L 其實不會有此狀況
		emitter.onTimeout(() -> exhibitionClients.get(exhibitionId).remove(id));
		// 錯誤 -> 例如 client 網路斷線
		emitter.onError((e) -> exhibitionClients.get(exhibitionId).remove(id));
		
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
	public void broadcastTicketCount(Integer exhibitionId, int remaining) {
	    Map<String, SseEmitter> clients = exhibitionClients.get(exhibitionId);
	    if (clients == null || clients.isEmpty()) return;

	    // Iterator 版本：可安全移除失效 emitter
	    for (Iterator<Map.Entry<String, SseEmitter>> it = clients.entrySet().iterator(); it.hasNext();) {
	        Map.Entry<String, SseEmitter> entry = it.next();
	        SseEmitter emitter = entry.getValue();

	        try {
	            emitter.send(SseEmitter.event()
	                    .name("ticket-update")
	                    .data(remaining));

	            // 1. 有人死掉會丟 IllegalStateException
	        } catch (IllegalStateException | IOException e) {
	            // 已失效 (client 斷線 or async context closed)
	            // ↓↓↓ 重點是這裡要非常防守式 ↓↓↓
	            try {
	                emitter.complete();
	            }
	            // 3. emitter.complete() 已經在 error 狀態會丟 IllegalStateException
	            catch (IllegalStateException ignored) {
	                // emitter 已經被標記 error / complete 不可再用 → 忽略即可
	            }
	            // 2. 要用 for Each 背後就是用 iterator -> 若直接移除 value 也會爆 ConcurrentModification
	            it.remove(); // 安全移除，不會 ConcurrentModification
	            
	            // 因為 Iterator 的 remove() 是唯一「安全地在迭代中移除元素」的方式。
	            // 它會幫你同步更新 expectedModCount，讓迭代器知道：
	            // “是我自己移除的，不是別人亂動。”
	            
	        }
	        
//	        catch (IOException e) {
//	        	emitter.complete();
//	        	clients.values().remove(emitter);
//	        }
	        
	    }
	}
}
