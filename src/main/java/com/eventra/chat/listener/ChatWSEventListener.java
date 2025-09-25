package com.eventra.chat.listener;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class ChatWSEventListener implements ApplicationListener<AbstractSubProtocolEvent>{

	private final SimpMessagingTemplate messagingTemplate;
	private final AtomicInteger onlineCount;
	
	public ChatWSEventListener(AtomicInteger onlineCount, SimpMessagingTemplate messagingTemplate) {
		this.onlineCount = onlineCount;
		this.messagingTemplate = messagingTemplate;
	}
	
	// WebSocket/STOMP 在握手、連線、訂閱、斷線時，Spring 會發出 ApplicationEvent
		// SessionConnectEvent;
		// SessionConnectedEvent;
		// SessionSubscribeEvent;
		// SessionDisconnectEvent;
	// 這些事件是由 WebSocketStompClient 和內部的 SubProtocolWebSocketHandler 觸發的
	// 最後被丟進 Spring 的 ApplicationEventPublisher
	// 只要 Bean 實作了 ApplicationListener<某事件>
		// 其中唯一需要實作的方法就是 void onApplicationEvent(E event)
		// Spring 會自動註冊 -> 當該事件發生，會被回呼
	
	@Override
	public void onApplicationEvent(AbstractSubProtocolEvent event) {
		if(event instanceof SessionConnectedEvent) {
			int count = onlineCount.incrementAndGet();
//			System.out.println("有人連線，目前人數：" + count);
			messagingTemplate.convertAndSend("/topic/onlineCount", count);
		} else if (event instanceof SessionDisconnectEvent) {
			int count = onlineCount.decrementAndGet();
//			System.out.println("有人斷線，目前人數：" + count);
			messagingTemplate.convertAndSend("/topic/onlineCount", count);
		}
	}
	
}



//@Component
//public class WebSocketSessionRegistry
//        implements ApplicationListener<AbstractSubProtocolEvent> {
//
//    private final Map<String, String> sessionMemberMap = new ConcurrentHashMap<>();
//
//    @Override
//    public void onApplicationEvent(AbstractSubProtocolEvent event) {
//        if (event instanceof SessionConnectedEvent sce) {
//            StompHeaderAccessor sha = StompHeaderAccessor.wrap(sce.getMessage());
//            String sessionId = sha.getSessionId();
//            String memberId = sha.getFirstNativeHeader("memberId"); // 前端連線時 header 帶上
//            sessionMemberMap.put(sessionId, memberId);
//            System.out.println("✅ 連線: " + memberId + " (session=" + sessionId + ")");
//        } else if (event instanceof SessionDisconnectEvent sde) {
//            StompHeaderAccessor sha = StompHeaderAccessor.wrap(sde.getMessage());
//            String sessionId = sha.getSessionId();
//            String memberId = sessionMemberMap.remove(sessionId);
//            System.out.println("❌ 斷線: " + memberId + " (session=" + sessionId + ")");
//        }
//    }
//
//    public int getOnlineCount() {
//        return sessionMemberMap.size();
//    }
//
//    public Set<String> getOnlineMembers() {
//        return new HashSet<>(sessionMemberMap.values());
//    }
//}
