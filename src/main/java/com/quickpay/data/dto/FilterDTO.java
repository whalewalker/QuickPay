package com.quickpay.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterDTO {
    private String transactionType;
    private String transactionId;
    private String accountNumber;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer page;
    private Integer size;
    private Integer draw;
    private int start;
    private int length;
    private Integer startPage;
}

