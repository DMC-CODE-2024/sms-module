package com.ceir.CEIRPostman.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.ceir.CEIRPostman.Repository.app.CfgFeatureAlertRepository;
import com.ceir.CEIRPostman.Repository.app.OperatorRepository;
import com.ceir.CEIRPostman.Repository.audit.ModulesAuditTrailRepository;
import com.ceir.CEIRPostman.constants.ChannelType;
import com.ceir.CEIRPostman.constants.SmsType;
import com.ceir.CEIRPostman.model.app.CfgFeatureAlert;
import com.ceir.CEIRPostman.model.app.Operator;
import com.ceir.CEIRPostman.util.VirtualIpAddressUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import com.ceir.CEIRPostman.Repository.app.NotificationRepository;
import com.ceir.CEIRPostman.RepositoryService.NotificationRepoImpl;
import com.ceir.CEIRPostman.RepositoryService.RunningAlertRepoService;
import com.ceir.CEIRPostman.RepositoryService.SystemConfigurationDbRepoImpl;
import com.ceir.CEIRPostman.model.app.Notification;
import com.ceir.CEIRPostman.model.app.SystemConfigurationDb;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class SmsService implements Runnable {
    Integer smsSleepTimer = 1000;

    String operatorName = "macra";
    private final Logger log = LogManager.getLogger(getClass());

    public SmsService() {
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
     @Autowired
     private CfgFeatureAlertRepository cfgFeatureAlertRepository;
     @Autowired
     private ModulesAuditTrailRepository modulesAuditTrailRepository;
     @Autowired
     private OperatorRepository operatorRepository;
     @Autowired
     private VirtualIpAddressUtil virtualIpAddressUtil;

    int successCount = 0;
    int failureCount = 0;
    Integer smsRetryCountValue = 0;
    Integer sleepTimeinMilliSec = 0;
    Integer tpsValue = 0;
    String from = null;
    String operatorNameArg = "macra";
    Operator operatorEntity = new Operator("macra_custom");

     public void run() {
          SystemConfigurationDb tps;
          Optional<SystemConfigurationDb> sleepTimerConfig = Optional.ofNullable(systemConfigRepoImpl.getDataByTag("smsSleepTimer"));
          sleepTimerConfig.ifPresent(systemConfigurationDb -> smsSleepTimer = Integer.parseInt(systemConfigurationDb.getValue()));
          SystemConfigurationDb smsRetryCount = systemConfigRepoImpl.getDataByTag("sms_retry_count");
          SystemConfigurationDb fromSender;
          SystemConfigurationDb sleepTps = systemConfigRepoImpl.getDataByTag("sms_retry_interval");
          try {

              tps = Optional.of(systemConfigRepoImpl.getDataByTag(operatorNameArg+"_sms_tps")).get();
              fromSender = Optional.of(systemConfigRepoImpl.getDataByTag(operatorNameArg+"_sender_id")).get();
              smsRetryCountValue = Integer.parseInt(smsRetryCount.getValue());
              sleepTimeinMilliSec = Integer.parseInt(sleepTps.getValue());
              from  = fromSender.getValue();
              tpsValue = Integer.parseInt(tps.getValue());
              log.info("sms retry count value: " + smsRetryCountValue + ", sms retry interval: " + sleepTimeinMilliSec + " and tps: " + tpsValue);
              while (true) {
                  if (true) {
                      sendSMS();
                      sleepForSeconds(smsSleepTimer);
                  } else {
                      log.info("VIP not found. Sleeping for " + smsSleepTimer + " seconds.");
                      sleepForSeconds(smsSleepTimer);
                  }
              }
          } catch (Exception e) {
              e.printStackTrace();
              log.error("Raising alert1202");
              System.out.println("Raising alert1202");
              Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1202");
              alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), this.operatorName, "SMS_MODULE", 0));
              System.exit(0);
          }
     }

     public void sendSMS() {
         try {
             log.info("fetching pending otp requests");
             List<Notification> otpNotificationData = notificationRepoImpl.dataByStatusAndRetryCountAndChannelType(0, 0, SmsType.SMS_OTP.name());
             int otpSmsSentCount = 0;
             long otpTsms = System.currentTimeMillis();
             if (!otpNotificationData.isEmpty()) {
                 LocalDateTime now = LocalDateTime.now();
                 log.info("notification data is not empty and size is " + otpNotificationData.size());
                 for (Notification notification : otpNotificationData) {
                     this.operatorName = notification.getOperatorName();
                     if("default".equals(notification.getOperatorName())) {
                         Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1207");
                         log.error("Raising alert1207");
                         System.out.println("Raising alert1207");
                         alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), notification.getMsisdn(), "SMS_MODULE", 0));
                     }
                     if (otpSmsSentCount >= tpsValue) {
                         long tsdiff = System.currentTimeMillis() - otpTsms;
                         if (tsdiff < 1000) {
                             otpSmsSentCount = 0;
                             Thread.sleep(1000 - tsdiff);
                         } else if (tsdiff == 1000) {
                             otpSmsSentCount = 0;
                         } else {
                             otpSmsSentCount = 0;
                             Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1204");
                             log.error("Raising alert1204");
                             System.out.println("Raising alert1204");
                             alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), operatorName, "SMS_MODULE", 0));
                         }
                         otpTsms = System.currentTimeMillis();
                     }
                     log.info("notification data id= " + notification.getId());
                     if (Objects.nonNull(notification.getMsisdn()) && Objects.nonNull(notification.getOperatorName())) {
                         SmsManagementService smsProvider = smsSendFactory.getSmsManagementService(notification.getOperatorName(), operatorEntity.getChannelType());
                         String correlationId = UniqueIdGenerator.generateUniqueId(operatorName);
                         String smsStatus = smsProvider.sendSms(operatorNameArg, notification.getMsisdn(), from, notification.getMessage(), correlationId, notification.getMsgLang());
                         if (Objects.equals(smsStatus, "SUCCESS")) {
                             successCount = successCount + 1;
                             notification.setStatus(1);
                             notification.setNotificationSentTime(now);
                             notification.setCorelationId(correlationId);
                         } else if (Objects.equals(smsStatus, "FAILED")) {
                             failureCount = failureCount + 1;
                             notification.setStatus(2);
                             Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1206");
                             log.error("Raising alert1206");
                             System.out.println("Raising alert1206");
                             alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), operatorName, "SMS_MODULE", 0));
                         } else if (Objects.equals(smsStatus, "SERVICE_UNAVAILABLE")) {
                             Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1203");
                             log.error("Raising alert1203");
                             System.out.println("Raising alert1203");
                             alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), operatorName, "SMS_MODULE", 0));
