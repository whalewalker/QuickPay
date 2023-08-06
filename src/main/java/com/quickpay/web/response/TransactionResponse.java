package com.quickpay.web.response;
import java.math.BigDecimal;

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
