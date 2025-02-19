package com.ambrygen.adx.errors;

public class TokenRefreshException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TokenRefreshException(String message) {
        super(message);
    }

    public TokenRefreshException(String message, Throwable t) {
        super(message, t);
    }
}