//                             ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", "Service Unavailable for "+operatorName, featureName, "UPDATE", "Alert1203", 0 , 0, executionStartTime, startTime);
//                             modulesAuditTrailRepository.save(tacAudit);
                         } else {
                             log.info("error in sending Sms for "+operatorNameArg);
                             Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1206");
                             log.error("Raising alert1206");
                             System.out.println("Raising alert1206");
                             alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), operatorName, "SMS_MODULE", 0));
//                             ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", "Send SMS status unknown "+operatorName, featureName, "UPDATE", "Alert1206", 0 , 0, executionStartTime, startTime);
//                             modulesAuditTrailRepository.save(tacAudit);
                         }
                         if(!operatorName.equals("default")) {
                             notification.setSendSmsInterface(operatorName);
                         } else {
                             SystemConfigurationDb defaultAggType = systemConfigRepoImpl.getDataByTag("default_agg_type");
                             if (defaultAggType.getValue().equals("Operator")) {
                                 SystemConfigurationDb defaultOperatorName = systemConfigRepoImpl.getDataByTag("default_operator_name");
                                 notification.setSendSmsInterface(defaultOperatorName.getValue());
                             } else if (defaultAggType.getValue().equals("Aggregator")) {
                                 notification.setSendSmsInterface("aggregator");
                             }
                         }

                         notificationRepo.save(notification);
                         otpSmsSentCount++;
                     }
                 }
             } else {
                 log.info("no otp requests pending");
             }

             log.info("fetching sms requests");
             List<Notification> notificationData = notificationRepoImpl.dataByStatusAndRetryCountAndChannelTypeV2(0, 0, SmsType.SMS.name());
             int smsSentCount = 0;
             long tsms = System.currentTimeMillis();
             if (!notificationData.isEmpty()) {
                 log.info("notification data is not empty and size is " + notificationData.size());
                 for (Notification notification : notificationData) {
                     this.operatorName = notification.getOperatorName();
                     LocalDateTime now = LocalDateTime.now();
                     if("default".equals(notification.getOperatorName())) {
                         Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1207");
                         log.error("Raising alert1207");
                         System.out.println("Raising alert1207");
                         alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), notification.getMsisdn(), "SMS_MODULE", 0));
                     }
                     if (smsSentCount >= tpsValue) {
                         long tsdiff = System.currentTimeMillis() - tsms;
                         if (tsdiff < 1000) {
                             smsSentCount = 0;
                             Thread.sleep(1000 - tsdiff);
                         } else if (tsdiff == 1000) {
                             smsSentCount = 0;
                         } else {
                             smsSentCount = 0;
                             Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1204");
                             log.error("Raising alert1204");
                             System.out.println("Raising alert1204");
                             alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), operatorName, "SMS_MODULE", 0));
