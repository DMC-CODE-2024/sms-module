package com.ceir.CEIRPostman.service;

import com.ceir.CEIRPostman.RepositoryService.SystemConfigurationDbRepoImpl;
import com.ceir.CEIRPostman.model.SystemConfigurationDb;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.rmi.ServerException;

@Component
public class MetfoneSms implements SmsManagementService{

    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    SystemConfigurationDbRepoImpl systemConfigRepoImpl;
    @Override
    public String sendSms(String to, String from, String message, String correlationId, String msgLang) {
        try{
            log.info("Sending sms via Metfone: "+to+","+from+","+message+","+","+correlationId);
            String resp = sendRequest(message,to, from, correlationId);
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

    public String sendRequest(String message, String msisdn, String senderId, String reqID) throws IOException, SAXException, ParserConfigurationException {

        SystemConfigurationDb url = systemConfigRepoImpl.getDataByTag("metfone_sms_url");
        SystemConfigurationDb accessKey = systemConfigRepoImpl.getDataByTag("metfone_access_key");
        SystemConfigurationDb username = systemConfigRepoImpl.getDataByTag("metfone_username");
        SystemConfigurationDb password = systemConfigRepoImpl.getDataByTag("metfone_password");
//        String url = "https://xxx.metfone.com.kh/apigw?wsdl";

        String responseString = null;

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url.getValue());

        // set the SOAP envelope and body
        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.bccsgw.viettel.com/\"><soapenv:Header/><soapenv:Body><web:gwOperation><Input><username>"
                + username.getValue()
                + "</username><password>"
                + password.getValue()
                + "</password><wscode>sendSMS</wscode><accessKey>"
                + accessKey.getValue()
                + "</accessKey><param name=\"message\" value=\""
                + message
                + "\"/><param name=\"msisdn\" value=\""
                + msisdn
                + "\"/><param name=\"senderId\" value=\""
                + senderId
                + "\"/><param name=\"reqID\" value=\""
                + reqID
                + "\"/></Input></web:gwOperation></soapenv:Body></soapenv:Envelope>";

        StringEntity requestEntity = new StringEntity(requestBody, "UTF-8");
        post.setEntity(requestEntity);
        post.setHeader("Content-Type", "text/xml;charset=UTF-8");

        // execute the POST request
        HttpResponse response = client.execute(post);

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode >= 400 && statusCode <= 499) {
            throw new ClientProtocolException("HTTP " + statusCode + ": " + response.getStatusLine().getReasonPhrase());
        } else if (statusCode >= 500 && statusCode <= 599) {
            throw new ServerException("HTTP " + statusCode + ": " + response.getStatusLine().getReasonPhrase());
        } else {
            HttpEntity entity = response.getEntity();

            // read the response as a string
            responseString = EntityUtils.toString(entity, "UTF-8");

            // parse the XML response
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(responseString));
            Document doc = builder.parse(is);

            // extract the error code and description from the response
            String errorCode = doc.getElementsByTagName("errorCode").item(0).getTextContent();
            String errorDescription = doc.getElementsByTagName("errorDescription").item(0).getTextContent();

            // return the response as a string
            return "errorCode: " + errorCode + ", errorDescription: " + errorDescription;
        }
    }

}
