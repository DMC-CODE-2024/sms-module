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

}



