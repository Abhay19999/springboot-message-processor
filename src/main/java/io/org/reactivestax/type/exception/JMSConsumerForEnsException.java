package io.org.reactivestax.type.exception;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JMSConsumerForEnsException extends RuntimeException {
    public JMSConsumerForEnsException(String s) {
        log.info(s);
    }
}
