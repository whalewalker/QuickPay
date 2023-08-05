package com.quickpay.services;

import com.quickpay.data.dto.UserDTO;
import com.quickpay.data.model.Account;
import com.quickpay.data.model.User;
import com.quickpay.data.repository.AccountRepository;
import com.quickpay.data.repository.UserRepository;
import com.quickpay.web.exception.BadRequestException;
import com.quickpay.web.response.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.quickpay.Utils.Helper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testSignup_SuccessfulSignup() {
        UserDTO userDTO = new UserDTO("John Doe", "john.doe@example.com", "password123", "A user bio");

        User user = createUser(userDTO);

        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(mapper.map(userDTO, User.class)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("Encrypted password");
        when(accountRepository.save(any(Account.class))).thenReturn(createAccount());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mapper.map(user, UserResponse.class)).thenReturn(createUserResponse(user));

        UserResponse newUser = userService.signup(userDTO);

        assertNotNull(newUser);
        assertEquals(userDTO.getName(), newUser.getName());
        assertEquals(userDTO.getEmail(), newUser.getEmail());
        assertEquals(userDTO.getBio(), newUser.getBio());
        assertEquals(0.0, newUser.getBalance());
        assertEquals(10, newUser.getAccountNumber().length());
    }

    @Test
    void testSignup_EmailAlreadyRegistered() {
        UserDTO userDTO = new UserDTO("John Doe", "john.doe@example.com", "password123", "A user bio");

        User existingUser = new User();
        existingUser.setName(userDTO.getName());
        existingUser.setEmail(userDTO.getEmail());

        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(existingUser));

        assertThrows(BadRequestException.class, () -> userService.signup(userDTO));
    }
}