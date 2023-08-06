package com.quickpay.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {
    @NotBlank(message = "Account Number is required")
    private String accountNumber;

    @NotNull(message = "Amount cannot be null")
    @Min(value = 1, message = "Deposit amount cannot be less than 1.00")
    private BigDecimal amount;

    private String narration;
}