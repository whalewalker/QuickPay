package com.quickpay.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickpay.data.dto.DepositDTO;
import com.quickpay.data.dto.ResponseDTO;
import com.quickpay.data.dto.TransferDTO;
import com.quickpay.data.model.Account;
import com.quickpay.data.model.Transaction;
import com.quickpay.data.model.TransactionType;
import com.quickpay.data.model.User;
import com.quickpay.security.CustomUserDetailService;
import com.quickpay.security.JwtTokenProvider;
import com.quickpay.services.AccountService;
import com.quickpay.web.exception.AccountException;
import com.quickpay.web.response.TransactionResponse;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.quickpay.Utils.Helper.createTransaction;
import static com.quickpay.Utils.Helper.createTransactionResponse;
import static com.quickpay.utils.Utils.formatDateTime;
import static com.quickpay.utils.Utils.generateTransactionDescription;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private static final String BASE_URL = "/api/v1/quick-pay/account";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    void testDeposit() throws Exception {
        String accountNumber = "0123456789";
        BigDecimal depositAmount = new BigDecimal("500");
        BigDecimal oldAccountBalance = new BigDecimal("1000");
        BigDecimal newAccountBalance = oldAccountBalance.add(depositAmount);

        DepositDTO depositDTO = new DepositDTO(depositAmount);
        Principal principal = () -> accountNumber;

        Transaction savedTransaction = createTransaction(TransactionType.DEPOSIT.name());
        TransactionResponse mockTransactionResponse = createTransactionResponse(savedTransaction);

        when(accountService.deposit(any(DepositDTO.class), any(Principal.class)))
                .thenReturn(mockTransactionResponse);

        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(depositDTO))
                        .principal(principal))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        ResponseDTO responseDTO = objectMapper.readValue(responseContent, ResponseDTO.class);

        assertTrue(responseDTO.isSuccessful());
        assertEquals("Successful", responseDTO.message());

        TransactionResponse transactionResponse = objectMapper.readValue(objectMapper.writeValueAsString(responseDTO.data()), TransactionResponse.class);
        assertEquals(TransactionType.DEPOSIT.toString(), transactionResponse.transactionType());
        assertEquals(generateTransactionDescription(depositAmount), transactionResponse.description());
        assertEquals(depositAmount, transactionResponse.amount());
        assertEquals(newAccountBalance, transactionResponse.accountBalance());
        assertEquals(formatDateTime(LocalDateTime.now()), transactionResponse.transactionDate());
    }


    @Test
    void testTransfer() throws Exception {
        String sourceAccountNumber = "0123456789";
        String destinationAccountNumber = "9876543210";
        BigDecimal amount = new BigDecimal("500.0");
        String narration = "Transfer Test";
        Principal principal = () -> sourceAccountNumber;

        TransferDTO transferDTO = new TransferDTO(destinationAccountNumber, amount, narration);

        TransactionResponse expectedResponse = new TransactionResponse(
                TransactionType.TRANSFER.toString(),
                "Wallet-to-wallet transfer",
                amount,
                new BigDecimal("1000.0"),
                formatDateTime(LocalDateTime.now()),
                destinationAccountNumber
        );

        when(accountService.fundsTransfer(any(TransferDTO.class), eq(sourceAccountNumber))).thenReturn(expectedResponse);

        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(transferDTO))
                        .principal(principal)).andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        ResponseDTO responseDTO = objectMapper.readValue(responseContent, ResponseDTO.class);

        assertTrue(responseDTO.isSuccessful());
        assertEquals("Successful", responseDTO.message());

        TransactionResponse actualResponse = objectMapper.readValue(objectMapper.writeValueAsString(responseDTO.data()), TransactionResponse.class);
        assertEquals(expectedResponse.transactionType(), actualResponse.transactionType());
        assertEquals(expectedResponse.description(), actualResponse.description());
        assertEquals(expectedResponse.amount(), actualResponse.amount());
        assertEquals(expectedResponse.accountBalance(), actualResponse.accountBalance());
        assertEquals(expectedResponse.transactionDate(), actualResponse.transactionDate());
        assertEquals(expectedResponse.to(), actualResponse.to());
    }

    @Test
    void testGetAccountInfo_AccountFound() throws Exception {
        String accountNumber = "0123456789";
        BigDecimal balance = new BigDecimal("1000.0");

        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setBio("A user bio");

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        account.setUser(user);

        UserResponse userResponse = new UserResponse();
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setBio(user.getBio());
        userResponse.setAccountNumber(accountNumber);
        userResponse.setBalance(balance);
        when(accountService.accountEnquiry(accountNumber)).thenReturn(userResponse);

        MvcResult mvcResult = mockMvc.perform(get(BASE_URL + "/account-info/{accountNumber}", accountNumber))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        ResponseDTO responseDTO = objectMapper.readValue(responseContent, ResponseDTO.class);
        assertTrue(responseDTO.isSuccessful());

        UserResponse result = objectMapper.convertValue(responseDTO.data(), UserResponse.class);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getBio(), result.getBio());
        assertEquals(accountNumber, result.getAccountNumber());
        assertEquals(balance, result.getBalance());
    }

    @Test
    void testGetAccountInfo_AccountNotFound() throws Exception {
        String accountNumber = "0123456789";
        when(accountService.accountEnquiry(accountNumber)).thenThrow(new AccountException("Account not found"));

        mockMvc.perform(get(BASE_URL + "/account-info/{accountNumber}", accountNumber))
                .andExpect(status().isBadRequest());
    }


    @Test
     void testFetchTransactions() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        MultiValueMap<String, String> allRequestParams = new LinkedMultiValueMap<>();
        allRequestParams.add("transactionType", "TRANSFER");
        allRequestParams.add("accountNumber", "123456789");
        allRequestParams.add("startDate", "2023-08-10T00:00:00");
        allRequestParams.add("endDate", "2023-08-20T00:00:00");
        allRequestParams.add("page", "0");
        allRequestParams.add("size", "10");
        allRequestParams.add("draw", "1");
        allRequestParams.add("start", "0");
        allRequestParams.add("length", "10");
        allRequestParams.add("startPage", "0");

        List<Transaction> transactions = List.of(new Transaction(), new Transaction());
        Map<String, Object> response = Map.of(
                "data", transactions,
                "draw", 1,
                "recordsTotal", transactions.size(),
                "recordsFiltered", transactions.size()
        );

        when(accountService.getTransactionsDetails(allRequestParams.toSingleValueMap())).thenReturn(response);


        MvcResult mvcResult = mockMvc.perform(get(BASE_URL + "/transactions")
                        .params(allRequestParams))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        ResponseDTO responseDTO = objectMapper.readValue(responseContent, ResponseDTO.class);
        assertTrue(responseDTO.isSuccessful());
        assertEquals("Successful", responseDTO.message());

        assertNotNull(responseDTO.data());
        assertTrue(responseDTO.data() instanceof Map);
        Map<String, Object> responseData = (Map<String, Object>) responseDTO.data();
        assertTrue(responseData.containsKey("data"));
        assertTrue(responseData.containsKey("draw"));
        assertTrue(responseData.containsKey("recordsTotal"));
        assertTrue(responseData.containsKey("recordsFiltered"));

    }

    private static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}