package com.quickpay.data.dto;

import javax.validation.constraints.NotBlank;
public record UserDTO(@NotBlank(message = "Name cannot be blank") String name,
                      @NotBlank(message = "Email cannot be blank") String email,
                      @NotBlank(message = "Password cannot be blank") String password,
                      String bio) {}
