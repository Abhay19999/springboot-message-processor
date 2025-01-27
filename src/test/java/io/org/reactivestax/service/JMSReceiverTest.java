package io.org.reactivestax.service;

import io.org.reactivestax.domain.NotificationMessage;
import io.org.reactivestax.repository.NotificationMessageRepository;
import io.org.reactivestax.type.enums.DeliveryMethodEnum;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.*;


@SpringBootTest(properties = "jms.listener.enabled=false")
class JMSReceiverTest {

    @MockitoBean
    private TwilioService twilioService;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private NotificationMessageRepository notificationMessageRepository;


    @Autowired
    private JMSReceiver jmsReceiver;

    @Test
    void testOnMessage_SMSDelivery() throws Exception {
        String messageId = "123";
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setMessageId(messageId);
        notificationMessage.setDeliveryMethod(DeliveryMethodEnum.SMS);
        notificationMessage.setPhoneNumber("1234567890");
        notificationMessage.setRawMessage("Test SMS Message");

        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(messageId);
        when(notificationMessageRepository.findByMessageId(messageId)).thenReturn(notificationMessage);
        jmsReceiver.onMessage(textMessage);
        verify(twilioService, times(1)).sendMessage("1234567890", "Test SMS Message");
        verifyNoInteractions(emailService);
        verify(twilioService,times(0)).makeCallToClient(anyString(),anyString());
    }
    @Test
    void testOnMessage_EmailDelivery() throws JMSException {
        String messageId = "123";
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setMessageId(messageId);
        notificationMessage.setDeliveryMethod(DeliveryMethodEnum.EMAIL);
        notificationMessage.setPhoneNumber("1234567890");
        notificationMessage.setEmail("abhaynimavat2410@gmail.com");
        notificationMessage.setRawMessage("Test SMS Message");

        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(messageId);
        when(notificationMessageRepository.findByMessageId(messageId)).thenReturn(notificationMessage);
        jmsReceiver.onMessage(textMessage);
        verify(twilioService, times(0)).sendMessage("1234567890", "Test SMS Message");
        verify(emailService,times(1)).sendEmail(notificationMessage.getEmail(),"Message from Abhay",notificationMessage.getRawMessage());
        verify(twilioService,times(0)).makeCallToClient(anyString(),anyString());
    }

    @Test
    void testOnMessage_CallDelivery() throws JMSException {
        String messageId = "123";
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setMessageId(messageId);
        notificationMessage.setDeliveryMethod(DeliveryMethodEnum.CALL);
        notificationMessage.setPhoneNumber("1234567890");
        notificationMessage.setRawMessage("Test SMS Message");

        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(messageId);
        when(notificationMessageRepository.findByMessageId(messageId)).thenReturn(notificationMessage);
        jmsReceiver.onMessage(textMessage);
        verify(twilioService, times(0)).sendMessage("1234567890", "Test SMS Message");
        verifyNoInteractions(emailService);
        verify(twilioService,times(1)).makeCallToClient(notificationMessage.getPhoneNumber(),notificationMessage.getRawMessage());
    }
    @Test
    void testOnMessage_WhenNoMessageReceived(){

    }

}