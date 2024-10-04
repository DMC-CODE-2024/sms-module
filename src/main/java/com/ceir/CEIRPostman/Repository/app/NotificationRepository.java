package com.ceir.CEIRPostman.Repository.app;

import com.ceir.CEIRPostman.model.app.Notification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	@Query("select noti from Notification noti where noti.status=?1 and upper(noti.channelType)=upper(?2)")
	List<Notification> findByStatusAndChannelType(int paramInt, String paramString);

	List<Notification> findByStatusAndChannelTypeAndOperatorNameAndModifiedOnGreaterThanEqual(int paramInt, String paramString1, String paramString2, LocalDateTime paramLocalDateTime);

	List<Notification> findByStatusAndRetryCountAndOperatorNameAndChannelType(int paramInt1, int paramInt2, String paramString1, String paramString2);

	List<Notification> findByStatusAndRetryCountAndOperatorNameAndChannelTypeAndSmsScheduledTimeLessThanEqual(int paramInt1, int paramInt2, String paramString1, String paramString2, LocalDateTime paramLocalDateTime);

	List<Notification> findByStatusAndRetryCountAndOperatorNameInAndChannelType(int paramInt1, int paramInt2, List<String> paramList, String paramString);

	List<Notification> findByStatusAndRetryCountAndOperatorNameInAndChannelTypeAndSmsScheduledTimeLessThanEqual(int paramInt1, int paramInt2, List<String> paramList, String paramString, LocalDateTime paramLocalDateTime);

	@Query("SELECT n FROM Notification n WHERE n.status = :status AND n.channelType = :channelType AND n.operatorName = :operatorName AND n.modifiedOn <= :modifiedOn AND n.retryCount >= :retryCount")
	List<Notification> findByStatusAndChannelTypeAndOperatorNameAndModifiedOnAndRetryCountGreaterThan(int paramInt1, String paramString1, String paramString2, LocalDateTime paramLocalDateTime, int paramInt2);

	@Query("SELECT n FROM Notification n WHERE n.status = :status AND n.channelType = :channelType AND n.operatorName IN :operatorNames AND n.modifiedOn <= :modifiedOn AND n.retryCount >= :retryCount")
	List<Notification> findByStatusAndChannelTypeAndOperatorNameInAndModifiedOnAndRetryCountGreaterThan(int paramInt1, String paramString, List<String> paramList, LocalDateTime paramLocalDateTime, int paramInt2);

	@Modifying
	@Query("UPDATE Notification n SET n.retryCount = :retryCount WHERE n.id = :id")
	void updateRetryCountById(Long paramLong, Integer paramInteger);

	@Modifying
	@Query("UPDATE Notification n SET n.status = :status WHERE n.id = :id")
	void updateStatusById(Long paramLong, Integer paramInteger);

	@Modifying
	@Query("UPDATE Notification n SET n.status = :status, n.notificationSentTime = :notificationSentTime, n.corelationId = :corelationId, n.deliveryStatus = :deliveryStatus WHERE n.id = :id")
	void updateStatusAndNotificationSentTimeAndCorelationIdById(Long paramLong, Integer paramInteger1, LocalDateTime paramLocalDateTime, String paramString, Integer paramInteger2);
}
