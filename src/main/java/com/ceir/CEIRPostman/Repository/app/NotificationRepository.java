package com.ceir.CEIRPostman.Repository.app;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ceir.CEIRPostman.model.app.Notification;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query("select noti from Notification noti where noti.status=?1 and upper(noti.channelType)=upper(?2)")
	public List<Notification> findByStatusAndChannelType(int status,String channelType);

//	@Query("select n from Notification n where n.status= :status and upper(n.channelType)=upper(:channelType) and n.operator_name= :operatorName and n.modified_on >= :modifiedOn")
	public List<Notification> findByStatusAndChannelTypeAndOperatorNameAndModifiedOnGreaterThanEqual(int status, String channelType, String operatorName, LocalDateTime modifiedOn);

	public List<Notification> findByStatusAndRetryCountAndOperatorNameAndChannelType(int status, int retryCount, String operatorName, String channelType);
	public List<Notification> findByStatusAndRetryCountAndChannelTypeAndSmsScheduledTimeLessThanEqual(int status, int retryCount, String channelType, LocalDateTime smsScheduledTime);

	public List<Notification> findByStatusAndRetryCountAndChannelType(int status, int retryCount, String channelType);
	public List<Notification> findByStatusAndRetryCountAndOperatorNameInAndChannelTypeAndSmsScheduledTimeLessThanEqual(int status, int retryCount, List<String> operatorNames, String channelType, LocalDateTime smsScheduledTime);

	@Query("SELECT n FROM Notification n WHERE n.status = :status AND n.channelType = :channelType " +
			"AND n.modifiedOn <= :modifiedOn AND n.retryCount >= :retryCount")
	List<Notification> findByStatusAndChannelTypeAndModifiedOnAndRetryCountGreaterThan(
			int status, String channelType, LocalDateTime modifiedOn, int retryCount);

	@Query("SELECT n FROM Notification n WHERE n.status = :status AND n.channelType = :channelType " +
			"AND n.operatorName IN :operatorNames AND n.modifiedOn <= :modifiedOn AND n.retryCount >= :retryCount")
	List<Notification> findByStatusAndChannelTypeAndOperatorNameInAndModifiedOnAndRetryCountGreaterThan(
			int status, String channelType, List<String> operatorNames, LocalDateTime modifiedOn, int retryCount);

	@Modifying
	@Query("UPDATE Notification n SET n.retryCount = :retryCount WHERE n.id = :id")
	void updateRetryCountById(Long id, Integer retryCount);

	@Modifying
	@Query("UPDATE Notification n SET n.status = :status WHERE n.id = :id")
	void updateStatusById(Long id, Integer status);


	@Modifying
	@Query("UPDATE Notification n SET n.status = :status, n.notificationSentTime = :notificationSentTime, n.corelationId = :corelationId, n.deliveryStatus = :deliveryStatus WHERE n.id = :id")
	void updateStatusAndNotificationSentTimeAndCorelationIdById(Long id, Integer status, LocalDateTime notificationSentTime, String corelationId, Integer deliveryStatus);

}
