package com.ceir.CEIRPostman.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;

public class KannelUtils {

    public static String sendSMS(String url, String from, String to, String username, String password, String message, String dlrMask, String dlrUrl, String coding, String correlationId, String operatorName, String smsc) throws IOException, URISyntaxException {
//        String url = "http://your-kannel-server:13013/cgi-bin/sendsms";
        try {
//            URI uri = new URI(dlrUrl);
//            uri = new URIBuilder(uri)
//                    .addParameter("answer", "%A")
//                    .addParameter("status", "%d")
//                    .addParameter("dlrvTime", "%T")
//                    .addParameter("myId", correlationId)
//                    .addParameter("operatorName", operatorName)
//                    .build();
            if (dlrMask == null) {
                dlrMask = "1";
            }
            String uri = String.format("%s?answer=%s&status=%s&dlrvTime=%s&myId=%s&operatorName=%s",
                    dlrUrl, "%A", "%d", "%T", correlationId, operatorName);

            HttpClient httpClient = HttpClientBuilder.create().build();

            // Construct the URL with query parameters
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setParameter("smsc", smsc);
            uriBuilder.setParameter("username", username);
            uriBuilder.setParameter("password", password);
            uriBuilder.setParameter("to", to);
            uriBuilder.setParameter("text", message);
            uriBuilder.setParameter("coding", coding);
            uriBuilder.setParameter("charset", "UTF-8");
            uriBuilder.setParameter("dlr-mask", dlrMask);
            uriBuilder.setParameter("dlr-url", uri);
            uriBuilder.setParameter("from", from);

            HttpGet httpGet = new HttpGet(uriBuilder.build());

            System.out.println("Final Request URL for to: " + to + ", url: " + httpGet.getURI().toString());

            HttpResponse httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            HttpEntity entity = httpResponse.getEntity();

            // Get the response body as a string
            String responseBody = EntityUtils.toString(entity);
            System.out.println(responseBody);
            if (statusCode >= 400 && statusCode <= 499) {
                throw new ClientProtocolException("HTTP " + statusCode + ": " + httpResponse.getStatusLine().getReasonPhrase());
            } else if (statusCode >= 500 && statusCode <= 599) {
                throw new ServerException("HTTP " + statusCode + ": " + httpResponse.getStatusLine().getReasonPhrase());
            }
            return responseBody;
        } catch (Exception e) {
            System.out.println("Exception while sending message through kanel: "+e);
            throw e;
        }

    }

}
