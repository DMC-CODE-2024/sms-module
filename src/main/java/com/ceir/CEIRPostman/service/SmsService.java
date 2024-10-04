package com.ceir.CEIRPostman.service;

import com.ceir.CEIRPostman.Repository.app.CfgFeatureAlertRepository;
import com.ceir.CEIRPostman.Repository.app.NotificationRepository;
import com.ceir.CEIRPostman.Repository.app.OperatorRepository;
import com.ceir.CEIRPostman.Repository.audit.ModulesAuditTrailRepository;
import com.ceir.CEIRPostman.RepositoryService.NotificationRepoImpl;
import com.ceir.CEIRPostman.RepositoryService.RunningAlertRepoService;
import com.ceir.CEIRPostman.RepositoryService.SystemConfigurationDbRepoImpl;
import com.ceir.CEIRPostman.constants.SmsType;
import com.ceir.CEIRPostman.model.app.CfgFeatureAlert;
import com.ceir.CEIRPostman.model.app.Notification;
import com.ceir.CEIRPostman.model.app.Operator;
import com.ceir.CEIRPostman.model.app.SystemConfigurationDb;
import com.ceir.CEIRPostman.util.VirtualIpAddressUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

public class SmsService implements Runnable {
    Integer smsSleepTimer = Integer.valueOf(1000);

    private final Logger log = LogManager.getLogger(getClass());

    private final String operatorName;

    private final List<String> operators;

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

    int successCount;

    int failureCount;

    String operatorNameArg;

    String featureName;

    Integer smsRetryCountValue;

    Integer sleepTimeinMilliSec;

    Integer tpsValue;

    String from;

    Operator operatorEntity;

    public SmsService(String operatorName, List<String> operators) {
        this.successCount = 0;
        this.failureCount = 0;
        this.operatorNameArg = null;
        this.featureName = "";
        this.smsRetryCountValue = Integer.valueOf(0);
        this.sleepTimeinMilliSec = Integer.valueOf(0);
        this.tpsValue = Integer.valueOf(0);
        this.from = null;
        this.operatorName = operatorName;
        this.operators = operators;
    }

