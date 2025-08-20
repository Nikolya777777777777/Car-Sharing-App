package com.example.carsharingapp.exception;

public class SpecificationProviderManagerException extends RuntimeException {
    public SpecificationProviderManagerException(String message) {
        super(message);
    }

    public SpecificationProviderManagerException(String message, RuntimeException exception) {
        super(message, exception);
    }
}
