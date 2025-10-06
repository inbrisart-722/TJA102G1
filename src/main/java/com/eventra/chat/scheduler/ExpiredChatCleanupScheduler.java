package com.eventra.chat.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eventra.chat.model.ChatRedisRepository;

@Component
public class ExpiredChatCleanupScheduler {
	
	private final ChatRedisRepository CHAT_REDIS_REPO;
	
	public ExpiredChatCleanupScheduler(ChatRedisRepository chatRedisRepository) {
		this.CHAT_REDIS_REPO = chatRedisRepository;
	}
	
	@Scheduled(fixedRate=600_000)
	public void cleanupExpiredChatMessages() {
		System.out.println("-----每10分鐘掃: cleanupExpiredChatMessages-----");
		CHAT_REDIS_REPO.cleanupExpiredChatMessages();
	}
	
}
