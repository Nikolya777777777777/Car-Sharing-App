package com.example.carsharingapp.exception;

public class StripeSessionFailureException extends RuntimeException {
    public StripeSessionFailureException(String msg) {
        super(msg);
    }

    public StripeSessionFailureException(Exception e, String msg) {
        super(msg, e);
    }
}
