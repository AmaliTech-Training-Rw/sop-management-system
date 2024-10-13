package com.team.messaging_service.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class ErrorResponse {
    private int status;
    private String message;
    private Map<String, String> errors;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
    public ErrorResponse(int status, String message, Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }
}