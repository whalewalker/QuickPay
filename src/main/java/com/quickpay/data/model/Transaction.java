package com.quickpay.data.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Getter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate transactionDate;
    private TransactionType transactionType;
    private String narration;
    private Double amount;
    private Double accountBalance;
}