//                             ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", "TPS not Achieved", featureName, "UPDATE", "Alert1204", 0 , 0, executionStartTime, startTime);
//                             modulesAuditTrailRepository.save(tacAudit);
                         }
                         tsms = System.currentTimeMillis();
                     }
                     log.info("notification data id= " + notification.getId());
                     if (Objects.nonNull(notification.getMsisdn()) && Objects.nonNull(notification.getOperatorName())) {
                         SmsManagementService smsProvider = smsSendFactory.getSmsManagementService(notification.getOperatorName(), operatorEntity.getChannelType());
                         String correlationId = UniqueIdGenerator.generateUniqueId(operatorName);
                         String smsStatus = smsProvider.sendSms(operatorNameArg, notification.getMsisdn(), from, notification.getMessage(), correlationId, notification.getMsgLang());
                         if (Objects.equals(smsStatus, "SUCCESS")) {
                             successCount = successCount + 1;
                             notification.setStatus(1);
                             notification.setNotificationSentTime(now);
                             notification.setCorelationId(correlationId);
                         } else if (Objects.equals(smsStatus, "FAILED")) {
                             failureCount = failureCount + 1;
                             //check retry count if >3 update status to 2 else increase retry count|| if 5xx then raise alarm
                             if (notification.getRetryCount() < smsRetryCountValue) {
                                 notification.setRetryCount(notification.getRetryCount() + 1);
                             } else {
                                 notification.setStatus(2);
                                 Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1206");
                                 log.error("Raising alert1206");
                                 System.out.println("Raising alert1206");
                                 alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), operatorName, "SMS_MODULE", 0));
//                                 ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", "Send SMS failed for "+operatorName, featureName, "UPDATE", "Alert1206", 0 , 0, executionStartTime, startTime);
//                                 modulesAuditTrailRepository.save(tacAudit);
                             }
                         } else if (Objects.equals(smsStatus, "SERVICE_UNAVAILABLE")) {
                             Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1203");
                             log.error("Raising alert1203");
                             System.out.println("Raising alert1203");
                             alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), operatorName, "SMS_MODULE", 0));
