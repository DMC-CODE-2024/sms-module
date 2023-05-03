package com.ceir.CEIRPostman;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
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
@EnableEncryptableProperties
public class App 
{
	public static void main( String[] args )
	{
		ConfigurableApplicationContext ctx =SpringApplication.run(App.class, args);
		String operatorName = args.length > 0 ? args[0] : null;
		SmsService fetch=ctx.getBean(SmsService.class,operatorName);
		new Thread(fetch).start();
	}
}



