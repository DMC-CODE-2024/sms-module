package com.ceir.CEIRPostman.service;

import com.ceir.CEIRPostman.RepositoryService.SystemConfigurationDbRepoImpl;
import com.ceir.CEIRPostman.constants.OperatorTypes;
import com.ceir.CEIRPostman.model.app.SystemConfigurationDb;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KanelService implements SmsManagementService{
    private final Logger log = LogManager.getLogger(getClass());
    @Value("${dlrMask}")
    private String dlrMaskValue;
    @Autowired
    SystemConfigurationDbRepoImpl systemConfigRepoImpl;

    @Override
    public String sendSms(String operatorName, String to, String from, String message, String correlationId, String msgLang) {
        try {
            log.info("Sending sms via " + operatorName + ": " + to + "," + from + "," + message + "," + correlationId);

            Map<String, String> operatorPropertyMap = getOperatorPropertyMap(operatorName);

            SystemConfigurationDb url = systemConfigRepoImpl.getDataByTag(operatorPropertyMap.get("url"));
            SystemConfigurationDb username = systemConfigRepoImpl.getDataByTag(operatorPropertyMap.get("username"));
            SystemConfigurationDb password = systemConfigRepoImpl.getDataByTag(operatorPropertyMap.get("password"));
            SystemConfigurationDb callbackUrl = systemConfigRepoImpl.getDataByTag(operatorPropertyMap.get("callback_url"));
            SystemConfigurationDb smsc = systemConfigRepoImpl.getDataByTag(operatorPropertyMap.get("smsc"));

            String dlrMask = dlrMaskValue;
            String coding = "0";
            if(msgLang.equals("kh")) {
                coding = "2";
            }
            String resp = KannelUtils.sendSMS(url.getValue(), from, to, username.getValue(), password.getValue(), message, dlrMask, callbackUrl.getValue(), coding, correlationId, OperatorTypes.METFONE.getValue(), smsc.getValue());
            log.info("Response from Metfone "+ resp);
            return "SUCCESS";
        } catch (ClientProtocolException cp) {
            cp.printStackTrace();
            return "FAILED";
        } catch (Exception e) {
            e.printStackTrace();
            return "SERVICE_UNAVAILABLE";
        }
    }

    private Map<String, String> getOperatorPropertyMap(String operatorName) {
        Map<String, String> operatorPropertyMap = new HashMap<>();
        operatorPropertyMap.put("url", operatorName + "_sms_url");
        operatorPropertyMap.put("username", operatorName + "_username");
        operatorPropertyMap.put("password", operatorName + "_password");
        operatorPropertyMap.put("callback_url", operatorName + "_callback_url");
        operatorPropertyMap.put("smsc", operatorName + "_smsc_code");
        return operatorPropertyMap;
    }
}
