package io.org.reactivestax.type.exception;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JMSReceiverForOtpException extends RuntimeException {
    public JMSReceiverForOtpException(String s) {
        log.error(s);
    }

}
