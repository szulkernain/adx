package com.ambrygen.adx.events;

import com.ambrygen.adx.models.security.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserAccountVerifiedEvent extends ApplicationEvent {
    private User user;

    UserAccountVerifiedEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
