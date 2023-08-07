package com.quickpay.data.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {
    @NotBlank(message = "Account Number is required")
    private String beneficiaryAccountNumber;

    @NotNull(message = "Amount cannot be null")
    @Min(value = 1, message = "Deposit amount cannot be less than 1.00")
    private BigDecimal amount;

    private String narration;
}