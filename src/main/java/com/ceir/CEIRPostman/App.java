package com.ceir.CEIRPostman;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import com.ceir.CEIRPostman.service.SmsService;

@EnableAsync
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages ="com.ceir.CEIRPostman")
@EnableEncryptableProperties
public class App
{
	private static String[] args;
	public static void main(String[] args) {
		App.args = args;
		String operatorName = args.length > 0 ? args[0] : null;
		ConfigurableApplicationContext ctx = SpringApplication.run(App.class, args);
		SmsService fetch = ctx.getBean(SmsService.class, operatorName);
		new Thread(fetch).start();
	}

	@Bean
	public static String operatorName() {
		String operatorName = args.length > 0 ? args[0] : null;
		return operatorName;
	}

	@Bean
	public SmsService smsService() {
		String operatorName = operatorName();
		return new SmsService(operatorName);
	}
}



