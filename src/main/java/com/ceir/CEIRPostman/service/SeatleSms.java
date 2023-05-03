package com.ceir.CEIRPostman.service;

import com.ceir.CEIRPostman.RepositoryService.SystemConfigurationDbRepoImpl;
import com.ceir.CEIRPostman.constants.OperatorTypes;
import com.ceir.CEIRPostman.model.SystemConfigurationDb;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.rmi.ServerException;

@Component
public class SeatleSms implements SmsManagementService{
    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    SystemConfigurationDbRepoImpl systemConfigRepoImpl;
    @Override
    public String sendSms(String to, String from, String message, String correlationId, String msgLang) {
        try {
            log.info("Sending sms via Seatle: "+to+","+from+","+message+","+","+correlationId);
            SystemConfigurationDb url = systemConfigRepoImpl.getDataByTag("seatle_sms_url");
            SystemConfigurationDb username = systemConfigRepoImpl.getDataByTag("seatle_username");
            SystemConfigurationDb password = systemConfigRepoImpl.getDataByTag("seatle_password");
            SystemConfigurationDb callbackUrl = systemConfigRepoImpl.getDataByTag("seatle_callback_url");
            String dlrMask = "7";
            String coding = "0";
            if(msgLang == "kh") {
                coding = "2";
            }
            String resp = KannelUtils.sendSMS(url.getValue(), from, to, username.getValue(), password.getValue(), message, dlrMask, callbackUrl.getValue(), coding, correlationId, OperatorTypes.SEATLE.getValue());
            log.info("Response from Seatle "+ resp);
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
