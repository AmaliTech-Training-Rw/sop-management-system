package com.team.sop_management_service.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    // Handle general Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        // Log the error details here
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }

    // Handle InvalidPipelineException
    @ExceptionHandler(InvalidSOPException.class)
    public ResponseEntity<String> handleInvalidPipelineException(InvalidSOPException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    // Handle SOPInitiationException
    @ExceptionHandler(SOPNotFoundException.class)
    public ResponseEntity<String> handleSOPInitiationException(SOPNotFoundException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    // Handle SOPRetrievalException
    @ExceptionHandler(SOPRetrievalException.class)
    public ResponseEntity<String> handleSOPRetrievalException(SOPRetrievalException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
