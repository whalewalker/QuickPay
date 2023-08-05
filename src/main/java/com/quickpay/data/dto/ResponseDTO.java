package com.quickpay.data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseDTO(boolean isSuccessful,
                          String message,
                          @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime timeStamp,
                          Object data) {
    public ResponseDTO(boolean isSuccessful, String message) {
        this(isSuccessful, message, LocalDateTime.now(), null);
    }

    public ResponseDTO(boolean isSuccessful, String message, Object data) {
        this(isSuccessful, message, LocalDateTime.now(), data);
    }
}
