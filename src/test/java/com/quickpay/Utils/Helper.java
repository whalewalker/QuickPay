package com.quickpay.Utils;

import com.quickpay.data.dto.UserDTO;
import com.quickpay.data.model.*;
import com.quickpay.web.response.TransactionResponse;
import com.quickpay.web.response.UserResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.quickpay.utils.Utils.formatDateTime;
import static com.quickpay.utils.Utils.generateTransactionDescription;

public class Helper {
    public static Account createAccount() {
        return new Account("0123456789", BigDecimal.ZERO);
    }

    public static User createUser(UserDTO userDTO) {
        User user = new User();
        user.setId(1L);
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setBio(userDTO.getBio());
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

    public static TransactionResponse createTransactionResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getTransactionType(),
                generateTransactionDescription(transaction.getAmount()),
                transaction.getAmount(),
                transaction.getAccountBalance().add(BigDecimal.valueOf(1000)),
                formatDateTime(LocalDateTime.now()));
    }

    public static Transaction createTransaction(String transactionType){
        BigDecimal amount = new BigDecimal("500");
        Transaction savedTransaction = new Transaction();
        savedTransaction.setTransactionType(transactionType);
        savedTransaction.setNarration(generateTransactionDescription(amount));
        savedTransaction.setAmount(amount);
        savedTransaction.setAccountBalance(amount);
        return savedTransaction;
    }

}
