package com.ceir.CEIRPostman.Repository.app;

import com.ceir.CEIRPostman.model.app.Notification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	// Find notifications by status and channel type (case-insensitive for channelType)
	@Query("SELECT n FROM Notification n WHERE n.status = :status AND UPPER(n.channelType) = UPPER(:channelType)")
	List<Notification> findByStatusAndChannelType(@Param("status") int status, @Param("channelType") String channelType);

	// Find notifications by status, channel type, operator name, and modified date
	List<Notification> findByStatusAndChannelTypeAndOperatorNameAndModifiedOnGreaterThanEqual(
			int status,
			String channelType,
			String operatorName,
			LocalDateTime modifiedOn
	);

	// Find notifications by status, retry count, operator name, and channel type
	List<Notification> findByStatusAndRetryCountAndOperatorNameAndChannelType(
			int status,
			int retryCount,
			String operatorName,
			String channelType
	);

	// Find notifications by status, retry count, operator name, channel type, and scheduled time
	List<Notification> findByStatusAndRetryCountAndOperatorNameAndChannelTypeAndSmsScheduledTimeLessThanEqual(
			int status,
			int retryCount,
			String operatorName,
			String channelType,
			LocalDateTime smsScheduledTime
	);

	// Find notifications by status, retry count, multiple operator names, and channel type
	List<Notification> findByStatusAndRetryCountAndOperatorNameInAndChannelType(
			int status,
			int retryCount,
			List<String> operatorNames,
			String channelType
	);

	// Find notifications by status, retry count, multiple operator names, channel type, and scheduled time
	List<Notification> findByStatusAndRetryCountAndOperatorNameInAndChannelTypeAndSmsScheduledTimeLessThanEqual(
			int status,
			int retryCount,
			List<String> operatorNames,
			String channelType,
			LocalDateTime smsScheduledTime
	);

	// Custom query: Find notifications by multiple criteria
	@Query("SELECT n FROM Notification n WHERE n.status = :status AND n.channelType = :channelType AND n.operatorName = :operatorName AND n.modifiedOn <= :modifiedOn AND n.retryCount >= :retryCount")
	List<Notification> findByStatusAndChannelTypeAndOperatorNameAndModifiedOnAndRetryCountGreaterThan(
			@Param("status") int status,
			@Param("channelType") String channelType,
			@Param("operatorName") String operatorName,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("retryCount") int retryCount
	);

	// Custom query: Find notifications with multiple operator names and other criteria
	@Query("SELECT n FROM Notification n WHERE n.status = :status AND n.channelType = :channelType AND n.operatorName IN :operatorNames AND n.modifiedOn <= :modifiedOn AND n.retryCount >= :retryCount")
	List<Notification> findByStatusAndChannelTypeAndOperatorNameInAndModifiedOnAndRetryCountGreaterThan(
			@Param("status") int status,
			@Param("channelType") String channelType,
			@Param("operatorNames") List<String> operatorNames,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("retryCount") int retryCount
	);

	// Update retry count by ID
	@Modifying
	@Query("UPDATE Notification n SET n.retryCount = :retryCount WHERE n.id = :id")
	void updateRetryCountById(@Param("id") Long id, @Param("retryCount") Integer retryCount);

	// Update status by ID
	@Modifying
	@Query("UPDATE Notification n SET n.status = :status WHERE n.id = :id")
	void updateStatusById(@Param("id") Long id, @Param("status") Integer status);

	// Update status, notification sent time, correlation ID, and delivery status by ID
	@Modifying
	@Query("UPDATE Notification n SET n.status = :status, n.notificationSentTime = :notificationSentTime, n.corelationId = :corelationId, n.deliveryStatus = :deliveryStatus WHERE n.id = :id")
	void updateStatusAndNotificationSentTimeAndCorelationIdById(
			@Param("id") Long id,
			@Param("status") Integer status,
			@Param("notificationSentTime") LocalDateTime notificationSentTime,
			@Param("corelationId") String corelationId,
			@Param("deliveryStatus") Integer deliveryStatus
	);
}
