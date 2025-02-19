package com.ambrygen.adx.events;

import com.ambrygen.adx.models.security.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {
    private final ApplicationEventPublisher publisher;

    EventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishUserAccountVerifiedEvent(final User user) {
        // Publishing event created by extending ApplicationEvent
        publisher.publishEvent(new UserAccountVerifiedEvent(this, user));
    }
}
