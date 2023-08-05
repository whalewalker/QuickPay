package com.quickpay.services;

import com.quickpay.data.dto.LoginDTO;
import com.quickpay.data.dto.UserDTO;
import com.quickpay.data.model.Account;
import com.quickpay.data.model.User;
import com.quickpay.data.repository.AccountRepository;
import com.quickpay.data.repository.UserRepository;
import com.quickpay.security.CustomUserDetailService;
import com.quickpay.security.JwtTokenProvider;
import com.quickpay.web.exception.BadRequestException;
import com.quickpay.web.response.LoginResponse;
import com.quickpay.web.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.quickpay.Utils.Helper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private  JwtTokenProvider tokenProvider;

    @Mock
    private CustomUserDetailService customUserDetailService;

    @Mock
    private  AuthenticationManager authenticationManager;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user = null;
    private UserDTO userDTO = null;

    @BeforeEach
    void setUp() {
         userDTO = new UserDTO("John Doe", "john.doe@example.com", "password123", "A user bio");
        user = createUser(userDTO);
    }

    @Test
    void testSignup_SuccessfulSignup() {
        when(userRepository.findByEmail(userDTO.email())).thenReturn(Optional.empty());
        when(mapper.map(userDTO, User.class)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("Encrypted password");
        when(accountRepository.save(any(Account.class))).thenReturn(createAccount());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mapper.map(user, UserResponse.class)).thenReturn(createUserResponse(user));

        UserResponse newUser = userService.createUser(userDTO);

        assertNotNull(newUser);
        assertEquals(userDTO.name(), newUser.getName());
        assertEquals(userDTO.email(), newUser.getEmail());
        assertEquals(userDTO.bio(), newUser.getBio());
        assertEquals(0.0, newUser.getBalance());
        assertEquals(10, newUser.getAccountNumber().length());
    }

    @Test
    void testSignup_EmailAlreadyRegistered() {
        when(userRepository.findByEmail(userDTO.email())).thenReturn(Optional.of(user));
        assertThrows(BadRequestException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void testLogin_SuccessfulLogin()  {
        LoginDTO loginDTO = new LoginDTO("0123456789", "password123");

        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(loginDTO.accountNumber(),
                loginDTO.accountPassword());
        testingAuthenticationToken.setAuthenticated(true);
        testingAuthenticationToken.setDetails(loginDTO);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.accountNumber(), loginDTO.accountPassword())
        )).thenReturn(testingAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);

        when(tokenProvider.generateToken(anyString())).thenReturn("generated_token");

        LoginResponse loginResponse = userService.login(loginDTO);
        verify(tokenProvider, times(1)).generateToken(loginDTO.accountNumber());

        assertNotNull(loginResponse);
        assertEquals("generated_token", loginResponse.accessToken());
    }

}