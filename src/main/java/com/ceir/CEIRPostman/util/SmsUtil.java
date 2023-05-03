package com.ceir.CEIRPostman.util;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

import com.ceir.CEIRPostman.RepositoryService.RunningAlertRepoService;
import com.ceir.CEIRPostman.RepositoryService.SystemConfigurationDbRepoImpl;
import com.ceir.CEIRPostman.model.RunningAlertDb;
@Service
public class SmsUtil {

	private final Logger logger = Logger.getLogger(getClass());	

	@Autowired
	SmsConfig smsUtil; 
	@Autowired
	SystemConfigurationDbRepoImpl systemConfigRepoImpl;

	private final Logger log = Logger.getLogger(getClass());

	int batchSize =1; // for example, adjust it to you needs
	String[] messages;
	String[] numbers;
	int messageIndex = 0;
	int numberIndex = 0;
	
	@Autowired
	RunningAlertRepoService alertDbRepo;

	public void setBatchSize(int noOfElements) {
		this.numbers = new String[noOfElements];
		this.messages = new String[noOfElements];
	}
	public void setIndexZero() {
		messageIndex = 0;
		numberIndex = 0;
	}

	public boolean emailValidator(String email) {
		//boolean isValid=false;  
		  try {
			  log.info("inside sms validator");
		  }catch (Exception e) {
			  log.info("this sms address is incorrect: " + email);
			  log.info(e.toString());
		  }
		 
		return true;
	}
	public boolean sendEmail(String toAddress, String fromAddress, String subject, String msgBody,int totalData,int dataRead,Integer sleep) {
		try {
			if(!toAddress.isEmpty() && !msgBody.isEmpty()) {
			logger.info("adding sms into the array");
			 messages[messageIndex++] = msgBody;
			 numbers[numberIndex++] = toAddress;
			 logger.info("Number: "+numbers[numberIndex-1]);
			 logger.info("Message: "+messages[messageIndex-1]);
			    if (messageIndex == batchSize) {
			    	logger.info("if batch size equals to no of sms added into array");
			    	logger.info("now going to send sms");
			    	try {
			    		smsUtil.sendSMS(numbers,messages);    		
				        logger.info("no of sms are sent: "+batchSize);
				        log.info("Total Data: "+totalData+" Data Read: "+dataRead);
				        int difference=totalData-dataRead;
				        log.info("Difference: "+difference);
				        setBatchSize(batchSize,difference);
				        logger.info("now batchSize is "+batchSize);
				        messageIndex = 0;
				        numberIndex = 0;
			    	}
			    	catch(MailSendException  send ) {
			    		log.info("inside sms send exception");
				        logger.info("if sms fail to send: "+batchSize);
				        log.info(send.toString());
				        log.info("Total Data: "+totalData+" Data Read: "+dataRead);
				        int difference=totalData-dataRead;
				        log.info("Difference: "+difference);
				        setBatchSize(batchSize,difference);
				        logger.info("now batchSize is "+batchSize);
				        messageIndex = 0;
				        numberIndex = 0;
						RunningAlertDb alertDb=new RunningAlertDb("alert009","error occurs while sending sms",0);
						alertDbRepo.saveAlertDb(alertDb);
						logger.info("error occur while send sms");
			
						return Boolean.FALSE;
			    	}
			    	catch(MailParseException  send ) {
			    		log.info("inside other sms exceptions");
				        logger.info("if sms fail to parse: "+batchSize);
				        log.info("Total Data: "+totalData+" Data Read: "+dataRead);
				        int difference=totalData-dataRead;
				        log.info("Difference: "+difference);
				        setBatchSize(batchSize,difference);
				        logger.info("now batchSize is "+batchSize);
				        messageIndex = 0;
				        numberIndex = 0;
						RunningAlertDb alertDb=new RunningAlertDb("alert009","error occurs while parsing sms",0);
						alertDbRepo.saveAlertDb(alertDb);
						logger.info("error occur while parsing sms");
						logger.info(send.getMessage());
						logger.info(send.toString());
						return Boolean.FALSE;
			    	}
			    	catch (Exception e) {
						logger.info("error occur in first block");
						logger.error(e.getMessage());
						logger.info(e.toString());
						logger.info("Send Email Status FALSE");
						messageIndex = 0;
						return Boolean.FALSE;
					}
			
			    }
			    try {
			    	logger.info("sleep time in milliseconds: "+sleep);
					Thread.sleep(sleep);
				}
				catch(Exception e) {
					logger.info(e.toString());
				}
			    logger.info("Send sms Status TRUE");
			return Boolean.TRUE;
		}else {
			logger.info("size of number and message array not equal");
			return Boolean.FALSE;
		}
		}catch (Exception e) {
			logger.info("error occur");
			logger.error(e.getMessage());
			logger.info(e.toString());
			logger.info("Send sms Status FALSE");
			return Boolean.FALSE;
		}
	}
}