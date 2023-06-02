package com.ceir.CEIRPostman.service;

import com.ceir.CEIRPostman.RepositoryService.SystemConfigurationDbRepoImpl;
import com.ceir.CEIRPostman.model.SystemConfigurationDb;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class SmartSms implements SmsManagementService{

    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    SystemConfigurationDbRepoImpl systemConfigRepoImpl;
    @Override
    public String sendSms(String to, String from, String message, String correlationId, String msgLang) {
        try {
            log.info("Sending sms via Smart: "+to+","+from+","+message+","+","+correlationId);
            String token = null;
            SystemConfigurationDb savedToken = systemConfigRepoImpl.getDataByTag("smart_token");
            SystemConfigurationDb tokenTimeoutInSec = systemConfigRepoImpl.getDataByTag("smart_token_timeout_in_sec");
            SystemConfigurationDb senderName = systemConfigRepoImpl.getDataByTag("smart_sender_name");
            SystemConfigurationDb callbackUrl = systemConfigRepoImpl.getDataByTag("smart_callback_url");
            Date lastUpdated = savedToken.getModifiedOn();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String lastUpdatedDate = formatter.format(lastUpdated);
            String currentDate = getCurrentDateTime();
            boolean isDateDifferenceLessThan1Hour = isDateDifferenceLessThan1Hour(currentDate, lastUpdatedDate, Integer.parseInt(tokenTimeoutInSec.getValue()));
            if(isDateDifferenceLessThan1Hour) {
                token = savedToken.getValue();

            } else {
                String[] resp = getToken();
                token = resp[0];
                String expiresIn = resp[1];
                tokenTimeoutInSec.setValue(expiresIn);
                systemConfigRepoImpl.saveConfigDb(tokenTimeoutInSec);
            }
            String resp = sendRequest(to, from, message, correlationId, callbackUrl.getValue(), correlationId, senderName.getValue(), token).toString();
            log.info("Response from Smart "+resp);
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

    public String[] getToken() throws IOException {
        SystemConfigurationDb url = systemConfigRepoImpl.getDataByTag("smart_token_url");
//        String url = "https://mife.smart.com.kh:8243/token";
        SystemConfigurationDb clientId = systemConfigRepoImpl.getDataByTag("smart_consumer_key");
//        String clientId = "e3A07qYUfzv64934dqMHhMGZ5yIa";
        SystemConfigurationDb clientSecret = systemConfigRepoImpl.getDataByTag("smart_consumer_secret");
//        String clientSecret = "oZ0DWsPp2nBh5Sv159Y9f15fEeoa";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url.getValue());

        // Set request headers
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
        String authHeader = Base64.getEncoder().encodeToString((clientId.getValue() + ":" + clientSecret.getValue()).getBytes(StandardCharsets.UTF_8));
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + authHeader);
        httpPost.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        // Set request body
        String requestBody = "grant_type=client_credentials";
        httpPost.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));

        // Execute request
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        String responseBody = EntityUtils.toString(httpEntity);

        // Extract token from response
        JSONObject jsonObject = new JSONObject(responseBody);
        String accessToken = jsonObject.getString("access_token");
        String expiresIn = jsonObject.getString("expires_in");

        // Cleanup
        EntityUtils.consume(httpEntity);
        httpClient.close();

        return new String[] {accessToken, expiresIn};
    }

    public static String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return now.format(formatter);
    }

    public static boolean isDateDifferenceLessThan1Hour(String dateStr1, String dateStr2, Integer timeout) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = dateFormat.parse(dateStr1);
            date2 = dateFormat.parse(dateStr2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diffInMs = Math.abs(date1.getTime() - date2.getTime());
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

        return diffInSec < timeout;
    }

    public JSONObject sendRequest(String to, String senderAddress, String message, String clientCorrelator, String notifyURL,
                                  String callbackData, String senderName, String accessToken) throws IOException {
        SystemConfigurationDb smartSmsUrl = systemConfigRepoImpl.getDataByTag("smart_sms_url");
//        String baseUrl = "https://mife.smart.com.kh:8243/smsmessaging/v1/outbound/tel/%s/requests";
        String urlEncodedSenderAddress = URLEncoder.encode("tel:"+senderAddress, StandardCharsets.UTF_8);

        String url = String.format(smartSmsUrl.getValue(), urlEncodedSenderAddress);

        int maxRetries = 2;
        int retryCount = 0;
        boolean retry = true;
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = null;
        while (retry) {
            httpPost = getHttpPostEntity(url, accessToken, to, senderAddress, message,clientCorrelator, notifyURL, callbackData, senderName);
            // Execute request
            try {
                String responseBody = null;
                httpResponse = httpClient.execute(httpPost);
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                HttpEntity httpEntity = httpResponse.getEntity();
                responseBody = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
                if (statusCode >= 400 && statusCode <= 499) {
                    if (statusCode == 401 && retryCount < maxRetries) {
                        String[] resp = getToken();
                        accessToken = resp[0];
                        retryCount++;
                    } else {
                        throw new ClientProtocolException("HTTP " + statusCode + ": " + httpResponse.getStatusLine().getReasonPhrase());
                    }
                } else if (statusCode >= 500 && statusCode <= 599) {
                    throw new ServerException("HTTP " + statusCode + ": " + httpResponse.getStatusLine().getReasonPhrase());
                } else {
                    return new JSONObject(responseBody);
                }
            } catch (ClientProtocolException e) {
                if (httpResponse != null) {
                    EntityUtils.consumeQuietly(httpResponse.getEntity());
                }
                httpClient.close();
                throw e;
            } catch (IOException e) {
                if (httpResponse != null) {
                    EntityUtils.consumeQuietly(httpResponse.getEntity());
                }
                httpClient.close();
                throw e;
            }

            // Retry limit reached
            if (retryCount >= maxRetries) {
                retry = false;
            }
        }

        if (httpResponse != null) {
            EntityUtils.consumeQuietly(httpResponse.getEntity());
        }

        throw new ServerException("Exceeded maximum number of retries");

    }

    public HttpPost getHttpPostEntity(String url, String accessToken, String to, String senderAddress, String message, String clientCorrelator, String notifyURL, String callbackData, String senderName) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);

        // Set request headers
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        httpPost.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        // Set request body
        JSONObject requestBody = new JSONObject();
        JSONObject outboundSMSMessageRequest = new JSONObject();
        JSONArray address = new JSONArray();
        address.put("tel:" + to);
        outboundSMSMessageRequest.put("address", address);
        outboundSMSMessageRequest.put("senderAddress", "tel:" + senderAddress);
        JSONObject outboundSMSTextMessage = new JSONObject();
        outboundSMSTextMessage.put("message", message);
        outboundSMSMessageRequest.put("outboundSMSTextMessage", outboundSMSTextMessage);
        outboundSMSMessageRequest.put("clientCorrelator", clientCorrelator);
        JSONObject receiptRequest = new JSONObject();
        receiptRequest.put("notifyURL", notifyURL);
        receiptRequest.put("callbackData", callbackData);
        outboundSMSMessageRequest.put("receiptRequest", receiptRequest);
        outboundSMSMessageRequest.put("senderName", senderName);
        requestBody.put("outboundSMSMessageRequest", outboundSMSMessageRequest);

        httpPost.setEntity(new StringEntity(requestBody.toString()));
        return httpPost;
    }

}
