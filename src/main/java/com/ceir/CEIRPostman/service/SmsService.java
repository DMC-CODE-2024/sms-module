package com.ceir.CEIRPostman.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;

import com.ceir.CEIRPostman.Repository.app.CfgFeatureAlertRepository;
import com.ceir.CEIRPostman.Repository.audit.ModulesAuditTrailRepository;
import com.ceir.CEIRPostman.builder.ModulesAuditTrailBuilder;
import com.ceir.CEIRPostman.constants.OperatorTypes;
import com.ceir.CEIRPostman.model.app.CfgFeatureAlert;
import com.ceir.CEIRPostman.model.audit.ModulesAuditTrail;
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

//@Service
public class SmsService implements Runnable {
    private final Logger log = LogManager.getLogger(getClass());
    private final String operatorName;
    private final List<String> operators;

    public SmsService(String operatorName, List<String> operators) {
        this.operatorName = operatorName;
        this.operators = operators;
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

     public void run() {
         int executionStartTime = Math.toIntExact(System.currentTimeMillis() / 1000);
         log.info("starting send sms process");
         int successCount = 0;
         int failureCount = 0;
          int moduleAudiTrailId = 0;
          String type = "SMS";
          String operatorNameArg = null;
         String featureName = operatorName+" SMS Notification Module";
         ModulesAuditTrail startAudit = ModulesAuditTrailBuilder.forInsert(201, "Initial", "NA", featureName, "INSERT", "Started SMS module process");
         startAudit = modulesAuditTrailRepository.save(startAudit);
         moduleAudiTrailId = startAudit.getId();
          try{
              operatorNameArg = OperatorTypes.valueOf(operatorName.toUpperCase()).getValue();
              System.out.println("operator name: "+ operatorNameArg);
              if (operators.size() > 0) {
                  for(String values : operators) {
                      System.out.println("operators : "+ values);
                  }
              }
          } catch (IllegalArgumentException e) {
              Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1205");
              log.error("Raising alert1205");
              System.out.println("Raising alert1205");
              if (alert.isPresent()) {
                  raiseAnAlert(alert.get().getAlertId(), operatorName, "SMS_MODULE", 0);
//                  RunningAlertDb alertDb = new RunningAlertDb(alert.get().getAlertId(), operatorName, 0);
//                  alertDbRepo.saveAlertDb(alertDb);
              }
              ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", "Operator Name does not exist "+operatorName, featureName, "UPDATE", "Alert1205", 0 , 0, executionStartTime);
              modulesAuditTrailRepository.save(tacAudit);
              e.printStackTrace();
              System.exit(0);
          }
          SystemConfigurationDb tps;
          SystemConfigurationDb smsRetryCount = systemConfigRepoImpl.getDataByTag("sms_retry_count");
          SystemConfigurationDb fromSender;
          SystemConfigurationDb sleepTps = systemConfigRepoImpl.getDataByTag("sms_retry_interval");
          Integer smsRetryCountValue = 0;
          Integer sleepTimeinMilliSec = 0;
          Integer tpsValue = 0;
          String from = null;
          try {
              if (operatorNameArg == null) {
                  tps = Optional.of(systemConfigRepoImpl.getDataByTag("default_sms_tps")).get();
                  fromSender = Optional.of(systemConfigRepoImpl.getDataByTag("default_sender_id")).get();
              } else {
                  tps = Optional.of(systemConfigRepoImpl.getDataByTag(operatorNameArg+"_sms_tps")).get();
                  fromSender = Optional.of(systemConfigRepoImpl.getDataByTag(operatorNameArg+"_sender_id")).get();
              }
               smsRetryCountValue = Integer.parseInt(smsRetryCount.getValue());
               sleepTimeinMilliSec = Integer.parseInt(sleepTps.getValue());
               from  = fromSender.getValue();
               tpsValue = Integer.parseInt(tps.getValue());
               log.info("sms retry count value: " + smsRetryCountValue + ", sms retry interval: " + sleepTimeinMilliSec + " and tps: " + tpsValue);
          } catch (Exception e) {
              log.error("Raising alert1202");
              System.out.println("Raising alert1202");
              Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1202");
              if (alert.isPresent()) {
                  raiseAnAlert(alert.get().getAlertId(), operatorName, "SMS_MODULE", 0);
//                  RunningAlertDb alertDb = new RunningAlertDb(alert.get().getAlertId(), operatorName, 0);
//                  alertDbRepo.saveAlertDb(alertDb);
              }
              ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", e.getMessage(), featureName, "UPDATE", "Alert1202", 0 , 0, executionStartTime);
              modulesAuditTrailRepository.save(tacAudit);
              e.printStackTrace();
              System.exit(0);
          }
          while (true) {
               log.info("inside Sms process");
               try {
                    log.info("inside Sms process");
                    log.info("going to fetch data from notification table for operator="+operatorNameArg+", status=0, retryCount=0 and channel type="+type);
                   List<Notification> notificationData;
                    if (operators.size() == 0){
                        notificationData = notificationRepoImpl.dataByStatusAndRetryCountAndOperatorNameAndChannelType(0, 0, operatorNameArg, type);
                    } else {
                        notificationData = notificationRepoImpl.dataByStatusAndRetryCountAndOperatorNameInAndChannelType(0, 0, operators, type);
                    }
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
                                     Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1204");
                                     log.error("Raising alert1204");
                                     System.out.println("Raising alert1204");
                                     if (alert.isPresent()) {
                                         raiseAnAlert(alert.get().getAlertId(), operatorName, "SMS_MODULE", 0);
//                                         RunningAlertDb alertDb = new RunningAlertDb(alert.get().getAlertId(), operatorName, 0);
//                                         alertDbRepo.saveAlertDb(alertDb);
                                     }
                                     ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", "TPS not Achieved", featureName, "UPDATE", "Alert1204", 0 , 0, executionStartTime);
                                     modulesAuditTrailRepository.save(tacAudit);
                                 }
                                 tsms = System.currentTimeMillis();
                             }
                             log.info("notification data id= " + notification.getId());
                             if (Objects.nonNull(notification.getMsisdn()) && Objects.nonNull(notification.getOperatorName())) {
                                 SmsManagementService smsProvider = smsSendFactory.getSmsManagementService(notification.getOperatorName());
                                 String correlationId = UniqueIdGenerator.generateUniqueId(operatorName);
                                 String smsStatus = smsProvider.sendSms(notification.getMsisdn(), from, notification.getMessage(), correlationId, notification.getMsgLang());
                                 if (smsStatus == "SUCCESS") {
                                     successCount = successCount + 1;
                                     LocalDateTime now = LocalDateTime.now();
                                     notification.setStatus(1);
                                     notification.setNotificationSentTime(now);
                                     notification.setCorelationId(correlationId);
                                 } else if (smsStatus == "FAILED") {
                                     failureCount = failureCount + 1;
                                     //check retry count if >3 update status to 2 else increase retry count|| if 5xx then raise alarm
                                     if (notification.getRetryCount() < smsRetryCountValue) {
                                         notification.setRetryCount(notification.getRetryCount() + 1);
                                     } else {
                                         notification.setStatus(2);
                                         Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1206");
                                         log.error("Raising alert1206");
                                         System.out.println("Raising alert1206");
                                         if (alert.isPresent()) {
                                             raiseAnAlert(alert.get().getAlertId(), operatorName, "SMS_MODULE", 0);
//                                             RunningAlertDb alertDb = new RunningAlertDb(alert.get().getAlertId(), operatorName, 0);
//                                             alertDbRepo.saveAlertDb(alertDb);
                                         }
                                         ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", "Send SMS failed for "+operatorName, featureName, "UPDATE", "Alert1206", 0 , 0, executionStartTime);
                                         modulesAuditTrailRepository.save(tacAudit);
                                     }
                                 } else if (smsStatus == "SERVICE_UNAVAILABLE") {
                                     Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1203");
                                     log.error("Raising alert1203");
                                     System.out.println("Raising alert1203");
                                     if (alert.isPresent()) {
                                         raiseAnAlert(alert.get().getAlertId(), operatorName, "SMS_MODULE", 0);
//                                         RunningAlertDb alertDb = new RunningAlertDb(alert.get().getAlertId(), operatorName, 0);
//                                         alertDbRepo.saveAlertDb(alertDb);
                                     }
                                     ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", "Service Unavailable for "+operatorName, featureName, "UPDATE", "Alert1203", 0 , 0, executionStartTime);
                                     modulesAuditTrailRepository.save(tacAudit);
                                 } else {
                                     log.info("error in sending Sms for "+operatorNameArg);
                                     //some alert
                                     Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1206");
                                     log.error("Raising alert1206");
                                     System.out.println("Raising alert1206");
                                     if (alert.isPresent()) {
                                         raiseAnAlert(alert.get().getAlertId(), operatorName, "SMS_MODULE", 0);
//                                         RunningAlertDb alertDb = new RunningAlertDb(alert.get().getAlertId(), operatorName, 0);
//                                         alertDbRepo.saveAlertDb(alertDb);
                                     }
                                     ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", "Send SMS status unknown "+operatorName, featureName, "UPDATE", "Alert1206", 0 , 0, executionStartTime);
                                     modulesAuditTrailRepository.save(tacAudit);
//                                     RunningAlertDb alertDb = new RunningAlertDb("Alert1201", "Database connection failed or login failed due to credentials", 0);
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
                         log.info("no sms is pending to send");
                    }
                   log.info("retrying for failed sms");
                   LocalDateTime dateTime = LocalDateTime.now();
                   LocalDateTime newDateTime = dateTime.plusNanos(dateTime.getNano() - sleepTimeinMilliSec * 1000000);
                   List<Notification> notificationDataForRetries;
                   if (operators.size() == 0) {
                       notificationDataForRetries = notificationRepoImpl.findByStatusAndChannelTypeAndOperatorNameAndModifiedOnAndRetryCountGreaterThanEqualTo(0, type, operatorNameArg, newDateTime, 1);
                   } else {
                       notificationDataForRetries = notificationRepoImpl.findByStatusAndChannelTypeAndOperatorNameInAndModifiedOnAndRetryCountGreaterThanEqualTo(0, type, operators, newDateTime, 1);
                   }
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
                                   Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1204");
                                   log.error("Raising alert1204");
                                   System.out.println("Raising alert1204");
                                   if (alert.isPresent()) {
                                       raiseAnAlert(alert.get().getAlertId(), operatorName, "SMS_MODULE", 0);
//                                       RunningAlertDb alertDb = new RunningAlertDb(alert.get().getAlertId(), operatorName, 0);
//                                       alertDbRepo.saveAlertDb(alertDb);
                                   }
                                   ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", "TPS not achieved", featureName, "UPDATE", "Alert1204", 0 , 0, executionStartTime);
                                   modulesAuditTrailRepository.save(tacAudit);
                               }
                               tsms = System.currentTimeMillis();
                           }
                           log.info("retrying notification data id= " + notification.getId());
                           if (Objects.nonNull(notification.getMsisdn()) && Objects.nonNull(notification.getOperatorName())) {
                               SmsManagementService smsProvider = smsSendFactory.getSmsManagementService(notification.getOperatorName());
                               String correlationId = UniqueIdGenerator.generateUniqueId(operatorName);
                               String smsStatus = smsProvider.sendSms(notification.getMsisdn(), from, notification.getMessage(), correlationId, notification.getMsgLang());
                               if (smsStatus == "SUCCESS") {
                                   successCount = successCount + 1;
                                   LocalDateTime now = LocalDateTime.now();
                                   notification.setStatus(1);
                                   notification.setNotificationSentTime(now);
                                   notification.setCorelationId(correlationId);
                               } else if (smsStatus == "FAILED") {
                                   failureCount = failureCount + 1;
                                   if (notification.getRetryCount() < smsRetryCountValue) {
                                       notification.setRetryCount(notification.getRetryCount() + 1);
                                   } else {
                                       notification.setStatus(2);
                                       Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1206");
                                       log.error("Raising alert1206");
                                       System.out.println("Raising alert1206");
                                       if (alert.isPresent()) {
                                           raiseAnAlert(alert.get().getAlertId(), operatorName, "SMS_MODULE", 0);
//                                           RunningAlertDb alertDb = new RunningAlertDb(alert.get().getAlertId(), operatorName, 0);
//                                           alertDbRepo.saveAlertDb(alertDb);
                                       }
                                       ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 200, "NA", "Send SMS failed for "+operatorName, featureName, "UPDATE", "Alert1206", 0 , 0, executionStartTime);
                                       modulesAuditTrailRepository.save(tacAudit);
                                   }
                               } else if (smsStatus == "SERVICE UNAVAILABLE") {
                                   Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1203");
                                   log.error("Raising alert1203");
                                   System.out.println("Raising alert1203");
                                   if (alert.isPresent()) {
                                       raiseAnAlert(alert.get().getAlertId(), operatorName, "SMS_MODULE", 0);
//                                       RunningAlertDb alertDb = new RunningAlertDb(alert.get().getAlertId(), operatorName, 0);
//                                       alertDbRepo.saveAlertDb(alertDb);
                                   }
                                   ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", "Service Unavailable for operator "+operatorName, featureName, "UPDATE", "Alert1203", 0 , 0, executionStartTime);
                                   modulesAuditTrailRepository.save(tacAudit);
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

                       log.info("total sms sent=  " + successCount);
                       log.info("sms failed to send: " + failureCount);
                   }
                   ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 200,  "Success", "NA", featureName, "UPDATE", "Process Completed for SMS module process", successCount , failureCount, executionStartTime);
                   modulesAuditTrailRepository.save(tacAudit);
               } catch (DataAccessException e) {
                   Optional<CfgFeatureAlert> alert = cfgFeatureAlertRepository.findByAlertId("alert1201");
                   log.error("Raising alert1201");
                   System.out.println("Raising alert1201");
                   if (alert.isPresent()) {
                       raiseAnAlert(alert.get().getAlertId(), operatorName, "SMS_MODULE", 0);
//                       RunningAlertDb alertDb = new RunningAlertDb(alert.get().getAlertId(), operatorName, 0);
//                       alertDbRepo.saveAlertDb(alertDb);
                   }
                   e.printStackTrace(); // Or log the exception and handle accordingly
                   System.exit(0);
               } catch (Exception e) {
                   e.printStackTrace();
                   log.info("error in sending Sms");
                   log.info(e.toString());
                   log.info(e.toString());
                   if(moduleAudiTrailId == 0) {
                       ModulesAuditTrail audit = ModulesAuditTrailBuilder.forInsert(501, "NA", e.getMessage(), featureName, "INSERT", "Exception during SMS module process");
                       modulesAuditTrailRepository.save(audit);
                   } else {
                       ModulesAuditTrail tacAudit = ModulesAuditTrailBuilder.forUpdate(moduleAudiTrailId, 501, "NA", e.getMessage(), featureName, "UPDATE", "Exception during SMS module process", 0 , 0, executionStartTime);
                       modulesAuditTrailRepository.save(tacAudit);
                   }
               }
               log.info("exit from  service");
               System.exit(0);
          }
     }

    public void raiseAnAlert(String alertCode, String alertMessage, String alertProcess, int userId) {
        try {   // <e>  alertMessage    //      <process_name> alertProcess
            String path = System.getenv("APP_HOME") + "alert/start.sh";
            ProcessBuilder pb = new ProcessBuilder(path, alertCode, alertMessage, alertProcess, String.valueOf(userId));
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
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
}



