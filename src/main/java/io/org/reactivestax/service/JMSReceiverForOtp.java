package io.org.reactivestax.service;

import io.org.reactivestax.domain.Otp;
import io.org.reactivestax.repository.ClientRepository;
import io.org.reactivestax.repository.NotificationMessageRepository;
import io.org.reactivestax.repository.OTPRepository;
import io.org.reactivestax.type.enums.DeliveryMethodEnum;
import io.org.reactivestax.type.exception.JMSReceiverForOtpException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;

@EnableJms
@Configuration
@Slf4j
@PropertySource("classpath:application.properties")
@RequiredArgsConstructor
public class JMSReceiverForOtp {
    @Value("${jms.otp.queue}")
    String otpQueueName;
    @Autowired
    Environment environment;


//    @Value("${twilio.account.sid}")
//    private String accountSid;


    private TwilioService twilioService;
    private NotificationMessageRepository notificationMessageRepository;
    private OTPRepository otpRepository;
    private EmailService emailService;
    private ClientRepository clientRepository;




    @JmsListener(destination = "#{@environment.getProperty('jms.otp.queue')}")
    public void onMessage(Message message) {
        String accountSid = environment.getProperty("TWILIO_ACCOUNT_SID");

        System.out.println(message);
        System.out.println("message >>>>"+accountSid);
        try {
            if (message instanceof TextMessage textMessage) {
                System.out.println("Received message: " + textMessage.getText());
                Otp otp = otpRepository.findByOtpId(textMessage.getText());
                DeliveryMethodEnum contactMethod = otp.getDeliveryMethod();
                if(contactMethod.equals(DeliveryMethodEnum.SMS)) {
                    twilioService.sendMessage(otp.getMobileNumber(),String.valueOf(otp.getOtpNumber()));
                } else if (contactMethod.equals(DeliveryMethodEnum.CALL)) {
                    twilioService.makeCallToClient(otp.getMobileNumber(),String.valueOf(otp.getOtpNumber()));
                }else{
                    emailService.sendEmail(otp.getEmail(),"Your OTP ", String.valueOf(otp.getOtpNumber()));
                }
            } else {
                System.out.println("Received non-text message: " + message);
            }
        } catch (Exception e) {
            throw new JMSReceiverForOtpException("Error occurred while fetching message from JMS otp queue");
        }
    }
}
