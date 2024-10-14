package com.team.sop_management_service.exceptions;

// Custom exceptions for handling specific SOP-related errors

public class SOPNotFoundException extends RuntimeException {
    public SOPNotFoundException(String message) {
        super(message);
    }
}


