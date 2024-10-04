package com.ceir.CEIRPostman.RepositoryService;

import com.ceir.CEIRPostman.Repository.app.NotificationRepository;
import com.ceir.CEIRPostman.model.app.Notification;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationRepoImpl {
	@Autowired
	NotificationRepository notificationRepository;

	private final Logger log = LogManager.getLogger(getClass());

	public List<Notification> dataByStatusAndRetryCountAndOperatorNameAndChannelType(int status, int retryCount, String operatorName, String channelType) {
		try {
			List<Notification> notification = this.notificationRepository.findByStatusAndRetryCountAndOperatorNameAndChannelType(status, retryCount, operatorName, channelType);
			return notification;
		} catch (Exception e) {
			this.log.info("error occurs while fetch notification data");
			this.log.info(e.toString());
			return new ArrayList<>();
		}
	}

	public List<Notification> dataByStatusAndRetryCountAndOperatorNameAndChannelTypeV2(int status, int retryCount, String operatorName, String channelType) {
		try {
			List<Notification> notification = this.notificationRepository.findByStatusAndRetryCountAndOperatorNameAndChannelTypeAndSmsScheduledTimeLessThanEqual(status, retryCount, operatorName, channelType, LocalDateTime.now());
			return notification;
		} catch (Exception e) {
			this.log.info("error occurs while fetch notification data");
			this.log.info(e.toString());
			return new ArrayList<>();
		}
	}

	public List<Notification> dataByStatusAndRetryCountAndOperatorNameInAndChannelType(int status, int retryCount, List<String> operatorNames, String channelType) {
		try {
			List<Notification> notification = this.notificationRepository.findByStatusAndRetryCountAndOperatorNameInAndChannelType(status, retryCount, operatorNames, channelType);
			return notification;
		} catch (Exception e) {
			this.log.info("error occurs while fetch notification data");
			this.log.info(e.toString());
			return new ArrayList<>();
		}
	}

	public List<Notification> dataByStatusAndRetryCountAndOperatorNameInAndChannelTypeV2(int status, int retryCount, List<String> operatorNames, String channelType) {
		try {
			List<Notification> notification = this.notificationRepository.findByStatusAndRetryCountAndOperatorNameInAndChannelTypeAndSmsScheduledTimeLessThanEqual(status, retryCount, operatorNames, channelType, LocalDateTime.now());
			return notification;
		} catch (Exception e) {
			this.log.info("error occurs while fetch notification data");
			this.log.info(e.toString());
			return new ArrayList<>();
		}
	}

	public List<Notification> findByStatusAndChannelTypeAndOperatorNameAndModifiedOnAndRetryCountGreaterThanEqualTo(int status, String type, String operatorName, LocalDateTime modifiedOn, int retryCount) {
		try {
			List<Notification> notification = this.notificationRepository.findByStatusAndChannelTypeAndOperatorNameAndModifiedOnAndRetryCountGreaterThan(status, type, operatorName, modifiedOn, retryCount);
			return notification;
		} catch (Exception e) {
			this.log.info(e.toString());
			return new ArrayList<>();
		}
	}

	public List<Notification> findByStatusAndChannelTypeAndOperatorNameInAndModifiedOnAndRetryCountGreaterThanEqualTo(int status, String type, List<String> operatorNames, LocalDateTime modifiedOn, int retryCount) {
		try {
			List<Notification> notification = this.notificationRepository.findByStatusAndChannelTypeAndOperatorNameInAndModifiedOnAndRetryCountGreaterThan(status, type, operatorNames, modifiedOn, retryCount);
			return notification;
		} catch (Exception e) {
			this.log.info(e.toString());
			return new ArrayList<>();
		}
	}
}
