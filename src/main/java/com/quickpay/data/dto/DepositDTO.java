package com.quickpay.data.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DepositDTO {
    @NotNull
    @Min(value = 1, message = "Deposit amount cannot be less than 1.00")
    private BigDecimal amount;
}
