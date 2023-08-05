package com.quickpay.services;

import com.quickpay.data.dto.UserDTO;
import com.quickpay.web.exception.BadRequestException;
import com.quickpay.web.response.UserResponse;

public interface UserService {
    UserResponse signup(UserDTO userDTO) throws BadRequestException;
}
