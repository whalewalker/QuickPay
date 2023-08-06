package com.quickpay.data.dto;

import java.math.BigDecimal;

public record TransferDTO(String accountNumber, BigDecimal amount, String narration) { }
