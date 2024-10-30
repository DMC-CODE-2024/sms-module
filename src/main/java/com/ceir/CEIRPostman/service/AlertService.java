package com.ceir.CEIRPostman.service;

import com.ceir.CEIRPostman.builder.AlertBuilder;
import com.ceir.CEIRPostman.dto.AlertDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AlertService {
    private final Logger log = LogManager.getLogger(getClass());

    @Value("${eirs.alert.url}")
    private String alertApiUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public AlertService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void raiseAlert(String alertId, String alertMessage, Integer userId) {
        AlertDTO alertDTO = AlertBuilder.createAlert(alertId, alertMessage, userId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<AlertDTO> entity = new HttpEntity(alertDTO, (MultiValueMap)headers);
        ResponseEntity<String> response = this.restTemplate.exchange(this.alertApiUrl, HttpMethod.POST, entity, String.class, new Object[0]);
        if (response.getStatusCode().is2xxSuccessful()) {
            this.log.info("Alert raised with response: " + (String)response.getBody());
        } else {
            this.log.error("Failed to raise alert with response: " + (String)response.getBody());
        }
    }
}