package com.ceir.CEIRPostman.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;

import org.hibernate.annotations.ParamDef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ceir.CEIRPostman.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query("select noti from Notification noti where noti.status=?1 and upper(noti.channelType)=upper(?2)")
	public List<Notification> findByStatusAndChannelType(int status,String channelType);

//	@Query("select n from Notification n where n.status= :status and upper(n.channelType)=upper(:channelType) and n.operator_name= :operatorName and n.modified_on >= :modifiedOn")
	public List<Notification> findByStatusAndChannelTypeAndOperatorNameAndModifiedOnGreaterThanEqual(int status, String channelType, String operatorName, LocalDateTime modifiedOn);

	public List<Notification> findByStatusAndRetryCountAndOperatorNameAndChannelType(int status, int retryCount, String operatorName, String channelType);

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
