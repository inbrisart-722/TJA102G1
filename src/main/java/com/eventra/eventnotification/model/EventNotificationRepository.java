package com.eventra.eventnotification.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EventNotificationRepository extends JpaRepository<EventNotificationVO, Integer> {
		
	// 查詢會員所有收藏通知
	@Query(value="SELECT * FROM event_notification " +
				 "WHERE member_id = :memId " +
				 "ORDER BY created_at DESC",
				 nativeQuery = true)
	List<EventNotificationVO> findNotificationsByMember(@Param("memId") Integer memberId);
	
	// 修改單筆收藏通知狀態為已讀
	@Transactional 
	@Modifying 
	@Query(value="UPDATE event_notification " +
				 "SET read_status = :status " +
				 "WHERE favorite_announcement_id = :annId",
				 nativeQuery = true)
	public void updateOneReadStatus(@Param("status")Boolean readStatus, @Param("annId")int favoriteAnnouncementId);
	
	// 修改會員所有收藏通知狀態為已讀
	@Transactional
	@Modifying
	@Query(value="UPDATE event_notification " +
				 "SET read_status = :status " +
				 "WHERE member_id = :memId",
				 nativeQuery = true)
	public void updateAllReadStatus(@Param("status")Boolean readStatus, @Param("memId")int memberId);
	
	
}
