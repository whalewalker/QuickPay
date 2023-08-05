package com.quickpay.data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO {
    private boolean isSuccessful;
    private String message;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timeStamp;
    private Object data;

    public ResponseDTO(boolean isSuccessful, String message) {
        this.isSuccessful = isSuccessful;
        this.message = message;
        timeStamp = LocalDateTime.now();
    }


    public ResponseDTO(boolean isSuccessful, String message, Object data) {
        this(isSuccessful, message);
        this.data = data;
    }
}