//                             ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", "Service Unavailable for "+operatorName, featureName, "UPDATE", "Alert1203", 0 , 0, executionStartTime, startTime);
//                             modulesAuditTrailRepository.save(tacAudit);
                         } else {
                             log.info("error in sending Sms for "+operatorNameArg);
                             Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1206");
                             log.error("Raising alert1206");
                             System.out.println("Raising alert1206");
                             alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), operatorName, "SMS_MODULE", 0));
//                             ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", "Send SMS status unknown "+operatorName, featureName, "UPDATE", "Alert1206", 0 , 0, executionStartTime, startTime);
//                             modulesAuditTrailRepository.save(tacAudit);
                         }
                         if(!operatorName.equals("default")) {
                             notification.setSendSmsInterface(operatorName);
                         } else {
                             SystemConfigurationDb defaultAggType = systemConfigRepoImpl.getDataByTag("default_agg_type");
                             if (defaultAggType.getValue().equals("Operator")) {
                                 SystemConfigurationDb defaultOperatorName = systemConfigRepoImpl.getDataByTag("default_operator_name");
                                 notification.setSendSmsInterface(defaultOperatorName.getValue());
                             } else if (defaultAggType.getValue().equals("Aggregator")) {
                                 notification.setSendSmsInterface("aggregator");
                             }
                         }

                         notificationRepo.save(notification);
                         smsSentCount++;
                     }
                 }
             } else {
                 log.info("no sms requests pending");
             }

             log.info("retrying for failed sms");
             LocalDateTime dateTime = LocalDateTime.now();
             LocalDateTime newDateTime = dateTime.plusNanos(dateTime.getNano() - sleepTimeinMilliSec * 1000000);
             List<Notification> notificationDataForRetries = notificationRepoImpl.findByStatusAndChannelTypeAndModifiedOnAndRetryCountGreaterThanEqualTo(0, SmsType.SMS.name(), newDateTime, 1);
             if (!notificationDataForRetries.isEmpty()) {
                 LocalDateTime now = LocalDateTime.now();
                 log.info("notification for retry data is not empty and size is " + notificationDataForRetries.size());
                 for (Notification notification : notificationDataForRetries) {
                     this.operatorName = notification.getOperatorName();
                     if("default".equals(notification.getOperatorName())) {
                         Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1207");
                         log.error("Raising alert1207");
                         System.out.println("Raising alert1207");
                         alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), notification.getMsisdn(), "SMS_MODULE", 0));
                     }
                     if (smsSentCount >= tpsValue) {
                         long tsdiff = System.currentTimeMillis() - tsms;
                         if (tsdiff < 1000) {
                             smsSentCount = 0;
                             Thread.sleep(1000 - tsdiff);
                         } else if (tsdiff == 1000) {
                             smsSentCount = 0;
                         } else {
                             smsSentCount = 0;
                             Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1204");
                             log.error("Raising alert1204");
                             System.out.println("Raising alert1204");
                             alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), operatorName, "SMS_MODULE", 0));
//                             ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", "TPS not achieved", featureName, "UPDATE", "Alert1204", 0 , 0, executionStartTime, startTime);
//                             modulesAuditTrailRepository.save(tacAudit);
                         }
                         tsms = System.currentTimeMillis();
                     }
                     log.info("retrying notification data id= " + notification.getId());
                     if (Objects.nonNull(notification.getMsisdn()) && Objects.nonNull(notification.getOperatorName())) {
                         SmsManagementService smsProvider = smsSendFactory.getSmsManagementService(notification.getOperatorName(), operatorEntity.getChannelType());
                         String correlationId = UniqueIdGenerator.generateUniqueId(operatorName);
                         String smsStatus = smsProvider.sendSms(operatorNameArg, notification.getMsisdn(), from, notification.getMessage(), correlationId, notification.getMsgLang());
                         if (Objects.equals(smsStatus, "SUCCESS")) {
                             successCount = successCount + 1;
                             notification.setStatus(1);
                             notification.setNotificationSentTime(now);
                             notification.setCorelationId(correlationId);
                         } else if (Objects.equals(smsStatus, "FAILED")) {
                             failureCount = failureCount + 1;
                             if (notification.getRetryCount() < smsRetryCountValue) {
                                 notification.setRetryCount(notification.getRetryCount() + 1);
                             } else {
                                 notification.setStatus(2);
                                 Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1206");
                                 log.error("Raising alert1206");
                                 System.out.println("Raising alert1206");
                                 alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), operatorName, "SMS_MODULE", 0));
