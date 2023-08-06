package com.quickpay.services;

import com.quickpay.data.dto.LoginDTO;
import com.quickpay.data.dto.UserDTO;
import com.quickpay.data.model.Account;
import com.quickpay.data.model.Role;
import com.quickpay.data.model.User;
import com.quickpay.data.repository.AccountRepository;
import com.quickpay.data.repository.RoleRepository;
import com.quickpay.data.repository.UserRepository;
import com.quickpay.security.JwtTokenProvider;
import com.quickpay.web.exception.BadRequestException;
import com.quickpay.web.response.LoginResponse;
import com.quickpay.web.response.UserResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.quickpay.utils.Utils.generateRandomValue;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper mapper;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserResponse createUser(UserDTO userDTO) throws BadRequestException {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new BadRequestException("Email is already registered");
        }
        Account account = createAccount();
        UserResponse userResponse = mapper.map(createUser(userDTO, account), UserResponse.class);
        userResponse.setBalance(account.getBalance());
        userResponse.setAccountNumber(account.getAccountNumber());
        return userResponse;
    }

    @Override
    public LoginResponse login(LoginDTO loginDTO) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getAccountNumber(),
                        loginDTO.getAccountPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = tokenProvider.generateToken(loginDTO.getAccountNumber());
        return new LoginResponse(token);
    }

    private User createUser(UserDTO userDTO, Account account) {
        User user = mapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        Role role = roleRepository.findByName("ROLE_USER");
        user.setRoles(List.of(role));
        user.setAccount(account);
        return userRepository.save(user);
    }

    private Account createAccount() {
        Account account = new Account();
        account.setAccountNumber(generateRandomValue("", 10));
        account.setBalance(BigDecimal.ZERO);
        return accountRepository.save(account);
    }

    @PostConstruct
    void setupUserRole(){
        Role role = new Role("ROLE_USER");
        roleRepository.save(role);
    }
}
