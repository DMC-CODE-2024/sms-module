package com.ceir.CEIRPostman.service;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ceir.CEIRPostman.Repository.NotificationRepository;
import com.ceir.CEIRPostman.RepositoryService.EndUserRepoService;
import com.ceir.CEIRPostman.RepositoryService.MessageRepoSevice;
import com.ceir.CEIRPostman.RepositoryService.NotificationRepoImpl;
import com.ceir.CEIRPostman.RepositoryService.RunningAlertRepoService;
import com.ceir.CEIRPostman.RepositoryService.SystemConfigurationDbRepoImpl;
import com.ceir.CEIRPostman.RepositoryService.UserRepoService;
import com.ceir.CEIRPostman.RepositoryService.UserTempRepoService;
import com.ceir.CEIRPostman.configuration.AppConfig;
import com.ceir.CEIRPostman.model.EndUserDB;
import com.ceir.CEIRPostman.model.Notification;
import com.ceir.CEIRPostman.model.RunningAlertDb;
import com.ceir.CEIRPostman.model.SystemConfigurationDb;
import com.ceir.CEIRPostman.model.User;
import com.ceir.CEIRPostman.model.UserTemporarydetails;
import com.ceir.CEIRPostman.util.SmsUtil;

@Service
public class SmsService implements Runnable {

     @Autowired
     SmsUtil emailUtil;

     @Autowired
     NotificationRepository notificationRepo;

     @Autowired
     AppConfig appConfig;

     @Autowired
     NotificationRepoImpl notificationRepoImpl;

     @Autowired
     SystemConfigurationDbRepoImpl systemConfigRepoImpl;

     @Autowired
     EndUserRepoService endUserRepoService;

     @Autowired
     UserRepoService userRepoService;

     @Autowired
     UserTempRepoService userTempRepoService;

     @Value("${type}")
     String type;

     @Autowired
     RunningAlertRepoService alertDbRepo;

     @Autowired
     MessageRepoSevice messageRepo;

     @Autowired
     AuthorityRepoService authorityRepo;

     private final Logger log = Logger.getLogger(getClass());

