package com.ceir.CEIRPostman.RepositoryService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ceir.CEIRPostman.Repository.NotificationRepository;
import com.ceir.CEIRPostman.model.Notification;
@Service
public class NotificationRepoImpl {

	@Autowired
	NotificationRepository notificationRepository;
	
	private final Logger log = Logger.getLogger(getClass());
	public List<Notification> dataByStatusAndRetryCountAndOperatorNameAndChannelType(int status,int retryCount, String operatorName, String channelType) {
		try {
			List<Notification> notification=notificationRepository.findByStatusAndRetryCountAndOperatorNameAndChannelType(status,retryCount, operatorName, channelType);
		    return notification;
		}
		catch(Exception e) {
			log.info("error occurs while fetch notification data");
			log.info(e.toString());
            return new ArrayList<Notification>();
		}
	}
	
//	public List<Notification> dataByStatus(int status) {
//		try {
//			List<Notification> notification=notificationRepository.findByStatus(status);
//		    return notification;
//		}
//		catch(Exception e) {
//			log.info(e.toString());
//            return new ArrayList<Notification>();
//		}
//	}
	public List<Notification> findByStatusAndChannelTypeAndOperatorNameAndModifiedOnGreaterThanEqualTo(int status, String channelType, LocalDateTime modifiedOn, String operatorName) {
		try {
			List<Notification> notification=notificationRepository.findByStatusAndChannelTypeAndOperatorNameAndModifiedOnGreaterThanEqual(status, channelType, operatorName, modifiedOn);
			return notification;
		}
		catch(Exception e) {
			log.info(e.toString());
			return new ArrayList<Notification>();
		}
	}

}
