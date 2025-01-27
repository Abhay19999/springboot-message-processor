package io.org.reactivestax.service;

import io.org.reactivestax.domain.NotificationMessage;
import io.org.reactivestax.repository.NotificationMessageRepository;
import io.org.reactivestax.type.enums.DeliveryMethodEnum;
import io.org.reactivestax.type.exception.JMSConsumerForEnsException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@EnableJms
@Service
@Slf4j
@PropertySource("classpath:application.properties")
public class JMSReceiver {
    @Value("${jms.queue}")
    String queueName;


    @Autowired
    private final TwilioService twilioService;


    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationMessageRepository notificationMessageRepository;


    public JMSReceiver( TwilioService twilioService) {
        this.twilioService = twilioService;
    }

    @JmsListener(destination = "#{@environment.getProperty('jms.queue')}")
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage textMessage) {
                System.out.println("Received message: " + textMessage.getText());
                NotificationMessage notificationMessage = notificationMessageRepository.findByMessageId(textMessage.getText());
                if(notificationMessage.getDeliveryMethod().equals(DeliveryMethodEnum.SMS)) {
                    twilioService.sendMessageViaRestTemplate(notificationMessage.getPhoneNumber(), notificationMessage.getRawMessage());
//                    twilioService.sendMessage(notificationMessage.getPhoneNumber(), notificationMessage.getRawMessage());
                } else if (notificationMessage.getDeliveryMethod().equals(DeliveryMethodEnum.CALL)) {
                    twilioService.makeCallToClient(notificationMessage.getPhoneNumber(),notificationMessage.getRawMessage());
                }else {
                    emailService.sendEmail(notificationMessage.getEmail(),"Message from Abhay",notificationMessage.getRawMessage());
                }
            } else {
                System.out.println("Received non-text message: " + message);
            }
        } catch (Exception e) {
            throw new JMSConsumerForEnsException("Error occurred while fetching message from JMS jms.queue");
        }
    }

}
