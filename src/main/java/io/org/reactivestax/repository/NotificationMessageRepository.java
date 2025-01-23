package io.org.reactivestax.repository;

import com.twilio.rest.api.v2010.account.Message;
import io.org.reactivestax.domain.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationMessageRepository extends JpaRepository<NotificationMessage,Long> {
    NotificationMessage findByMessageId(String messageId);
}
