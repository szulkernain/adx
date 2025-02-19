package com.ambrygen.adx.errors;

public class UserAccountNotVerifiedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserAccountNotVerifiedException(String message) {
        super(message);
    }

    public UserAccountNotVerifiedException(String message, Throwable t) {
        super(message, t);
    }
}
