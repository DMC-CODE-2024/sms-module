package com.ceir.CEIRPostman.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ceir.CEIRPostman.RepositoryService.SystemConfigurationDbRepoImpl;
import com.ceir.CEIRPostman.model.SystemConfigurationDb;

@Service
public class SmsConfig {

     private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(getClass());

     @Autowired
     SystemConfigurationDbRepoImpl systemConfigRepoImpl;

     public boolean[] sendSMS(String msisdn[], String text[]) {

          boolean[] status = new boolean[msisdn.length];
          SystemConfigurationDb smsSubmitUrl = systemConfigRepoImpl.getDataByTag("sms_submit_url");
          log.info("smsSubmitUrl " + smsSubmitUrl);
          Arrays.fill(status, false);
          try {
               int size = msisdn.length;
               for (int i = 0; i < size; i++) {
                    log.info("SendSMS msisnd=" + msisdn[i] + ",Text=" + text[i]);
                    String enText = URLEncoder.encode(text[i], "UTF-8");
                    String enMsisdn = URLEncoder.encode(msisdn[i], "UTF-8");

                    String url = smsSubmitUrl.getValue();

                    url = url.replaceAll("<MSISDN>", enMsisdn);
                    url = url.replaceAll("<MESSAGE>", enText);

                    log.info("full Url  " + url);

                    String resp = callURL(url);
                    resp = resp.toLowerCase();
                    log.info("Response: " + resp);
                    if (resp.indexOf("message_id") != -1) {
                         status[i] = true;
                    } else {
                         status[i] = false;
                    }
                    log.info("Status of message " + i + " : " + status[i]);
               }
          } catch (Exception exp) {
               exp.printStackTrace();
          }
          return status;
     }

     public String callURL(String urlString) {
          String inputResponse = "";
          String messageId = "";
          try {

               log.info(urlString);
               URL url = new URL(urlString);
               HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
               urlConn.setConnectTimeout(30 * 1000);
               urlConn.setReadTimeout(30 * 1000);
               int code = urlConn.getResponseCode();
               BufferedReader inputReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
               String inputLine;
               while ((inputLine = inputReader.readLine()) != null) {
                    inputResponse = inputResponse + inputLine;
               }
               inputReader.close();
               if ("200".equals(Integer.toString(code)) || "OK".equals(Integer.toString(code))) {
                    JSONObject myResponse = new JSONObject(inputResponse.toString());
                    log.info("Message Sent Successfully");
                    log.info("messageId: " + myResponse.getString("message_id"));
                    messageId = myResponse.getString("message_id");
               } else {
                    messageId = "No message id as message failed to send";
               }
               log.info("messageId: " + messageId);
          } catch (Exception exp) {
               inputResponse = exp.getMessage();
          }
          log.info(inputResponse);
          return inputResponse;

     }

}
