package com.ceir.CEIRPostman.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.ceir.CEIRPostman.constants.OperatorTypes;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ceir.CEIRPostman.Repository.NotificationRepository;
import com.ceir.CEIRPostman.RepositoryService.NotificationRepoImpl;
import com.ceir.CEIRPostman.RepositoryService.RunningAlertRepoService;
import com.ceir.CEIRPostman.RepositoryService.SystemConfigurationDbRepoImpl;
import com.ceir.CEIRPostman.model.Notification;
import com.ceir.CEIRPostman.model.RunningAlertDb;
import com.ceir.CEIRPostman.model.SystemConfigurationDb;

//@Service
public class SmsService implements Runnable {
     private final Logger log = Logger.getLogger(getClass());
    private final String operatorName;

    public SmsService(String operatorName) {
        this.operatorName = operatorName;
    }
     @Autowired
     private NotificationRepository notificationRepo;
     @Autowired
     private NotificationRepoImpl notificationRepoImpl;
     @Autowired
     private SystemConfigurationDbRepoImpl systemConfigRepoImpl;
     @Autowired
     private RunningAlertRepoService alertDbRepo;
     @Autowired
     private SmsSendFactory smsSendFactory;

     public void run() {
          String type = "SMS";
          String operatorNameArg = null;
          try{
              operatorNameArg = OperatorTypes.valueOf(operatorName.toUpperCase()).getValue();
              System.out.println("operator name: "+ operatorNameArg);
          } catch (IllegalArgumentException e) {
              RunningAlertDb alertDb = new RunningAlertDb("Alert1205", "Operator " + operatorNameArg + " does not exist", 0);
              alertDbRepo.saveAlertDb(alertDb);
          }
          SystemConfigurationDb tps;
          SystemConfigurationDb smsUrl;
          SystemConfigurationDb smsRetryCount = systemConfigRepoImpl.getDataByTag("sms_retry_count");
          SystemConfigurationDb fromSender;
          SystemConfigurationDb sleepTps = systemConfigRepoImpl.getDataByTag("sms_retry_interval");
          Integer smsRetryCountValue = 0;
          Integer sleepTimeinMilliSec = 0;
          Integer tpsValue = 0;
          String from = null;
          try {
              if (type == null) {
                  tps = systemConfigRepoImpl.getDataByTag("default_sms_tps");
                  fromSender = systemConfigRepoImpl.getDataByTag("default_sender_id");
              } else {
                  tps = systemConfigRepoImpl.getDataByTag(type+"_sms_tps");
                  fromSender = systemConfigRepoImpl.getDataByTag(type+"_sender_id");
              }
               smsRetryCountValue = Integer.parseInt(smsRetryCount.getValue());
               sleepTimeinMilliSec = Integer.parseInt(sleepTps.getValue());
               from  = fromSender.getValue();
               tpsValue = Integer.parseInt(tps.getValue());
               log.info("sms retry count value: " + smsRetryCountValue + ", sms retry interval: " + sleepTimeinMilliSec + " and tps: " + tps);
          } catch (Exception e) {
               RunningAlertDb alertDb = new RunningAlertDb("Alert1202", "DB connection OK but failed to\n" +
                       "read configuration value", 0);
               alertDbRepo.saveAlertDb(alertDb);
               log.info(e.toString());
          }
          while (true) {
               log.info("inside Sms process");
               try {
                    log.info("inside Sms process");
                    log.info("going to fetch data from notification table for operator="+operatorNameArg+", status=0, retryCount=0 and channel type="+type);
                    List<Notification> notificationData = notificationRepoImpl.dataByStatusAndRetryCountAndOperatorNameAndChannelType(0, 0, operatorNameArg, type);
                   LocalDateTime dateTime = LocalDateTime.now();
                    int totalMailsent = 0;
                    int totalMailNotsent = 0;
                    int smsSentCount = 0;
                    long tsms = System.currentTimeMillis();
                    if (!notificationData.isEmpty()) {
                         log.info("notification data is not empty and size is " + notificationData.size());
                         for (Notification notification : notificationData) {
                             if (smsSentCount >= tpsValue) {
                                 long tsdiff = System.currentTimeMillis() - tsms;
                                 if (tsdiff < 1000) {
                                     smsSentCount = 0;
                                     Thread.sleep(1000 - tsdiff);
                                 } else if (tsdiff == 1000) {
                                     smsSentCount = 0;
                                 } else {
                                     smsSentCount = 0;
                                     RunningAlertDb alertDb = new RunningAlertDb("Alert1204", "TPS not achieved", 0);
                                     alertDbRepo.saveAlertDb(alertDb);
                                 }
                                 tsms = System.currentTimeMillis();
                             }
                             log.info("notification data id= " + notification.getId());
                             if (Objects.nonNull(notification.getMsisdn()) && Objects.nonNull(notification.getOperatorName())) {
                                 SmsManagementService smsProvider = smsSendFactory.getSmsManagementService(notification.getOperatorName());
                                 String correlationId = UUID.randomUUID().toString();
                                 String smsStatus = smsProvider.sendSms(notification.getMsisdn(), from, notification.getMessage(), correlationId, notification.getMsgLang());
                                 if (smsStatus == "SUCCESS") {
                                     LocalDateTime now = LocalDateTime.now();
                                     notification.setStatus(1);
                                     notification.setNotificationSentTime(now);
                                     notification.setCorelationId(correlationId);
                                 } else if (smsStatus == "FAILED") {
                                     //check retry count if >3 update status to 2 else increase retry count|| if 5xx then raise alarm
                                     if (notification.getRetryCount() < smsRetryCountValue) {
                                         notification.setRetryCount(notification.getRetryCount() + 1);
                                     } else {
                                         notification.setStatus(2);
                                     }
                                     RunningAlertDb alertDb = new RunningAlertDb("Alert1206", "Send SMS failed for " + operatorNameArg, 0);
                                     alertDbRepo.saveAlertDb(alertDb);
                                 } else if (smsStatus == "SERVICE_UNAVAILABLE") {
                                     RunningAlertDb alertDb = new RunningAlertDb("Alert1203", "Operator " + operatorNameArg + " is down", 0);
                                     alertDbRepo.saveAlertDb(alertDb);
                                 } else {
                                     log.info("error in sending Sms for "+operatorNameArg);
                                     //some alert
                                     RunningAlertDb alertDb = new RunningAlertDb("Alert1201", "Database connection failed or login failed due to credentials", 0);
                                     alertDbRepo.saveAlertDb(alertDb);
                                     System.exit(0);
                                 }
                                 notificationRepo.save(notification);
                                 smsSentCount++;
                             }
                         }

                         log.info("total sms sent=  " + totalMailsent);
                         log.info("sms failed to send: " + totalMailNotsent);
                    } else {
                         log.info("notification data is  empty");
                         log.info(" so no sms is pending to send");
                    }
                   log.info("retrying for failed sms");
                   LocalDateTime newDateTime = dateTime.withNano(dateTime.getNano() + sleepTimeinMilliSec * 1000000);
                   List<Notification> notificationDataForRetries = notificationRepoImpl.findByStatusAndChannelTypeAndOperatorNameAndModifiedOnGreaterThanEqualTo(0, type, newDateTime, operatorNameArg);
                   if (!notificationDataForRetries.isEmpty()) {
                       log.info("notification for retry data is not empty and size is " + notificationDataForRetries.size());
                       for (Notification notification : notificationDataForRetries) {
                           if (smsSentCount >= tpsValue) {
                               long tsdiff = System.currentTimeMillis() - tsms;
                               if (tsdiff < 1000) {
                                   smsSentCount = 0;
                                   Thread.sleep(1000 - tsdiff);
                               } else if (tsdiff == 1000) {
                                   smsSentCount = 0;
                               } else {
                                   smsSentCount = 0;
                                   RunningAlertDb alertDb = new RunningAlertDb("Alert1204", "TPS not achieved", 0);
                                   alertDbRepo.saveAlertDb(alertDb);
                               }
                               tsms = System.currentTimeMillis();
                           }
                           log.info("retrying notification data id= " + notification.getId());
                           if (Objects.nonNull(notification.getMsisdn()) && Objects.nonNull(notification.getOperatorName())) {
                               SmsManagementService smsProvider = smsSendFactory.getSmsManagementService(notification.getOperatorName());
                               String correlationId = UUID.randomUUID().toString();
                               String smsStatus = smsProvider.sendSms(notification.getMsisdn(), from, notification.getMessage(), correlationId, notification.getMsgLang());
                               if (smsStatus == "SUCCESS") {
                                   LocalDateTime now = LocalDateTime.now();
                                   notification.setStatus(1);
                                   notification.setNotificationSentTime(now);
                                   notification.setCorelationId(correlationId);
                               } else if (smsStatus == "FAILED") {
                                   if (notification.getRetryCount() < smsRetryCountValue) {
                                       notification.setRetryCount(notification.getRetryCount() + 1);
                                   } else {
                                       notification.setStatus(2);
                                   }
                               } else if (smsStatus == "SERVICE UNAVAILABLE") {
                                   RunningAlertDb alertDb = new RunningAlertDb("Alert1203", "Operator " + operatorNameArg + " is down", 0);
                                   alertDbRepo.saveAlertDb(alertDb);
                               }
                               notificationRepo.save(notification);
                           }
                       }

                       log.info("total sms sent=  " + totalMailsent);
                       log.info("sms failed to send: " + totalMailNotsent);
                   }
               } catch (Exception e) {
                    log.info("error in sending Sms");
                    log.info(e.toString());
                    log.info(e.toString());
                    RunningAlertDb alertDb = new RunningAlertDb("Alert1201", "Database connection failed or login failed due to credentials", 0);
                    alertDbRepo.saveAlertDb(alertDb);
               }
               log.info("exit from  service");
               System.exit(0);
          }
     }
}



