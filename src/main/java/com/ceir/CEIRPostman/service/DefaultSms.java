package com.ceir.CEIRPostman.service;

import com.ceir.CEIRPostman.RepositoryService.SystemConfigurationDbRepoImpl;
import com.ceir.CEIRPostman.model.SystemConfigurationDb;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.ServerException;
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
        try {
            SystemConfigurationDb defaultAggType = systemConfigRepoImpl.getDataByTag("default_agg_type");
            if (defaultAggType.getValue().equals("Operator")) {
                SystemConfigurationDb defaultOperatorName = systemConfigRepoImpl.getDataByTag("default_operator_name");
                SmsManagementService smsProvider = smsSendFactory.getSmsManagementService(defaultOperatorName.getValue());
                String smsStatus = smsProvider.sendSms(to, from, message, correlationId, msgLang);
                return smsStatus;
            } else if (defaultAggType.getValue().equals("Aggregator")) {
                SystemConfigurationDb aggUrl = systemConfigRepoImpl.getDataByTag("agg_url");
                SystemConfigurationDb aggUsername = systemConfigRepoImpl.getDataByTag("agg_username");
                SystemConfigurationDb aggPassword = systemConfigRepoImpl.getDataByTag("agg_password");
                String smsStatus = sendAggSms(aggUrl.getValue(), aggUsername.getValue(), aggPassword.getValue(), message, to, from);
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

    private String sendAggSms(String url, String username, String password, String message, String sender, String phoneNumber) throws IOException {
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setParameter("username", username);
            uriBuilder.setParameter("pass", password);
            uriBuilder.setParameter("smstext", message);
            uriBuilder.setParameter("sender", sender);
            uriBuilder.setParameter("gsm", phoneNumber);

            URI uri = uriBuilder.build();
            HttpGet httpGet = new HttpGet(uri);

            httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            HttpEntity entity = httpResponse.getEntity();

            // Get the response body as a string
            String responseBody = EntityUtils.toString(entity);

            if (statusCode == 200 && responseBody.startsWith("0")) {
                // Extract the status message from the response body
                String statusMessage = responseBody.substring(responseBody.indexOf("[") + 1, responseBody.indexOf("]"));
                return "Success: " + statusMessage;
            } else {
                return "Error: " + responseBody;
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
