package com.eventra.eventnotification.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventNotificationFactory {

    private static final DateTimeFormatter DATE_TIME_FMT =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    // ===== 1. 開賣提醒 =====
    public static String buildTicketStartTitle(String exhibitionName) {
        return "開賣提醒｜您收藏的展覽即將開賣";
    }

    public static String buildTicketStartContent(String exhibitionName, LocalDateTime startTime) {
        return "您收藏的展覽「" + exhibitionName + "」將於 " +
                startTime.format(DATE_TIME_FMT) + " 開賣，請盡早登入完成購票！";
    }

    
    // ===== 2. 售票緊急 =====
    public static String buildTicketLowTitle(String exhibitionName) {
        return "售票緊急｜您收藏的展覽即將售完";
    }

    public static String buildTicketLowContent(String exhibitionName, int remaining) {
        return "您收藏的展覽「" + exhibitionName + "」票券剩餘不多（約 " +
                remaining + " 張），建議您立即前往購票！";
    }

    
    // ===== 3. 地點異動 =====
    public static String buildLocationChangeTitle(String exhibitionName) {
        return "展覽通知｜您收藏的展覽地點異動";
    }

    public static String buildLocationChangeContent(String exhibitionName, String newLocation) {
        return "您收藏的展覽「" + exhibitionName + "」地點已更新為「" +
                newLocation + "」，請至活動頁面確認最新資訊。";
    }

    
    // ===== 4. 檔期異動 =====
    public static String buildTimeChangeTitle(String exhibitionName) {
        return "展覽通知｜您收藏的展覽時間異動";
    }

    public static String buildTimeChangeContent(String exhibitionName,
                                                LocalDateTime startTime,
                                                LocalDateTime endTime) {
        return "您收藏的展覽「" + exhibitionName + "」檔期時間有所異動，新檔期為 " +
                startTime.format(DATE_TIME_FMT) + " – " +
                endTime.format(DATE_TIME_FMT) + "，請至活動頁面確認最新資訊。";
    }
}
