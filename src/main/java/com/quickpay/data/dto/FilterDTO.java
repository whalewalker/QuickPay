package com.quickpay.data.dto;

import java.time.LocalDateTime;

public record FilterDTO(
        String transactionType,
        String transactionId,
        String accountNumber,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer page,
        Integer size,
        Integer draw,
        int start,
        int length,
        Integer startPage
) {}

