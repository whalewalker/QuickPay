package com.quickpay.web.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserResponse {
    private String name;
    private String email;
    private String bio;
    private String accountNumber;
    private BigDecimal balance;
}
