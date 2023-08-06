package com.quickpay.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickpay.data.dto.DepositDTO;
import com.quickpay.data.dto.ResponseDTO;
import com.quickpay.data.model.Account;
import com.quickpay.data.model.Transaction;
import com.quickpay.data.model.TransactionType;
import com.quickpay.security.CustomUserDetailService;
import com.quickpay.security.JwtTokenProvider;
import com.quickpay.services.AccountService;
import com.quickpay.web.response.TransactionResponse;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;

import static com.quickpay.Utils.Helper.createDepositResponse;
import static com.quickpay.utils.Utils.generateTransactionDescription;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(AccountController.class)
@WithMockUser
@TestPropertySource(locations = "classpath:application.properties")
class AccountControllerTest {

    @MockBean
    private AccountService accountService;

    @MockBean
    private CustomUserDetailService customUserDetailService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    void testDeposit() throws Exception {
        String accountNumber = "0123456789";
        BigDecimal depositAmount = new BigDecimal("500.00");
        BigDecimal oldAccountBalance = new BigDecimal("1000.00");
        BigDecimal newAccountBalance = oldAccountBalance.add(depositAmount);

        DepositDTO depositDTO = new DepositDTO(depositAmount);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(accountNumber);

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(oldAccountBalance);

        Transaction savedTransaction = new Transaction();
        savedTransaction.setTransactionType(TransactionType.DEPOSIT.name());
        savedTransaction.setNarration(generateTransactionDescription(depositAmount));
        savedTransaction.setAmount(depositAmount);
        savedTransaction.setAccountBalance(newAccountBalance);

        TransactionResponse mockTransactionResponse = createDepositResponse(savedTransaction);

        when(accountService.deposit(depositDTO, principal))
                .thenReturn(mockTransactionResponse);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/quick-pay/account/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositDTO)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        ResponseDTO responseDTO = objectMapper.readValue(responseContent, ResponseDTO.class);

        assertTrue(responseDTO.isSuccessful());
        assertEquals("Successful", responseDTO.message());

        TransactionResponse transactionResponse = objectMapper.readValue(objectMapper.writeValueAsString(responseDTO.data()), TransactionResponse.class);
        assertEquals(accountNumber, transactionResponse.accountNumber());
        assertEquals(TransactionType.DEPOSIT.toString(), transactionResponse.transactionType());
        assertEquals(generateTransactionDescription(depositAmount), transactionResponse.description());
        assertEquals(depositAmount, transactionResponse.amount());
        assertEquals(newAccountBalance, transactionResponse.newAccountBalance());
        assertEquals(LocalDateTime.now().toString(), transactionResponse.transactionDate());
    }



}