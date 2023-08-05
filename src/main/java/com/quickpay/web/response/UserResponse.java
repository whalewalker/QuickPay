package com.quickpay.web.response;

import lombok.Data;

@Data
public class UserResponse {
    private String name;
    private String email;
    private String bio;
    private String accountNumber;
    private double balance;
}
