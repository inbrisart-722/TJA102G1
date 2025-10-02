package com.eventra.eventnotification.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.eventra.eventnotification.model.EventNotificationService.NotificationType;

/**
 * 負責產生各種展覽通知類型的標題與內容
 *
 * 通知類型:
 * - OPENING_SOON			// 開賣提醒
 * - LOW_STOCK				// 低庫存提醒
 * - LOCATION_CHANGE		// 地點異動
 * - TIME_CHANGE			// 檔期異動
 *
 */

public class EventNotificationMessageBuilder {

	// 確保所有通知的時間格式一致
    private static final DateTimeFormatter DATE_TIME_FMT =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    
    // ===== 通用入口, 自動呼叫對應的標題 =====
    public static String buildTitleByType(NotificationType type, String exhibitionName) {
    	switch (type) {
    	case OPENING_SOON:
    		return buildTicketStartTitle(exhibitionName);
    	case LOW_STOCK:
    		return buildTicketLowTitle(exhibitionName);
    	case LOCATION_CHANGE:
    		return buildLocationChangeTitle(exhibitionName);
    	case TIME_CHANGE:
    		return buildTimeChangeTitle(exhibitionName);
    	default:
    		// 不合法參數, 丟出例外
    		throw new IllegalArgumentException("未知通知類型: " + type);
    	}
    }

    // ===== 開賣提醒 OPENING_SOON =====
    public static String buildTicketStartTitle(String exhibitionName) {
        return "開賣提醒｜您收藏的展覽即將開賣";
    }
    
    public static String buildTicketStartContent(String exhibitionName, LocalDateTime startTime) {
        return "您收藏的展覽「" + exhibitionName + "」將於 " +
                startTime.format(DATE_TIME_FMT) + " 開賣，請盡早登入完成購票！";
    }

    // ===== 低庫存提醒 LOW_STOCK (顯示門檻) =====
    public static String buildTicketLowTitle(String exhibitionName) {
        return "售票緊急｜您收藏的展覽即將售完";
    }

    public static String buildTicketLowContent(String exhibitionName, int threshold) {
        return "您收藏的展覽「" + exhibitionName + "」票券數量已低於 " +
                threshold + " 張，建議您立即前往購票！";
    }

    // ===== 地點異動 LOCATION_CHANGE =====
    public static String buildLocationChangeTitle(String exhibitionName) {
        return "展覽通知｜您收藏的展覽地點異動";
    }

    public static String buildLocationChangeContent(String exhibitionName, String newLocation) {
        return "您收藏的展覽「" + exhibitionName + "」地點已更新為「" +
                newLocation + "」，請至活動頁面確認最新資訊。";
    }

    // ===== 檔期異動 TIME_CHANGE =====
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
