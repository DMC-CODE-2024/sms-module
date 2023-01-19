package com.ceir.CeirCode.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertiesReaders {

	@Value("${spring.jpa.properties.hibernate.dialect}")
	public String dialect;
}
