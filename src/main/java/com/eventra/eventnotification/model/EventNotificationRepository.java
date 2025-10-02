package com.eventra.eventnotification.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EventNotificationRepository extends JpaRepository<EventNotificationVO, Integer> {
		
	// 查詢會員所有通知 (依建立時間由新到舊排序)	
	// 一次查出通知 + 展覽資料 (避免 N + 1 問題)
	@Query("SELECT n FROM EventNotificationVO n " +
			"LEFT JOIN FETCH n.exhibition e " +
			"WHERE n.memberId = :memId " +
			"ORDER BY n.createdAt DESC")
	List<EventNotificationVO> findNotificationsWithExhibition(@Param("memId") Integer memberId);

	// 分頁查詢會員通知 (依建立時間由新到舊排序)
	Page<EventNotificationVO> findByMemberIdOrderByCreatedAtDesc(Integer memberId, Pageable pageable);
	
	// 更新 [單一]通知為已讀狀態
	@Transactional 
	@Modifying 
	@Query(value="UPDATE event_notification " +
				 "SET read_status = :status " +
				 "WHERE favorite_announcement_id = :annId",
				 nativeQuery = true)
	public void updateOneReadStatus(@Param("status")Boolean readStatus, @Param("annId")int favoriteAnnouncementId);

	// 更新 [全部]通知為已讀狀態
	@Transactional
	@Modifying
	@Query(value="UPDATE event_notification " +
				 "SET read_status = :status " +
				 "WHERE member_id = :memId",
				 nativeQuery = true)
	public void updateAllReadStatus(@Param("status")Boolean readStatus, @Param("memId")int memberId);
	
	// [開賣提醒通知用] 檢查某會員、某展覽、某通知類型是否已經發送過, 用來避免重複發送相同通知
	boolean existsByMemberIdAndExhibitionIdAndNotificationType(
			Integer memberId,
			Integer exhibitionId,
			String notificationType);
	
	// [低庫存通知用] 檢查某會員、某展覽、某通知類型、某門檻數量是否已經發送過, 用來避免重複發送相同通知
	boolean existsByMemberIdAndExhibitionIdAndNotificationTypeAndThreshold(
		    Integer memberId,
		    Integer exhibitionId,
		    String notificationType,
		    Integer threshold);

}
