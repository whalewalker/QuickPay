package com.quickpay.services;

import com.quickpay.data.dto.UserDTO;
import com.quickpay.data.model.Account;
import com.quickpay.data.model.Role;
import com.quickpay.data.model.User;
import com.quickpay.data.repository.AccountRepository;
import com.quickpay.data.repository.UserRepository;
import com.quickpay.web.exception.BadRequestException;
import com.quickpay.web.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper mapper;

    @Override
    public UserResponse signup(UserDTO userDTO) throws BadRequestException {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new BadRequestException("Email is already registered");
        }
        Account account = createAccount();
        UserResponse userResponse = mapper.map( createUser(userDTO, account), UserResponse.class);
        userResponse.setBalance(account.getBalance());
        userResponse.setAccountNumber(account.getAccountNumber());
        return userResponse;
    }

    private  User createUser(UserDTO userDTO, Account account) {
        User user = mapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRoles(List.of(new Role("ROLE_USER")));
        user.setAccount(account);
       return userRepository.save(user);
    }

    private Account createAccount() {
        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .balance(0.0)
                .build();
        return accountRepository.save(account);
    }

    private String generateAccountNumber() {
        StringBuilder accountNumberBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int digit = ThreadLocalRandom.current().nextInt(10);
            accountNumberBuilder.append(digit);
        }
        return accountNumberBuilder.toString();
    }
}
