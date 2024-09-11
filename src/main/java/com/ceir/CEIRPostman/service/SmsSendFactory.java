package com.ceir.CEIRPostman.service;

import com.ceir.CEIRPostman.constants.ChannelType;
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
    @Autowired
    KanelService kanelService;
    @Autowired
    MacraCustom macraCustom;


    public SmsManagementService getSmsManagementService
            (String operator, String channelType) {
        if(channelType.equals(ChannelType.kanel.name())){
            return kanelService;
        } else if(channelType.equals(ChannelType.macra_custom.name())) {
            return macraCustom;
        } else {
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
}
