package com.eventra.map.util;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.Application;  // 這就是你的主程式類

public class ImportExhibitionsRunner {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        ImportExhibitionsTool tool = context.getBean(ImportExhibitionsTool.class);
        tool.importData();
        context.close();
    }
}
