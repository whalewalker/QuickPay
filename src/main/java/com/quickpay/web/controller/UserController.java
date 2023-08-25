package com.quickpay.web.controller;

import com.quickpay.data.dto.LoginDTO;
import com.quickpay.data.dto.ResponseDTO;
import com.quickpay.data.dto.UserDTO;
import com.quickpay.services.UserService;
import com.quickpay.web.response.LoginResponse;
import com.quickpay.web.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO> signup(@RequestBody @Valid UserDTO userDTO) {
        UserResponse response = userService.createUser(userDTO);
        return ResponseEntity.ok(new ResponseDTO(true, "User is successfully created", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login( @RequestBody @Valid LoginDTO loginDTO) {
        LoginResponse response = userService.login(loginDTO);
        return ResponseEntity.ok(new ResponseDTO(true, "User is successfully logged in", response ));
    }
}

