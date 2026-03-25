package com.demon_invasion.backend.model.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class DtoApiError {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public DtoApiError(HttpStatus status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status.value();
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
