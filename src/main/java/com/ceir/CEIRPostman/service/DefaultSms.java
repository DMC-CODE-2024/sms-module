package com.ceir.CEIRPostman.service;

import com.ceir.CEIRPostman.RepositoryService.SystemConfigurationDbRepoImpl;
import com.ceir.CEIRPostman.model.SystemConfigurationDb;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DefaultSms implements SmsManagementService{

    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    SystemConfigurationDbRepoImpl systemConfigRepoImpl;
    @Autowired
    SmsSendFactory smsSendFactory;

    @Override
    public String sendSms(String to, String from, String message, String correlationId, String msgLang) {
        SystemConfigurationDb defaultAggType = systemConfigRepoImpl.getDataByTag("default_agg_type");
        if (defaultAggType.getValue().equals("Operator")) {
            SystemConfigurationDb defaultOperatorName = systemConfigRepoImpl.getDataByTag("default_operator_name");
            SmsManagementService smsProvider = smsSendFactory.getSmsManagementService(defaultOperatorName.getValue());
            String smsStatus = smsProvider.sendSms(to, from, message, correlationId, msgLang);
            return smsStatus;
        } else if (defaultAggType.getValue().equals("Aggregator")) {
            return "SERVICE_UNAVAILABLE";
        } else {
            return "SERVICE_UNAVAILABLE";
        }
    }
}
