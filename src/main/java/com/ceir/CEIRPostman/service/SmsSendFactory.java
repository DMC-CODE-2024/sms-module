package com.ceir.CEIRPostman.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsSendFactory {

    @Autowired
    SmartSms smart;

    @Autowired
    MetfoneKanelSms metfone;

    @Autowired
    SeatleSms seatle;

    @Autowired
    CellCardSms cellCard;

    @Autowired
    DefaultSms defaultSms;


    public SmsManagementService getSmsManagementService
            (String operator) {
        switch (operator) {
            case "smart":
                return smart;
            case "metfone":
                return metfone;
            case "seatel":
                return seatle;
            case "cellcard":
                return cellCard;
            default:
                return defaultSms;
        }
    }
}
