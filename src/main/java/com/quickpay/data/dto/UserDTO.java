package com.quickpay.data.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
public record UserDTO(@NotBlank(message = "Name cannot be blank") String name,
                      @NotBlank(message = "Email cannot be blank") @Email(message = "Email must be valid", regexp = "^^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)(\\.[A-Za-z]{2,})$") String email,
                      @NotBlank(message = "Password cannot be blank") String password,
                      String bio) {}
