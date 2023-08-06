package com.quickpay.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotBlank(message = "Account Number is required")
    private String accountNumber;

    @NotBlank(message = "Account Password is required")
    private String accountPassword;
}
