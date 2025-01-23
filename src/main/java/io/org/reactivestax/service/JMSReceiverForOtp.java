package io.org.reactivestax.service;

import io.org.reactivestax.domain.Client;
import io.org.reactivestax.domain.Otp;
import io.org.reactivestax.repository.ClientRepository;
import io.org.reactivestax.repository.ContactRepository;
import io.org.reactivestax.repository.NotificationMessageRepository;
import io.org.reactivestax.repository.OTPRepository;
import io.org.reactivestax.type.DeliveryMethodEnum;
import io.org.reactivestax.type.exception.JMSReceiverForOtpException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;

@EnableJms
@Configuration
@Slf4j
@PropertySource("classpath:application.properties")
public class JMSReceiverForOtp {
    @Value("${jms.otp.queue}")
    String otpQueueName;

    @Autowired
    private final TwilioService twilioService;

    @Autowired
    private NotificationMessageRepository notificationMessageRepository;

    @Autowired
    private OTPRepository otpRepository;


    @Autowired
    private ClientRepository clientRepository;


    public JMSReceiverForOtp(TwilioService twilioService) {
        this.twilioService = twilioService;
    }

    @JmsListener(destination = "#{@environment.getProperty('jms.otp.queue')}")
    public void onMessage(Message message) {
        System.out.println(message);
        try {
            if (message instanceof TextMessage textMessage) {
                System.out.println("Received message: " + textMessage.getText());
                Otp otp = otpRepository.findByOtpId(textMessage.getText());
                Client client = clientRepository.findById(otp.getClientId());
                String contactMethod = client.getPreferredContactMethod();
                if(contactMethod.equals("sms")) {
                    twilioService.sendMessage(otp.getMobileNumber(),String.valueOf(otp.getOtpNumber()));
                } else if (contactMethod.equals("call")) {
                    twilioService.makeCallToClient(otp.getMobileNumber(),String.valueOf(otp.getOtpNumber()));
                }
            } else {
                System.out.println("Received non-text message: " + message);
            }
        } catch (Exception e) {
            throw new JMSReceiverForOtpException("Error occurred while fetching message from JMS otp queue");
        }
    }
}
