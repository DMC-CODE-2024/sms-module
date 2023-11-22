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

import java.rmi.ServerException;

@Component
public class MetfoneKanelSms implements SmsManagementService{
    private final Logger log = LogManager.getLogger(getClass());
    @Value("${dlrMask}")
    private String dlrMaskValue;
    @Autowired
    SystemConfigurationDbRepoImpl systemConfigRepoImpl;
    @Override
    public String sendSms(String to, String from, String message, String correlationId, String msgLang) {
        try {
            log.info("Sending sms via Metfone: "+to+","+from+","+message+","+","+correlationId);
            SystemConfigurationDb url = systemConfigRepoImpl.getDataByTag("metfone_sms_url");
            SystemConfigurationDb username = systemConfigRepoImpl.getDataByTag("metfone_username");
            SystemConfigurationDb password = systemConfigRepoImpl.getDataByTag("metfone_password");
            SystemConfigurationDb callbackUrl = systemConfigRepoImpl.getDataByTag("metfone_callback_url");
            SystemConfigurationDb smsc = systemConfigRepoImpl.getDataByTag("metfone_smsc_code");
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
        } catch (ServerException se) {
            se.printStackTrace();
            return "SERVICE_UNAVAILABLE";
        } catch (Exception e) {
            e.printStackTrace();
            return "SERVICE_UNAVAILABLE";
        }
    }
}

