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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
    public void sendMessageViaRestTemplate(String recipientNumber, String messageBody){
        String accountSid = environment.getProperty("TWILIO_ACCOUNT_SID");
        String authToken = "cc9dab1926d066b92efd6bb19a73c143";
        String url = "https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String auth = accountSid + ":" + authToken;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        httpHeaders.set("Authorization", "Basic " + encodedAuth);

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("To", recipientNumber);
        body.add("From", twilioPhoneNumber);
        body.add("Body", messageBody);

        HttpEntity<LinkedMultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, httpHeaders);

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                System.out.println("Sms sent successfully. Response: " + responseEntity.getBody());
            } else {
                System.out.println("Failed to send Sms, Response: " + responseEntity.getBody());
            }
        } catch (Exception e) {
            System.out.println("Error occurred while sending Sms: " + e.getMessage());
        }

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
