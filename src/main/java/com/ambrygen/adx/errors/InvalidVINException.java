package com.ambrygen.adx.errors;

public class InvalidVINException extends RuntimeException {
    public InvalidVINException() {
        super();
    }

    public InvalidVINException(String message) {
        super(message);
    }
}
