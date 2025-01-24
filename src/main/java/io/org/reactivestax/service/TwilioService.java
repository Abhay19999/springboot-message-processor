package io.org.reactivestax.service;


import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.twilio.type.Twiml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.net.URISyntaxException;
//@PropertySource("classpath:application.properties")
@Service
public class TwilioService {

    @Autowired
    Environment environment;



//    @Value("${TWILIO_ACCOUNT_SID}")
//    private String accountSid;
//    @Value("${twilio.auth.token}")
//    private String token;
    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    public void sendMessage(String recipientNumber, String messageBody){
        String accountSid = environment.getProperty("TWILIO_ACCOUNT_SID");
        String token = environment.getProperty("TWILIO_AUTH_TOKEN");
        Twilio.init(accountSid,token);
        Message.creator(
                new com.twilio.type.PhoneNumber(recipientNumber),
                new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                messageBody
        ).create();
    }
    public void makeCallToClient(String recipientNumber,String messageBody)  {
        String twiml = "<Response><Say>"+messageBody+"</Say></Response>";
        String accountSid = environment.getProperty("TWILIO_ACCOUNT_SID");
        String token = environment.getProperty("TWILIO_AUTH_TOKEN");

        Twilio.init(accountSid,token);

      Call.creator(new PhoneNumber(recipientNumber), new PhoneNumber(twilioPhoneNumber),
                new Twiml(twiml)).create();


    }
}