//                                 ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 200, "NA", "Send SMS failed for "+operatorName, featureName, "UPDATE", "Alert1206", 0 , 0, executionStartTime, startTime);
//                                 modulesAuditTrailRepository.save(tacAudit);
                             }
                         } else if (Objects.equals(smsStatus, "SERVICE UNAVAILABLE")) {
                             Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1203");
                             log.error("Raising alert1203");
                             System.out.println("Raising alert1203");
                             alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), operatorName, "SMS_MODULE", 0));
//                             ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", "Service Unavailable for operator "+operatorName, featureName, "UPDATE", "Alert1203", 0 , 0, executionStartTime, startTime);
//                             modulesAuditTrailRepository.save(tacAudit);
                         }
                         if(!operatorName.equals("default")) {
                             notification.setSendSmsInterface(operatorName);
                         } else {
                             SystemConfigurationDb defaultAggType = systemConfigRepoImpl.getDataByTag("default_agg_type");
                             if (defaultAggType.getValue().equals("Operator")) {
                                 SystemConfigurationDb defaultOperatorName = systemConfigRepoImpl.getDataByTag("default_operator_name");
                                 notification.setSendSmsInterface(defaultOperatorName.getValue());
                             } else if (defaultAggType.getValue().equals("Aggregator")) {
                                 notification.setSendSmsInterface("aggregator");
                             }
                         }
                         notificationRepo.save(notification);
                     }
                 }
             } else {
                 log.info("no sms pending for retry");
             }
             log.info("total sms sent:  " + successCount);
             log.info("sms failed to send: " + failureCount);
//             ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 200,  "Success", "NA", featureName, "UPDATE", "Process Completed for SMS module process", successCount , failureCount, executionStartTime, startTime);
//             modulesAuditTrailRepository.save(tacAudit);
         } catch (DataAccessException e) {
             Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1201");
             log.error("Raising alert1201");
             System.out.println("Raising alert1201");
             alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), operatorName, "SMS_MODULE", 0));
             e.printStackTrace(); // Or log the exception and handle accordingly
             System.exit(0);
         } catch (Exception e) {
             e.printStackTrace();
             log.info("error in sending Sms {}", e.toString());
//             log.info(e.toString());
//             if(moduleAudiTrailId == 0) {
//                 ModulesAuditTrail audit = ModulesAuditTrailBuilder.forInsert(501, "NA", e.getMessage(), featureName, "INSERT", "Exception during SMS module process");
//                 modulesAuditTrailRepository.save(audit);
//             } else {
//                 ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", e.getMessage(), featureName, "UPDATE", "Exception during SMS module process", 0 , 0, executionStartTime, startTime);
//                 modulesAuditTrailRepository.save(tacAudit);
//             }
         }
         log.info("exit from service");
     }

    public void raiseAnAlert(String alertCode, String alertMessage, String alertProcess, int userId) {
        try {   // <e>  alertMessage    //      <process_name> alertProcess
            String path = System.getenv("APP_HOME") + "alert/start.sh";
            ProcessBuilder pb = new ProcessBuilder(path, alertCode, alertMessage, alertProcess, String.valueOf(userId));
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            String response = null;
            while ((line = reader.readLine()) != null) {
                response += line;
            }
            log.info("Alert is generated :response " + response);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Not able to execute Alert mgnt jar "+ ex.getLocalizedMessage() + " ::: " + ex.getMessage());
        }
    }

    private static void sleepForSeconds(int seconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}



