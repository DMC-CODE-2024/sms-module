package com.ceir.CEIRPostman.builder;


import com.ceir.CEIRPostman.dto.AlertDTO;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class AlertBuilder {
    public static AlertDTO createAlert(String alertId, String alertMessage, Integer userId) {
        String ip;
        String serverName;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            ip = localHost.getHostAddress();
            serverName = localHost.getHostName();
        } catch (UnknownHostException e) {
            ip = "127.0.0.1";
            serverName = "UnknownHost";
        }
        return AlertDTO.builder()
                .alertId(alertId)
                .alertMessage(alertMessage)
                .alertProcess("SMS_MODULE")
                .description("")
                .featureName("Send Sms")
                .ip(ip)
                .priority("High")
                .remarks("")
                .serverName(serverName)
                .status(Integer.valueOf(0))
                .txnId(UUID.randomUUID().toString())
                .userId(userId)
                .build();
    }
}
