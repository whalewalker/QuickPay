package com.quickpay.data.dto;

import javax.validation.constraints.NotBlank;

public record LoginDTO(@NotBlank(message = "Account Number is required") String accountNumber,
                       @NotBlank(message = "Account Password is required") String accountPassword) {}

