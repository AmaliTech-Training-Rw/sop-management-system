package com.team.sop_management_service.error;

// Custom exceptions for handling specific SOP-related errors

public class SOPInitiationException extends RuntimeException {
    public SOPInitiationException(String message, Throwable cause) {
        super(message, cause);
    }
}


