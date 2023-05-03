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

public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification>{

	@Query("select noti from Notification noti where noti.status=?1 and upper(noti.channelType)=upper(?2)")
	public List<Notification> findByStatusAndChannelType(int status,String channelType);

	@Query("select n from Notification n where n.modified_on >= :modifiedOn and n.status= :status and n.operator_name= :operatorName and upper(n.channelType)=upper(:channelType)")
	public List<Notification> findByStatusAndChannelTypeAndOperatorNameAndModifiedOnGreaterThanEqualTo(int status, String channelType, LocalDateTime modifiedOn, String operatorName);

	public List<Notification> findByStatusAndRetryCountAndOperatorNameAndChannelType(int status, int retryCount, String operatorName, String channelType);

	@Modifying
	@Query("UPDATE Notification n SET n.retryCount = :retryCount WHERE n.id = :id")
	void updateRetryCountById(Long id, Integer retryCount);

	@Modifying
	@Query("UPDATE Notification n SET n.status = :status WHERE n.id = :id")
	void updateStatusById(Long id, Integer status);


	@Modifying
	@Query("UPDATE Notification n SET n.status = :status and n.notification_sent_time = :notificationSentTime and corelation_id = :corelationId and delivery_status = :deliveryStatus WHERE n.id = :id")
	void updateStatusAndNotificationSentTimeAndCorelationIdById(Long id, Integer status, LocalDateTime notificationSentTime, String corelationId, Integer deliveryStatus);
}