    public void run() {
        try {
            List<Operator> operatorMeta = this.operatorRepository.findAll();
            Optional<Operator> foundOperator = operatorMeta.stream().filter(operator -> operator.getOperatorName().equals(this.operatorName.toLowerCase())).findFirst();
            if (foundOperator.isPresent()) {
                this.operatorEntity = foundOperator.get();
                this.operatorNameArg = this.operatorName.toLowerCase();
            } else {
                throw new IllegalArgumentException("Operator not found with name: " + this.operatorName);
            }
            System.out.println("operator name: " + this.operatorNameArg);
            if (!this.operators.isEmpty())
                for (String values : this.operators)
                    System.out.println("operators : " + values);
        } catch (IllegalArgumentException e) {
            Optional<CfgFeatureAlert> alert = this.cfgFeatureAlertRepository.findByAlertId("alert1205");
            this.log.error("Raising alert1205");
            System.out.println("Raising alert1205");
            alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), this.operatorName, "SMS_MODULE", 0));
            e.printStackTrace();
            System.exit(0);
        }
        Optional<SystemConfigurationDb> sleepTimerConfig = Optional.ofNullable(this.systemConfigRepoImpl.getDataByTag("smsSleepTimer"));
        sleepTimerConfig.ifPresent(systemConfigurationDb -> this.smsSleepTimer = Integer.valueOf(Integer.parseInt(systemConfigurationDb.getValue())));
        SystemConfigurationDb smsRetryCount = this.systemConfigRepoImpl.getDataByTag("sms_retry_count");
        SystemConfigurationDb sleepTps = this.systemConfigRepoImpl.getDataByTag("sms_retry_interval");
        try {
            SystemConfigurationDb tps, fromSender;
            if (this.operatorNameArg == null) {
                tps = Optional.<SystemConfigurationDb>of(this.systemConfigRepoImpl.getDataByTag("default_sms_tps")).get();
                fromSender = Optional.<SystemConfigurationDb>of(this.systemConfigRepoImpl.getDataByTag("default_sender_id")).get();
            } else {
                tps = Optional.<SystemConfigurationDb>of(this.systemConfigRepoImpl.getDataByTag(this.operatorNameArg + "_sms_tps")).get();
                fromSender = Optional.<SystemConfigurationDb>of(this.systemConfigRepoImpl.getDataByTag(this.operatorNameArg + "_sender_id")).get();
            }
            this.smsRetryCountValue = Integer.valueOf(Integer.parseInt(smsRetryCount.getValue()));
            this.sleepTimeinMilliSec = Integer.valueOf(Integer.parseInt(sleepTps.getValue()));
            this.from = fromSender.getValue();
            this.tpsValue = Integer.valueOf(Integer.parseInt(tps.getValue()));
            this.log.debug("sms retry count value: " + this.smsRetryCountValue + ", sms retry interval: " + this.sleepTimeinMilliSec + " and tps: " + this.tpsValue);
            while (true) {
                while (this.virtualIpAddressUtil.getFullList()) {
                    sendSMS();
                    sleepForSeconds(this.smsSleepTimer.intValue());
                }
                this.log.info("VIP not found. Sleeping for " + this.smsSleepTimer + " seconds.");
                sleepForSeconds(this.smsSleepTimer.intValue());
            }
        } catch (Exception e) {
            this.log.error("Raising alert1202");
            System.out.println("Raising alert1202");
            Optional<CfgFeatureAlert> alert = this.cfgFeatureAlertRepository.findByAlertId("alert1202");
            alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), this.operatorName, "SMS_MODULE", 0));
            e.printStackTrace();
            System.exit(0);
            return;
        }
    }

    public void sendSMS() {
        try {
            int initialSuccessCount = this.successCount;
            int initialFailureCount = this.failureCount;

            List<Notification> otpNotifications = fetchOtpNotifications();
            List<Notification> smsNotifications = fetchSmsNotifications();
            List<Notification> retryNotifications = fetchRetryNotifications();

            int otpProcessed = processNotifications(otpNotifications, SmsType.SMS_OTP);
            int smsProcessed = processNotifications(smsNotifications, SmsType.SMS);
            int retryProcessed = processNotifications(retryNotifications, SmsType.SMS);

            // Log summary only if any notifications were processed
            if (otpProcessed > 0 || smsProcessed > 0 || retryProcessed > 0) {
                this.log.info("SMS processing summary - OTP: {}, SMS: {}, Retry: {}, Sent: {}, Failed: {}",
                        otpProcessed, smsProcessed, retryProcessed,
                        this.successCount - initialSuccessCount,
                        this.failureCount - initialFailureCount);
            } else {
                this.log.debug("No SMS notifications processed in this cycle");
            }
        } catch (DataAccessException e) {
            handleDataAccessException(e);
        } catch (Exception e) {
            this.log.error("Error in sending SMS: {}", e.getMessage(), e);
        }
        this.log.debug("Exiting SMS service");
    }

    private List<Notification> fetchOtpNotifications() {
        this.log.debug("Fetching pending OTP requests");
        return this.operators.isEmpty() ?
                this.notificationRepoImpl.dataByStatusAndRetryCountAndOperatorNameAndChannelType(0, 0, this.operatorNameArg, SmsType.SMS_OTP.name()) :
                this.notificationRepoImpl.dataByStatusAndRetryCountAndOperatorNameInAndChannelType(0, 0, this.operators, SmsType.SMS_OTP.name());
    }

    private List<Notification> fetchSmsNotifications() {
        this.log.debug("Fetching SMS requests");
        return this.operators.isEmpty() ?
                this.notificationRepoImpl.dataByStatusAndRetryCountAndOperatorNameAndChannelTypeV2(0, 0, this.operatorNameArg, SmsType.SMS.name()) :
                this.notificationRepoImpl.dataByStatusAndRetryCountAndOperatorNameInAndChannelTypeV2(0, 0, this.operators, SmsType.SMS.name());
    }

    private List<Notification> fetchRetryNotifications() {
        this.log.debug("Fetching failed SMS for retry");
        LocalDateTime newDateTime = LocalDateTime.now().plusNanos((LocalDateTime.now().getNano() - this.sleepTimeinMilliSec.intValue() * 1000000));
        return this.operators.isEmpty() ?
                this.notificationRepoImpl.findByStatusAndChannelTypeAndOperatorNameAndModifiedOnAndRetryCountGreaterThanEqualTo(0, SmsType.SMS.name(), this.operatorNameArg, newDateTime, 1) :
                this.notificationRepoImpl.findByStatusAndChannelTypeAndOperatorNameInAndModifiedOnAndRetryCountGreaterThanEqualTo(0, SmsType.SMS.name(), this.operators, newDateTime, 1);
    }

    private int processNotifications(List<Notification> notifications, SmsType smsType) {
        if (notifications.isEmpty()) {
            return 0;
        }

        long tsms = System.currentTimeMillis();
        int smsSentCount = 0;

        for (Notification notification : notifications) {
            smsSentCount = processSingleNotification(notification, smsSentCount, tsms, smsType);
            tsms = System.currentTimeMillis();
        }

        return notifications.size();
    }

    private int processSingleNotification(Notification notification, int smsSentCount, long tsms, SmsType smsType) {
        if ("default".equals(notification.getOperatorName())) {
            raiseAlert("alert1207", notification.getMsisdn());
        }

        smsSentCount = handleTpsLimit(smsSentCount, tsms);

        if (Objects.nonNull(notification.getMsisdn()) && Objects.nonNull(notification.getOperatorName())) {
            sendSmsAndUpdateNotification(notification, smsType);
            smsSentCount++;
        }

        return smsSentCount;
    }

    private int handleTpsLimit(int smsSentCount, long tsms) {
        if (smsSentCount >= this.tpsValue.intValue()) {
            long tsdiff = System.currentTimeMillis() - tsms;
            if (tsdiff < 1000L) {
                try {
                    Thread.sleep(1000L - tsdiff);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return 0;
            } else if (tsdiff > 1000L) {
                raiseAlert("alert1204", this.operatorName);
            }
            return 0;
        }
        return smsSentCount;
    }

    private void sendSmsAndUpdateNotification(Notification notification, SmsType smsType) {
        SmsManagementService smsProvider = this.smsSendFactory.getSmsManagementService(notification.getOperatorName(), this.operatorEntity.getChannelType());
        String correlationId = UniqueIdGenerator.generateUniqueId(this.operatorName);
        String smsStatus = smsProvider.sendSms(this.operatorNameArg, notification.getMsisdn(), this.from, notification.getMessage(), correlationId, notification.getMsgLang());

        updateNotificationStatus(notification, smsStatus, correlationId, smsType);
        setSendSmsInterface(notification);
        this.notificationRepo.save(notification);
    }

    private void updateNotificationStatus(Notification notification, String smsStatus, String correlationId, SmsType smsType) {
        LocalDateTime now = LocalDateTime.now();
        if (Objects.equals(smsStatus, "SUCCESS")) {
            this.successCount++;
            notification.setStatus(1);
            notification.setNotificationSentTime(now);
            notification.setCorelationId(correlationId);
        } else if (Objects.equals(smsStatus, "FAILED")) {
            this.failureCount++;
            handleFailedSms(notification, smsType);
        } else if (Objects.equals(smsStatus, "SERVICE_UNAVAILABLE")) {
            raiseAlert("alert1203", this.operatorName);
        } else {
            this.log.warn("Unexpected SMS status for {}: {}", this.operatorNameArg, smsStatus);
            raiseAlert("alert1206", this.operatorName);
        }
    }

    private void handleFailedSms(Notification notification, SmsType smsType) {
        if (smsType == SmsType.SMS_OTP || notification.getRetryCount().intValue() >= this.smsRetryCountValue.intValue()) {
            notification.setStatus(2);
            raiseAlert("alert1206", this.operatorName);
        } else {
            notification.setRetryCount(notification.getRetryCount() + 1);
        }
    }

    private void setSendSmsInterface(Notification notification) {
        if (!this.operatorName.equals("default")) {
            notification.setSendSmsInterface(this.operatorName);
        } else {
            SystemConfigurationDb defaultAggType = this.systemConfigRepoImpl.getDataByTag("default_agg_type");
            if (defaultAggType.getValue().equals("Operator")) {
                SystemConfigurationDb defaultOperatorName = this.systemConfigRepoImpl.getDataByTag("default_operator_name");
                notification.setSendSmsInterface(defaultOperatorName.getValue());
            } else if (defaultAggType.getValue().equals("Aggregator")) {
                notification.setSendSmsInterface("aggregator");
            }
        }
    }

    private void raiseAlert(String alertId, String alertMessage) {
        Optional<CfgFeatureAlert> alert = this.cfgFeatureAlertRepository.findByAlertId(alertId);
        this.log.warn("Raising {}", alertId);
        alert.ifPresent(cfgFeatureAlert -> raiseAnAlert(cfgFeatureAlert.getAlertId(), alertMessage, "SMS_MODULE", 0));
    }

    private void handleDataAccessException(DataAccessException e) {
        this.log.error("Data access error: {}", e.getMessage(), e);
        raiseAlert("alert1201", this.operatorName);
        System.exit(0);
    }

    public void raiseAnAlert(String alertCode, String alertMessage, String alertProcess, int userId) {
        try {
            String path = System.getenv("APP_HOME") + "alert/start.sh";
            ProcessBuilder pb = new ProcessBuilder(new String[]{path, alertCode, alertMessage, alertProcess, String.valueOf(userId)});
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String response = null;
            String line;
            while ((line = reader.readLine()) != null)
                response = response + response;
            this.log.info("Alert is generated :response " + response);
        } catch (Exception ex) {
            ex.printStackTrace();
            this.log.error("Not able to execute Alert mgnt jar " + ex.getLocalizedMessage() + " ::: " + ex.getMessage());
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
