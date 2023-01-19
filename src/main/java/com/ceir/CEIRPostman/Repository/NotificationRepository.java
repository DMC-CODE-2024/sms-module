package com.ceir.CEIRPostman.Repository;
import java.util.List;
import java.util.Queue;

import org.hibernate.annotations.ParamDef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ceir.CEIRPostman.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification>{

	@Query("select noti from Notification noti where noti.status=?1 and upper(noti.channelType)=upper(?2)")
	public List<Notification> findByStatusAndChannelType(int status,String channelType);
	public List<Notification> findByStatus(int status);
}
