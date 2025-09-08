package com.util;

public class MillisToMinutesSecondsUtil {

    public static String convert(long milliseconds) {
    	
        // 將毫秒轉成秒
        long totalSeconds = milliseconds / 1000;
        
        // 計算分
        long minutes = totalSeconds / 60;
        
        // 計算秒 (取餘數)
        long seconds = totalSeconds % 60;
        
        // 使用 String.format 格式化輸出，確保秒數是兩位數
        return String.format("%d:%02d", minutes, seconds);
    }
}