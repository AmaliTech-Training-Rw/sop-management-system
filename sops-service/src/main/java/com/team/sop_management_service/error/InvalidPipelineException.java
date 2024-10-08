package com.team.sop_management_service.error;

public class InvalidPipelineException extends RuntimeException {
    public InvalidPipelineException(String message) {
        super(message);
    }
}