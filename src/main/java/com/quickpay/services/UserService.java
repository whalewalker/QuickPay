package com.quickpay.services;

import com.quickpay.data.dto.LoginDTO;
import com.quickpay.data.dto.UserDTO;
import com.quickpay.web.exception.BadRequestException;
import com.quickpay.web.response.LoginResponse;
import com.quickpay.web.response.UserResponse;

public interface UserService {
    UserResponse createUser(UserDTO userDTO) throws BadRequestException;

    LoginResponse login(LoginDTO loginDTO);
}
