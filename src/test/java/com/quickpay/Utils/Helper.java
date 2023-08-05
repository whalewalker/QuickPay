package com.quickpay.Utils;

import com.quickpay.data.dto.UserDTO;
import com.quickpay.data.model.Account;
import com.quickpay.data.model.Role;
import com.quickpay.data.model.User;
import com.quickpay.web.response.UserResponse;

import java.util.List;

public class Helper {
    public static Account createAccount(){
        return Account.builder()
                .accountNumber("0123456789")
                .balance(0.0)
                .build();
    }

    public static User createUser(UserDTO userDTO) {
        User user = new User();
        user.setId(1L);
        user.setName(userDTO.name());
        user.setEmail(userDTO.email());
        user.setPassword(userDTO.password());
        user.setBio(userDTO.bio());
        user.setRoles(List.of(new Role("ROLE_USER")));
        user.setAccount(createAccount());

        return user;
    }

    public static UserResponse createUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setBio(user.getBio());
        userResponse.setAccountNumber(user.getAccount().getAccountNumber());
        userResponse.setBalance(user.getAccount().getBalance());
        return userResponse;
    }

}
