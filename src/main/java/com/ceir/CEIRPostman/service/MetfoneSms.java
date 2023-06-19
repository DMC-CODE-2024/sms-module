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
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
import java.util.Arrays;

@Component
public class MetfoneSms implements SmsManagementService{

    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    SystemConfigurationDbRepoImpl systemConfigRepoImpl;
    @Override
    public String sendSms(String to, String from, String message, String correlationId, String msgLang) {
        try{
            log.info("Sending sms via Metfone: "+to+","+from+","+message+","+","+correlationId);
            String resp = sendRequest(message,to, from, correlationId, msgLang);
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

    public String sendRequest(String message, String msisdn, String senderId, String reqID, String msgLang) throws IOException, SAXException, ParserConfigurationException {

        SystemConfigurationDb url = systemConfigRepoImpl.getDataByTag("metfone_sms_url");
        SystemConfigurationDb accessKey = systemConfigRepoImpl.getDataByTag("metfone_access_key");
        SystemConfigurationDb username = systemConfigRepoImpl.getDataByTag("metfone_username");
        SystemConfigurationDb password = systemConfigRepoImpl.getDataByTag("metfone_password");
//        String url = "https://xxx.metfone.com.kh/apigw?wsdl";

        String responseString = null;

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url.getValue());
        Boolean unicode = msgLang == "kh" ? true : false;
        if(unicode) {
            byte[] byteArray = message.getBytes(StandardCharsets.UTF_16BE);
            message = Arrays.toString(byteArray);
        }

        // set the SOAP envelope and body
        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.bccsgw.viettel.com/\">"
                + " <soapenv:Header/>"
                + " <soapenv:Body>"
                + "   <web:gwOperation>"
                + "     <Input>"
                + "       <username>" + username.getValue() + "</username>"
                + "       <password>" + password.getValue() + "</password>"
                + "       <wscode>sendSMSByDmc</wscode>"
                + "       <accessKey>" + accessKey.getValue() + "</accessKey>"
                + "       <!--Zero or more repetitions:-->"
                + "       <param name=\"message\" value=\"" + message + "\"/>"
                + "       <param name=\"msisdn\" value=\"" + msisdn + "\"/>"
                + "       <param name=\"senderId\" value=\"" + "1205" + "\"/>"
                + "       <param name=\"reqId\" value=\"" + reqID + "\"/>"
                + "       <param name=\"unicode\" value=\"" + unicode + "\"/>"
                + "     </Input>"
                + "   </web:gwOperation>"
                + " </soapenv:Body>"
                + "</soapenv:Envelope>";

        System.out.println("BODY: "+requestBody);

        StringEntity requestEntity = new StringEntity(requestBody, "UTF-8");
        post.setEntity(requestEntity);
        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("SOAPAction", "Post");

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
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML response string
            Document document = builder.parse(new InputSource(new StringReader(responseString)));

            // Get the inner 'original' XML content
            Element originalElement = (Element) document.getElementsByTagName("original").item(0);
            String originalXml = originalElement.getTextContent();

            // Parse the inner XML content
            Document originalDocument = builder.parse(new InputSource(new StringReader(originalXml)));

            // Extract errorCode and errorDescription from the inner document
            String errorCode = originalDocument.getElementsByTagName("errorCode").item(0).getTextContent();
            String errorDescription = originalDocument.getElementsByTagName("errorDescription").item(0).getTextContent();
            // return the response as a string
            return "errorCode: " + errorCode + ", errorDescription: " + errorDescription;
        }
    }

}
