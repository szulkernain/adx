package com.ambrygen.adx.errors;

public class InvalidFileTypeException extends RuntimeException {
    public InvalidFileTypeException() {
        super();
    }

    public InvalidFileTypeException(String message) {
        super(message);
    }
}
