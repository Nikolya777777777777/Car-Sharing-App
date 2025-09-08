package com.example.carsharingapp.exception;

public class StripeSessionFailureException extends RuntimeException {
    public StripeSessionFailureException(String msg) {
        super(msg);
    }
}
