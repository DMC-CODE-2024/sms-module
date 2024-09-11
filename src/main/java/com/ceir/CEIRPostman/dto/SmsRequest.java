package com.ceir.CEIRPostman.dto;

public class SmsRequest {
    private String apiKey;
    private String phoneNumber;
    private String message;
    private String senderId;

    public SmsRequest() {
    }

    public SmsRequest(String apiKey, String phoneNumber, String message, String senderId) {
        this.apiKey = apiKey;
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.senderId = senderId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }


}
