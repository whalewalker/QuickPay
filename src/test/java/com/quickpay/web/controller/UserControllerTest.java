package com.quickpay.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickpay.data.dto.ResponseDTO;
import com.quickpay.data.dto.UserDTO;
import com.quickpay.data.model.User;
import com.quickpay.security.CustomUserDetailService;
import com.quickpay.security.JwtTokenProvider;
import com.quickpay.services.UserService;
import com.quickpay.web.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static com.quickpay.Utils.Helper.createUser;
import static com.quickpay.Utils.Helper.createUserResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
@WithMockUser
@TestPropertySource(locations = "classpath:application.yml")
class UserControllerTest {
    @MockBean
    private UserService userService;

    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailService customUserDetailService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
     void testSignup_SuccessfulSignup() throws Exception {
        UserDTO userDTO = new UserDTO("John Doe", "john.doe@example.com", "password123", "A user bio");

        User newUser = createUser(userDTO);

        when(userService.createUser(any(UserDTO.class))).thenReturn(createUserResponse(newUser));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/quick-pay/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))// Convert userDTO to JSON
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        ResponseDTO responseDTO = objectMapper.readValue(responseContent, ResponseDTO.class);

        assertTrue(responseDTO.isSuccessful());
        assertEquals("User is successfully created", responseDTO.message());

        UserResponse userResponse = objectMapper.readValue(objectMapper.writeValueAsString(responseDTO.data()), UserResponse.class);
        assertEquals(newUser.getName(), userResponse.getName());
        assertEquals(newUser.getEmail(), userResponse.getEmail());
        assertEquals(newUser.getBio(), userResponse.getBio());
        assertEquals(BigDecimal.ZERO, userResponse.getBalance());
        assertEquals(10, userResponse.getAccountNumber().length());
    }
}