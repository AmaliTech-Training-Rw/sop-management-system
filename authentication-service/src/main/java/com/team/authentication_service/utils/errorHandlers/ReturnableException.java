package com.team.authentication_service.utils.errorHandlers;

import lombok.Getter;
import org.apache.kafka.shaded.io.opentelemetry.proto.trace.v1.Status;
import org.springframework.http.HttpStatusCode;

@Getter
public class ReturnableException extends Exception {
    public HttpStatusCode statusCode;

    public ReturnableException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
