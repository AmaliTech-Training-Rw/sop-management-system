package com.team.sop_management_service.error;

public class InvalidSOPException extends RuntimeException {
    public InvalidSOPException(String message) {
        super(message);
    }
}