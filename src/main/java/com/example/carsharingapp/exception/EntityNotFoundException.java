package com.example.carsharingapp.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String msg) {
        super(msg);
    }

    public EntityNotFoundException(Exception e, String msg) {
        super(msg, e);
    }
}