     public void run() {
          SystemConfigurationDb batchSizeData = systemConfigRepoImpl.getDataByTag("Total_email_Send_InSec");
          SystemConfigurationDb emailProcessSleep = systemConfigRepoImpl.getDataByTag("EmailProcess_Sleep");
          SystemConfigurationDb sleepTps = systemConfigRepoImpl.getDataByTag("Email_TPS_Milli_Sec");
          SystemConfigurationDb fromEmail = systemConfigRepoImpl.getDataByTag("Email_Username");
          SystemConfigurationDb emailRetryCount = systemConfigRepoImpl.getDataByTag("Email_Retry_Count");
          //SystemConfigurationDb authorityMailSend = systemConfigRepoImpl.getDataByTag("Reporting_Authority_Mail_Status");
          //MessageConfigurationDb messageDb = messageRepo.getByTag("Reporting_Authority_Notification");
          Integer sleepTimeinMilliSec = 0;
          Integer emailretrycountValue = 0;
          //Integer authorityStatusValue = 0;
          try {
               emailretrycountValue = Integer.parseInt(emailRetryCount.getValue());
               log.info("email retry count value: " + emailRetryCount.getValue());
          } catch (Exception e) {
               RunningAlertDb alertDb = new RunningAlertDb("alert008", "sms retry count value not found in db", 0);
               alertDbRepo.saveAlertDb(alertDb);
               log.info(e.toString());
          }
          /*
		 * try { authorityStatusValue = Integer.parseInt(authorityMailSend.getValue());
		 * log.info("Authority mail status value: " + authorityMailSend.getValue()); }
		 * catch (Exception e) { RunningAlertDb alertDb = new RunningAlertDb("alert010",
		 * "authority email status value not found in db", 0);
		 * alertDbRepo.saveAlertDb(alertDb); log.info(e.toString()); }
           */
          try {
               sleepTimeinMilliSec = Integer.parseInt(sleepTps.getValue());

          } catch (Exception e) {
               log.info(e.toString());
          }
          while (true) {
               log.info("inside Sms process");
               int batchSize = 0;
               if (batchSizeData != null) {
                    log.info("no of sms per second value from db: " + batchSizeData.getValue());
                    batchSize = Integer.parseInt(batchSizeData.getValue());
               } else {
                    batchSize = 1;
               }

               try {
                    log.info("inside Sms process");
                    log.info("going to fetch data from notificatio table by status=1 and channel type= " + type);
                    List<Notification> notificationData = notificationRepoImpl.dataByStatusAndChannelType(1, type);
                    int totalMailsent = 0;
                    int totalMailNotsent = 0;

                    if (notificationData.isEmpty() == false) {
                         log.info("notification data is not empty and size is " + notificationData.size());
                         //SystemConfigurationDb emailBodyFooter = systemConfigRepoImpl.getDataByTag("mail_signature");
                         int sNo = 0;
                         emailUtil.setBatchSize(batchSize, notificationData.size());
                         for (Notification notification : notificationData) {
                              log.info("notification data id= " + notification.getId());
                              sNo++;
                              String body = new String();
                              body = notification.getMessage();
                              /*
						 * if (emailBodyFooter != null) { body = body + "\n" +
						 * emailBodyFooter.getValue(); }
                               */
                              String toEmail = "";
                              if (Objects.nonNull(notification.getUserId()) && notification.getUserId() != 0) {
                                   if (notification.getReferTable() != null) {
                                        log.info("refer Table: " + notification.getReferTable());
                                        if ("END_USER".equalsIgnoreCase(notification.getReferTable())) {
                                             EndUserDB endUser = endUserRepoService.getById(notification.getUserId());
                                             if (Objects.nonNull(endUser)) {
                                                  if (Objects.nonNull(endUser.getPhoneNo())) {
                                                       toEmail = endUser.getPhoneNo();
                                                  }

                                             } else {
                                                  log.info("no data found for this userid: " + notification.getUserId() + " in end user table");
                                             }

                                        } else if ("user_temp".equalsIgnoreCase(notification.getReferTable())) {
                                             UserTemporarydetails details = userTempRepoService
                                                     .getUserTempByUserId(notification.getUserId());
                                             if (Objects.nonNull(details)) {
                                                  if (Objects.nonNull(details.getMobileNo())) {
                                                       toEmail = details.getMobileNo();
                                                  }
                                             } else {
                                                  log.info("no data found for this userid: " + notification.getUserId() + " in UserTemporarydetails  table");

                                             }

                                        } else {
                                             User user = userRepoService.getById(notification.getUserId());
                                             if (Objects.nonNull(user)) {
                                                  if (Objects.nonNull(user.getUserProfile().getPhoneNo())) {
                                                       toEmail = user.getUserProfile().getPhoneNo();
                                                  }

                                             } else {
                                                  log.info("no data found for this userid: " + notification.getUserId() + " in users  table");

                                             }

                                             /*
									 * if(Objects.nonNull(notification.getAuthorityStatus())) {
									 * log.info("authority status: "+notification.getAuthorityStatus());
									 * if(notification.getAuthorityStatus()==1) { if
									 * (Objects.nonNull(user.getUserProfile().getAuthorityEmail()) &&
									 * authorityStatusValue == 1) { authorityEmail =
									 * user.getUserProfile().getAuthorityEmail();
									 * log.info("authorityEmail:  "+authorityEmail); emailUtil.increaseBatchSize();
									 * } } }
                                              */
                                        }
                                   } else {
                                        User user = userRepoService.getById(notification.getUserId());

                                        if (Objects.nonNull(user.getUserProfile().getPhoneNo())) {
                                             toEmail = user.getUserProfile().getPhoneNo();
                                        }
                                        /*
								 * if(Objects.nonNull(notification.getAuthorityStatus())) {
								 * log.info("authority status: "+notification.getAuthorityStatus());
								 * if(notification.getAuthorityStatus()==1) { if
								 * (Objects.nonNull(user.getUserProfile().getAuthorityEmail()) &&
								 * authorityStatusValue == 1) { authorityEmail =
								 * user.getUserProfile().getAuthorityEmail();
								 * log.info("authorityEmail:  "+authorityEmail); emailUtil.increaseBatchSize();
								 * } } }
                                         */

                                   }

                                   boolean emailStatus = false;

                                   if (toEmail != null && !toEmail.isEmpty()) {
                                        log.info("to Sms  " + toEmail);
                                        //validator regex??
                                        if (emailUtil.emailValidator(toEmail)) {
                                             //String message=body.replace("\\n", "\n");
                                             emailStatus = emailUtil.sendEmail(toEmail, fromEmail.getValue(),
                                                     notification.getSubject(), body, notificationData.size(), sNo,
                                                     sleepTimeinMilliSec);
                                             if (emailStatus) {
                                                  notification.setStatus(0);
                                                  totalMailsent++;
                                             } else {
                                                  notification.setRetryCount(notification.getRetryCount() + 1);
                                                  if (notification.getRetryCount() >= emailretrycountValue) {
                                                       notification.setStatus(2);
                                                  }
                                                  totalMailNotsent++;
                                             }

                                             /*
									 * if (authorityEmail != null && !authorityEmail.isEmpty()) { if
									 * (emailUtil.emailValidator(authorityEmail)) { body=body.replace("\\n", "\n");
									 * String content=messageDb.getValue().replace("\\n", "\n"); message =content +
									 * "\n" +body; log.info("message content in case of authority email: " +
									 * message);
									 * log.info("authorityEmail: "+authorityEmail+" fromEmail: "+fromEmail.getValue(
									 * )+"getSubject: "+messageDb.getSubject()); emailStatus =
									 * emailUtil.sendEmail(authorityEmail, fromEmail.getValue(),
									 * messageDb.getSubject(), message, notificationData.size(), sNo,
									 * sleepTimeinMilliSec); if (emailStatus) { totalMailsent++;
									 * AuthorityNotification authoritNoti = new AuthorityNotification(
									 * notification.getChannelType(), message, notification.getUserId(),
									 * notification.getFeatureId(), notification.getFeatureTxnId(),
									 * notification.getFeatureName(), notification.getSubFeature(),
									 * notification.getStatus(), notification.getSubject(),
									 * notification.getRetryCount(), notification.getReferTable(),
									 * notification.getRoleType(), notification.getReceiverUserType());
									 * authorityRepo.saveNotification(authoritNoti);
									 * 
									 * } else { totalMailNotsent++; } }
									 * 
									 * }
                                              */
                                        } else {
                                             log.info("this to sms is invalid: " + toEmail);
                                             notification.setRetryCount(notification.getRetryCount() + 1);
                                             notification.setStatus(2);
                                        }

                                   } else {
                                        log.info("if Sms value for this user id " + notification.getUserId()
                                                + " not found in db ");
                                        notification.setRetryCount(notification.getRetryCount() + 1);
                                        notification.setStatus(2);
                                   }

                              } else {
                                   notification.setRetryCount(notification.getRetryCount() + 1);
                                   notification.setStatus(2);
                                   log.info("user id for this notification is either null or 0");
                              }

                              notificationRepo.save(notification);
                         }

                         log.info("total sms sent=  " + totalMailsent);
                         log.info("sms failed to send: " + totalMailNotsent);
                         emailUtil.setIndexZero();
                    } else {
                         log.info("notification data is  empty");
                         log.info(" so no sms is pending to send");
                    }
                    log.info("exit from sms process");
               } catch (Exception e) {
                    log.info("error in sending Sms");
                    log.info(e.toString());
                    log.info(e.toString());
               }
               log.info("exit from  service");
               System.exit(0);
          }
     }
}



