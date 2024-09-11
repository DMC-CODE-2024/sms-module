package com.ceir.CEIRPostman.service;

import com.ceir.CEIRPostman.RepositoryService.SystemConfigurationDbRepoImpl;
import com.ceir.CEIRPostman.model.app.SystemConfigurationDb;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.http.client.HttpClient;

import java.rmi.ServerException;
import java.util.HashMap;
import java.util.Map;

@Component
public class MacraCustom implements SmsManagementService {

    private final Logger log = LogManager.getLogger(getClass());

    @Autowired
    SystemConfigurationDbRepoImpl systemConfigRepoImpl;

    @Override
    public String sendSms(String operatorName, String to, String from, String message, String correlationId, String msgLang) {
        try {
            log.info("Sending sms via MacraCustom: " + to + "," + from + "," + message + "," + correlationId);

            Map<String, String> operatorPropertyMap = getOperatorPropertyMap(operatorName);

            SystemConfigurationDb apiUrl = systemConfigRepoImpl.getDataByTag(operatorPropertyMap.get("url"));
            SystemConfigurationDb apiKey = systemConfigRepoImpl.getDataByTag(operatorPropertyMap.get("apiKey"));

            String response = sendMacraSms(apiUrl.getValue(), apiKey.getValue(), to, message, from);

            log.info("Response from MacraCustom: " + response);
            return "SUCCESS";
        } catch (ClientProtocolException cp) {
            cp.printStackTrace();
            return "FAILED";
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error sending sms via MacraCustom", e);
            return "SERVICE_UNAVAILABLE";
        }
    }

    public String sendMacraSms(String apiUrl, String apiKey, String phoneNumber, String message, String senderId) throws Exception {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(apiUrl);

            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            String formBody = String.format("apiKey=%s&phoneNumber=%s&message=%s&senderId=%s",
                    apiKey, phoneNumber, message, senderId);

            System.out.println("Form Body being sent: " + formBody);

            StringEntity requestEntity = new StringEntity(formBody, ContentType.APPLICATION_FORM_URLENCODED);
            httpPost.setEntity(requestEntity);

            // Log the curl command equivalent of the request
            String curlCmd = String.format("curl -i -X POST '%s' -d 'apiKey=%s&phoneNumber=%s&message=%s&senderId=%s'",
                    apiUrl, apiKey, phoneNumber, message, senderId);
            log.info("Request for MacraCustom: " + curlCmd);

            HttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);

            if (statusCode >= 400 && statusCode <= 499) {
                throw new ClientProtocolException("HTTP " + statusCode + ": " + response.getStatusLine().getReasonPhrase());
            } else if (statusCode >= 500 && statusCode <= 599) {
                throw new ServerException("HTTP " + statusCode + ": " + response.getStatusLine().getReasonPhrase());
            }
            return responseBody;
        } catch (Exception e) {
            System.out.println("Exception while sending message through macra api: " + e);
            throw e;
        }
    }

    private Map<String, String> getOperatorPropertyMap(String operatorName) {
        Map<String, String> operatorPropertyMap = new HashMap<>();
        operatorPropertyMap.put("url", operatorName + "_sms_url");
        operatorPropertyMap.put("apiKey", operatorName + "_api_key");
        return operatorPropertyMap;
    }
}

