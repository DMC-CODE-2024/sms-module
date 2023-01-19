package com.ceir.CEIRPostman.util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.ceir.CEIRPostman.Repository.SystemConfigurationDbRepository;

@Configuration
public class MailConfig {

	@Autowired
	SystemConfigurationDbRepository systemConfigurationDbRepository;
	
	@Bean
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost( systemConfigurationDbRepository.getByTag("Email_Host").getValue());
        mailSender.setPort(Integer.valueOf( systemConfigurationDbRepository.getByTag("Email_Port").getValue() ));
        mailSender.setUsername(systemConfigurationDbRepository.getByTag("Email_Username").getValue());
       //for production and test  server
        mailSender.setPassword("");  
       
       //use for devlopment server
//       mailSender.setPassword(systemConfigurationDbRepository.getByTag("Email_Password").getValue()); 
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.auth", "false");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.debug", "true");
        return mailSender;
	}
}