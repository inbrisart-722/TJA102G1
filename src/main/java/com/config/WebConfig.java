package com.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
    	// 白名單設置
    	registry.addMapping("/api/**")
        .allowedOriginPatterns(
            "http://localhost:3000", 
            "http://localhost:5173",
            "http://localhost:8080",
            "http://127.0.0.1:*", // 瀏覽器的 Origin 判斷非常嚴格, 如果前端有時用 localhost，有時用 127.0.0.1 開發 → 兩個都加進白名單較好。
            "https://44207b177b3c.ngrok-free.app" // ngrok to 外網 testing
        )
        .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
        .allowedHeaders("Content-Type","Authorization","X-Requested-With")
        .maxAge(3600);

    }
}

