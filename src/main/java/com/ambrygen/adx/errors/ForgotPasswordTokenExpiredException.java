package com.ambrygen.adx.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ForgotPasswordTokenExpiredException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ForgotPasswordTokenExpiredException(String message) {
        super(message);
    }

    public ForgotPasswordTokenExpiredException(String message, Throwable t) {
        super(message, t);
    }
}
