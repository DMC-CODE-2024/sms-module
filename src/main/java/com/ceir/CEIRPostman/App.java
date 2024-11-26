package com.ceir.CEIRPostman;
import com.ceir.CEIRPostman.constants.OperatorTypes;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

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
		ConfigurableApplicationContext ctx = SpringApplication.run(App.class, args);
		SmsService fetch = ctx.getBean(SmsService.class);
		new Thread(fetch).start();
	}

	@Bean
	public static String operatorName() {
		String operatorName = args.length > 0 ? args[0] : null;
		return operatorName;
	}

	@Bean
	public SmsService smsService() {
		List<String> operators = new ArrayList<>();
		String operatorName = operatorName();
		if(operatorName.contains("=") && operatorName.contains(OperatorTypes.DEFAULT.getValue())){
			String [] defaultArgs = (operatorName.split("="));
			operators = new ArrayList<>(Arrays.asList(defaultArgs[1].split(",")));
			operators.add(defaultArgs[0]);
			return new SmsService(defaultArgs[0], operators);
		} else {
			return new SmsService(operatorName, operators);
		}
	}

}



