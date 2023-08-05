package com.quickpay.data.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
public class ErrorDTO {
    private boolean isSuccessful;
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private List<ValidationDTO> errors;
    private String stackTrace;
    private Object data;

    public ErrorDTO(int status, String message) {
        this.isSuccessful = false;
        this.status = status;
        this.message = message;
        timestamp = LocalDateTime.now();
    }

    public void addValidationError(String field, String message) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(new ValidationDTO(field, message));
    }
}

record ValidationDTO(String field, String message) {}
