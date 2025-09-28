package com.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 用來控制 Spring 排程功能的開關
 * 
 * @Configuration : 表示是 Spring 設定檔, Spring Boot 啟動時會把這個類別當成設定來源載入
 * @EnableScheduling : 啟用 Spring 排程任務(Scheduling) 功能, 執行有 @Scheduled 的method
 * @ConditionalOnProperty : 透過 application.properties 決定排程任務是否啟用
 *  - eventra.scheduling.enabled=true  //  啟用(預設)
 *  - eventra.scheduling.enabled=false //  停用
 *  
 * 集中管理排程開關, 避免誤觸定時任務
 */

@Configuration
@EnableScheduling
@ConditionalOnProperty(
		prefix = "eventra.scheduling", 
		name = "enabled", 
		havingValue = "true", 
		matchIfMissing = true)
public class SchedulingConfig {
	
}
