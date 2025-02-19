package com.ambrygen.adx.events;

import com.ambrygen.adx.models.security.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAccountVerifiedListener {


    @Async
    @EventListener
    void handleReturnedEvent(UserAccountVerifiedEvent event) {
        User user = event.getUser();
        //Do something
    }
}
