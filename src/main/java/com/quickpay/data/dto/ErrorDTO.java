package com.quickpay.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.quickpay.utils.Utils.formatDateTime;

@Data
public class ErrorDTO {
    @JsonProperty("isSuccessful")
    private boolean isSuccessful;
    private int status;
    private String message;
    private String timestamp;
    private List<ValidationDTO> errors;

    public ErrorDTO(int status, String message) {
        this.isSuccessful = false;
        this.status = status;
        this.message = message;
        timestamp = formatDateTime(LocalDateTime.now());
    }

    public void addValidationError(String field, String message) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(new ValidationDTO(field, message));
    }
}

record ValidationDTO(String field, String message) {}
