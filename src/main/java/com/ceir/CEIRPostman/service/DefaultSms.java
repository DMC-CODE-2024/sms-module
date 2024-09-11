package com.ceir.CEIRPostman.service;

import com.ceir.CEIRPostman.Repository.app.OperatorRepository;
import com.ceir.CEIRPostman.RepositoryService.SystemConfigurationDbRepoImpl;
import com.ceir.CEIRPostman.model.app.Operator;
import com.ceir.CEIRPostman.model.app.SystemConfigurationDb;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.ServerException;
import java.util.Optional;

@Component
public class DefaultSms implements SmsManagementService{

    private final Logger log = LogManager.getLogger(getClass());
    @Autowired
    SystemConfigurationDbRepoImpl systemConfigRepoImpl;
    @Autowired
    SmsSendFactory smsSendFactory;
    @Autowired
    OperatorRepository operatorRepository;

    @Override
    public String sendSms(String operatorName, String to, String from, String message, String correlationId, String msgLang) {
        try {
            SystemConfigurationDb defaultAggType = systemConfigRepoImpl.getDataByTag("default_agg_type");
            log.info("Sending sms via Aggregator for type: "+defaultAggType.getValue()+","+to+","+from+","+message+","+","+correlationId);
            if (defaultAggType.getValue().equals("Operator")) {
                SystemConfigurationDb defaultOperatorName = systemConfigRepoImpl.getDataByTag("default_operator_name");
                Optional<Operator> operatorEntity = operatorRepository.findByOperatorName(operatorName.toLowerCase());
                if(operatorEntity.isPresent()) {
                    SmsManagementService smsProvider = smsSendFactory.getSmsManagementService(defaultOperatorName.getValue(), operatorEntity.get().getChannelType());
                    String fromSender = systemConfigRepoImpl.getDataByTag(defaultOperatorName.getValue()+"_sender_id").getValue();
                    return smsProvider.sendSms(operatorName.toLowerCase(), to, fromSender, message, correlationId, msgLang);
                } else {
                    throw new Exception("Operator not found with name: " + operatorName);
                }
            } else if (defaultAggType.getValue().equals("Aggregator")) {
                SystemConfigurationDb aggUrl = systemConfigRepoImpl.getDataByTag("agg_url");
                SystemConfigurationDb aggUsername = systemConfigRepoImpl.getDataByTag("agg_username");
                SystemConfigurationDb aggPassword = systemConfigRepoImpl.getDataByTag("agg_password");
                String smsStatus = sendAggSms(aggUrl.getValue(), aggUsername.getValue(), aggPassword.getValue(), message, to, from, correlationId);
                log.info("Response from Aggregator "+ smsStatus);
                if (smsStatus.toUpperCase().contains("SUCCESS")){
                    return "SUCCESS";
                } else {
                    return "FAILED";
                }
            }
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
        return "SERVICE_UNAVAILABLE";
    }

    private String sendAggSms(String url, String username, String password, String message, String phoneNumber, String sender, String correlationId) throws IOException {
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setParameter("username", username);
            uriBuilder.setParameter("pass", password);
            uriBuilder.setParameter("smstext", message);
            uriBuilder.setParameter("sender", sender);
            uriBuilder.setParameter("gsm", phoneNumber);
            uriBuilder.setParameter("cd", correlationId);

            URI uri = uriBuilder.build();
            HttpGet httpGet = new HttpGet(uri);

            System.out.println("Final Request URL for phone number: " + phoneNumber + ", url: " + httpGet.getURI().toString());

            httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            HttpEntity entity = httpResponse.getEntity();

            // Get the response body as a string
            String responseBody = EntityUtils.toString(entity);
            System.out.println("Response from aggregator: "+ responseBody);
            if (statusCode == 200 && responseBody.startsWith("0")) {
                // Extract the status message from the response body
                String statusMessage = responseBody.substring(responseBody.indexOf("[") + 1, responseBody.indexOf("]"));
                return "SUCCESS: " + statusMessage;
            } else {
                return "ERROR: " + responseBody;
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
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
