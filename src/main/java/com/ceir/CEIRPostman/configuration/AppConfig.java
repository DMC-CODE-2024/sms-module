package com.ceir.CEIRPostman.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class AppConfig {
	
	private Integer maxCount;
	public Integer getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(Integer maxCount) {
		this.maxCount = maxCount;
	}

	@Override
	public String toString() {
		return "AppConfig [maxCount=" + maxCount + "]";
	}
	
}
