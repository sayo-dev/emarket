package org.example.e_market.exceptions;

public class CustomNotFoundException extends RuntimeException {
    public CustomNotFoundException(String message) {
        super(message);
    }
}
