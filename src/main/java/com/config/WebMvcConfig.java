package com.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 把springBoot預設的 static/uploads/** 映射到我電腦本地的 /Users/lianliwei/uploads/**
		registry.addResourceHandler("/uploads/**")
			.addResourceLocations("file:/Users/lianliwei/uploads/");
	}

}
