package com.quickpay.web.response;
import java.math.BigDecimal;

public record TransactionResponse(String accountNumber,
                                  String transactionType,
                                  String description,
                                  BigDecimal amount,
                                  BigDecimal newAccountBalance,
                                  String transactionDate,
                                  String sourceAccountNumber,
                                  String destinationAccountNumber) {

    public TransactionResponse(String accountNumber, String transactionType, String description,
                               BigDecimal amount, BigDecimal newAccountBalance,
                               String transactionDate) {
        this(accountNumber, transactionType, description, amount, newAccountBalance, transactionDate,
                null, null);
    }
}
