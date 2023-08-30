package com.quickpay.data.dto;

import com.quickpay.data.model.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder(toBuilder = true)
@Data
public class TransactionDTO {
    private String responseCode;
    private String responseMessage;
    private TransactionType transactionType;
    private String narration;
    private BigDecimal amount;
    private BigDecimal accountBalance;
    private String accountNumber;
    private String bankName;
    private String bankCode;
}
