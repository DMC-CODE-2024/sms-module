package com.ceir.CEIRPostman;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import com.ceir.CEIRPostman.service.EmailService;
import com.ceir.CEIRPostman.service.SmsService;

@EnableAsync
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages ="com.ceir.CEIRPostman")
public class App 
{
	public static void main( String[] args )
	{
		ConfigurableApplicationContext ctx =SpringApplication.run(App.class, args);
		SmsService fetch=ctx.getBean(SmsService.class);
		new Thread(fetch).start();
	}
}



