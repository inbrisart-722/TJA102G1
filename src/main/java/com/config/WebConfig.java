package com.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
    	registry.addMapping("/**")
        .allowedOriginPatterns(
            "http://localhost:3000",
            "http://localhost:5173",
            "http://127.0.0.1:*",
            "https://*.yourdomain.com"
        )
        .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
        .allowedHeaders("Content-Type","Authorization","X-Requested-With")
        .maxAge(3600);

    }
}

