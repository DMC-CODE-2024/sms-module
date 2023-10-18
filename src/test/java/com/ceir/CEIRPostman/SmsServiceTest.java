//package com.ceir.CEIRPostman;
//
//import com.ceir.CEIRPostman.RepositoryService.*;
//import com.ceir.CEIRPostman.model.app.Notification;
//import com.ceir.CEIRPostman.service.SmsManagementService;
//import com.ceir.CEIRPostman.service.SmsSendFactory;
//import com.ceir.CEIRPostman.service.SmsService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static javassist.bytecode.analysis.Type.eq;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//import static org.mockito.Mockito.verify;
//
//@RunWith(MockitoJUnitRunner.class)
//public class SmsServiceTest {
//    @Mock
//    private NotificationRepoImpl notificationRepoImpl;
//    @Mock
//    private SystemConfigurationDbRepoImpl systemConfigRepoImpl;
//    @Mock
//    private RunningAlertRepoService alertDbRepo;
//
//    @Mock
//    private SmsSendFactory smsSendFactory;
//
//    @InjectMocks
//    private SmsService smsService;
//
//    @Test
//    public void testSmsService() throws Exception {
//        // Set up test data
//        Notification notification1 = new Notification();
//        notification1.setId(1);
//        notification1.setMsisdn("1234567890");
//        notification1.setOperatorName("Vodafone");
//        notification1.setMessage("Test message");
//        notification1.setMsgLang("EN");
//
//        List<Notification> notificationData = Arrays.asList(notification1);
//
//        when(notificationRepoImpl.dataByStatusAndRetryCountAndOperatorNameAndChannelType(0, 0, "Vodafone", "SMS"))
//                .thenReturn(notificationData);
//        when(smsSendFactory.getSmsManagementService("Vodafone")).thenReturn(new SmsManagementService());
//
//        // Call the method being tested
//        smsService.run();
//
//        // Verify that the SMS was sent
//        verify(smsUtil).sendSms(eq("1234567890"), anyString(), eq("Test message"), anyString(), eq("EN"));
//    }
//}
//
