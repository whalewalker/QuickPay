package com.quickpay.web.response;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionResponse(
                                  String transactionType,
                                  String description,
                                  BigDecimal amount,
                                  BigDecimal accountBalance,
                                  String transactionDate,
                                  String to) {

    public TransactionResponse(String transactionType, String description,
                               BigDecimal amount, BigDecimal accountBalance,
                               String transactionDate) {
        this( transactionType, description, amount, accountBalance, transactionDate, null);
    }
}
