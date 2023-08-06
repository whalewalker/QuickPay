package com.quickpay.security;


import com.quickpay.data.model.User;
import com.quickpay.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String accountNumber)  {
        return userRepository.findUserByAccountNumber(accountNumber).orElseThrow(
                () -> new UsernameNotFoundException(format("User not found with account number %s", accountNumber)));
    }
}
