package com.quickpay.data.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public record DepositDTO(@NotNull
                         @Min(value = 1, message = "Deposit amount cannot be less that 1.00")
                         BigDecimal amount) {
}
