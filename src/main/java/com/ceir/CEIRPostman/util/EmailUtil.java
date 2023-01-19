package com.ceir.CEIRPostman.util;


import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ceir.CEIRPostman.RepositoryService.RunningAlertRepoService;
import com.ceir.CEIRPostman.RepositoryService.SystemConfigurationDbRepoImpl;
import com.ceir.CEIRPostman.model.RunningAlertDb;

import org.apache.log4j.Logger;
@Service
public class EmailUtil {

	private final Logger logger = Logger.getLogger(getClass());	

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	MailSender mailSender; 
	@Autowired
	SystemConfigurationDbRepoImpl systemConfigRepoImpl;

	private final Logger log = Logger.getLogger(getClass());

	int batchSize =1; // for example, adjust it to you needs
	SimpleMailMessage[] messages;
	int messageIndex = 0;
	
	@Autowired
	RunningAlertRepoService alertDbRepo;

	public void setBatchSize(int batch,int noOfElements) {
		if(noOfElements<batch) {
			logger.info("if total mails less than batch size");
			logger.info("message array size: "+noOfElements);
			messages = new SimpleMailMessage[noOfElements];
			batchSize=noOfElements;
			log.info("batch size: "+batchSize);
		}
		else {
			logger.info("if total mails greater than or equals to batch size");
			messages = new SimpleMailMessage[batch];
			batchSize=batch;
			log.info("batch size: "+batchSize);
		}
	}
	public void setIndexZero() {
		messageIndex = 0;
	}
	public void increaseBatchSize() {
		SimpleMailMessage[] messages1;
		batchSize=batchSize+1;
		log.info("batch size now: "+batchSize);
		messages1 = new SimpleMailMessage[batchSize];
		for(int i=0;i<messages.length;i++) {
			messages1[i]=messages[i];
		}
		messages = new SimpleMailMessage[batchSize];
		for(int i=0;i<messages1.length;i++) {
			messages[i]=messages1[i];
		}
	}
	public boolean emailValidator(String email) {
		boolean isValid=false;
		log.info("inside email validator");
		try {
			InternetAddress internetAddress = new InternetAddress(email);
			internetAddress.validate();
			isValid = true;
         log.info(" Email is valid");
			return isValid;
		} catch (AddressException e) {
			log.info("this email address is incorrect: " + email);
			log.info(e.toString());
			return isValid;
		}
		
	}
	public boolean sendEmail(String toAddress, String fromAddress, String subject, String msgBody,int totalData,int dataRead,Integer sleep) {

		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom(fromAddress);
		simpleMailMessage.setTo(toAddress);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(msgBody);
		try {
			logger.info("adding emails into the array");
			 messages[messageIndex] = simpleMailMessage;
			 logger.info("Message: "+messages[messageIndex]+" Index Value: "+messageIndex+" Batch Size: "+batchSize);
			 messageIndex++;
			    if (messageIndex == batchSize) {
			    	logger.info("if batch size equals to no of mails added into array");
			    	logger.info("now going to send emails");
			    	try {
			            mailSender.send(messages);   		
				        logger.info("no of emails are sent: "+batchSize);
				        log.info("Total Data: "+totalData+" Data Read: "+dataRead);
				        int difference=totalData-dataRead;
				        log.info("Difference: "+difference);
				        setBatchSize(batchSize,difference);
				        logger.info("now batchSize is "+batchSize);
				        messageIndex = 0;

			    	}
			    	catch(MailSendException  send ) {
			    		log.info("inside email send exception");
				        logger.info("if emails fail to send: "+batchSize);
				        log.info(send.toString());
				        log.info("Total Data: "+totalData+" Data Read: "+dataRead);
				        int difference=totalData-dataRead;
				        log.info("Difference: "+difference);
				        setBatchSize(batchSize,difference);
				        messageIndex = 0;
						RunningAlertDb alertDb=new RunningAlertDb("alert009","error occurs while sending email",0);
						alertDbRepo.saveAlertDb(alertDb);
						logger.info("error occur while send email");
			
						return Boolean.FALSE;
			    	}
			    	catch(MailParseException  send ) {
			    		log.info("inside other mail exceptions");
				        logger.info("if emails fail to parse: "+batchSize);
				        log.info("Total Data: "+totalData+" Data Read: "+dataRead);
				        int difference=totalData-dataRead;
				        log.info("Difference: "+difference);
				        setBatchSize(batchSize,difference);
				        logger.info("now batchSize is "+batchSize);
				        messageIndex = 0;
						RunningAlertDb alertDb=new RunningAlertDb("alert009","error occurs while parsing email",0);
						alertDbRepo.saveAlertDb(alertDb);
						logger.info("error occur while parsing email");
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
			    	log.info("sleep time in milliseconds: "+sleep);
					Thread.sleep(sleep);
				}
				catch(Exception e) {
					log.info(e.toString());
				}
			    logger.info("Send Email Status TRUE");
			return Boolean.TRUE;
		}catch (Exception e) {
			logger.info("error occur");
			logger.error(e.getMessage());
			logger.info(e.toString());
			logger.info("Send Email Status FALSE");
			messageIndex = 0;
			return Boolean.FALSE;
		}
	}

	public void sendEmailWithAttactment(String toAddress, String fromAddress, String subject, String msgBody, String attachment) {

		MimeMessage message = javaMailSender.createMimeMessage();
		try{
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom(fromAddress);
			helper.setTo(toAddress);
			helper.setSubject(subject);
			helper.setText(msgBody);

			FileSystemResource file = new FileSystemResource(attachment);
			helper.addAttachment(file.getFilename(), file);

		}catch (MessagingException e) {
			throw new MailParseException(e);
		}

		javaMailSender.send(message);
	}
}

