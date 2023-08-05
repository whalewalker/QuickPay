package com.quickpay.web.controller;

import com.quickpay.data.dto.ResponseDTO;
import com.quickpay.data.dto.UserDTO;
import com.quickpay.services.UserService;
import com.quickpay.web.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/quick-pay/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO> signup(@RequestBody UserDTO userDTO) {
        UserResponse newUser = userService.signup(userDTO);
        return ResponseEntity.ok(new ResponseDTO(true, "User signup successful", newUser));
    }
}

