package com.ceir.CEIRPostman.RepositoryService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ceir.CEIRPostman.Repository.app.NotificationRepository;
import com.ceir.CEIRPostman.model.app.Notification;
@Service
public class NotificationRepoImpl {

	@Autowired
	NotificationRepository notificationRepository;
	
	private final Logger log = LogManager.getLogger(getClass());
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

	public List<Notification> dataByStatusAndRetryCountAndOperatorNameInAndChannelType(int status,int retryCount, List<String> operatorNames, String channelType) {
		try {
			List<Notification> notification=notificationRepository.findByStatusAndRetryCountAndOperatorNameInAndChannelType(status,retryCount, operatorNames, channelType);
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
	public List<Notification> findByStatusAndChannelTypeAndOperatorNameAndModifiedOnAndRetryCountGreaterThanEqualTo(int status, String type, String operatorName, LocalDateTime modifiedOn, int retryCount) {
		try {
			List<Notification> notification=notificationRepository.findByStatusAndChannelTypeAndOperatorNameAndModifiedOnAndRetryCountGreaterThan(status, type, operatorName, modifiedOn, retryCount);
			return notification;
		}
		catch(Exception e) {
			log.info(e.toString());
			return new ArrayList<Notification>();
		}
	}

	public List<Notification> findByStatusAndChannelTypeAndOperatorNameInAndModifiedOnAndRetryCountGreaterThanEqualTo(int status, String type, List<String> operatorNames, LocalDateTime modifiedOn, int retryCount) {
		try {
			List<Notification> notification=notificationRepository.findByStatusAndChannelTypeAndOperatorNameInAndModifiedOnAndRetryCountGreaterThan(status, type, operatorNames, modifiedOn, retryCount);
			return notification;
		}
		catch(Exception e) {
			log.info(e.toString());
			return new ArrayList<Notification>();
		}
	}
}
