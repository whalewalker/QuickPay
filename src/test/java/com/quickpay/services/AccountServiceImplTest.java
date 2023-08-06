package com.quickpay.services;

import com.quickpay.data.dto.DepositDTO;
import com.quickpay.data.dto.TransferDTO;
import com.quickpay.data.model.Account;
import com.quickpay.data.model.Transaction;
import com.quickpay.data.model.TransactionType;
import com.quickpay.data.model.User;
import com.quickpay.data.repository.AccountRepository;
import com.quickpay.data.repository.TransactionRepository;
import com.quickpay.web.exception.AccountException;
import com.quickpay.web.exception.InsufficientBalanceException;
import com.quickpay.web.response.TransactionResponse;
import com.quickpay.web.response.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.quickpay.utils.Utils.formatDateTime;
import static com.quickpay.utils.Utils.generateTransactionDescription;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private ModelMapper mapper;

    @Test
    void testDeposit() {
        String accountNumber = "1234567890";
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

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any())).thenReturn(savedTransaction);

        TransactionResponse transactionResponse = accountService.deposit(depositDTO, principal);

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
        verify(transactionRepository, times(1)).save(any());
        verify(accountRepository, times(1)).save(account);

        assertEquals(TransactionType.DEPOSIT.toString(), transactionResponse.transactionType());
        assertEquals(generateTransactionDescription(depositAmount), transactionResponse.description());
        assertEquals(depositAmount, transactionResponse.amount());
        assertEquals(newAccountBalance, transactionResponse.accountBalance());
        assertEquals(formatDateTime(LocalDateTime.now()), transactionResponse.transactionDate());
    }

    @Test
    void testDepositWhenAccountNotFound() {
        String accountNumber = "1234567890";
        BigDecimal depositAmount = new BigDecimal("500.00");

        DepositDTO depositDTO = new DepositDTO(depositAmount);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(accountNumber);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        assertThrows(AccountException.class, () ->
                accountService.deposit(depositDTO, principal));

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
        verify(transactionRepository, never()).save(any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void testFundsTransfer() {
        String sourceAccountNumber = "0123456789";
        String destinationAccountNumber = "9876543210";
        BigDecimal amount = new BigDecimal("500.00");
        String narration = "Transfer Test";

        TransferDTO transferDTO = new TransferDTO(destinationAccountNumber, amount, narration);

        Account sourceAccount = new Account();
        sourceAccount.setAccountNumber(sourceAccountNumber);
        sourceAccount.setBalance(new BigDecimal("1000.00"));

        Account destinationAccount = new Account();
        destinationAccount.setAccountNumber(destinationAccountNumber);
        destinationAccount.setBalance(new BigDecimal("500.00"));

        when(accountRepository.findByAccountNumber(sourceAccountNumber)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber(destinationAccountNumber)).thenReturn(Optional.of(destinationAccount));

        TransactionResponse expectedResponse = new TransactionResponse(
                TransactionType.TRANSFER.toString(),
                "Wallet-to-wallet transfer",
                amount,
                new BigDecimal("500.00"),
                formatDateTime(LocalDateTime.now()),
                destinationAccountNumber);

        TransactionResponse actualResponse = accountService.fundsTransfer(transferDTO, sourceAccountNumber);

        assertEquals(expectedResponse.transactionType(), actualResponse.transactionType());
        assertEquals(expectedResponse.description(), actualResponse.description());
        assertEquals(expectedResponse.amount(), actualResponse.amount());
        assertEquals(expectedResponse.accountBalance(), actualResponse.accountBalance());
        assertEquals(expectedResponse.transactionDate(), actualResponse.transactionDate());
        assertEquals(expectedResponse.to(), actualResponse.to());

        verify(accountRepository, times(1)).findByAccountNumber(sourceAccountNumber);
        verify(accountRepository, times(1)).findByAccountNumber(destinationAccountNumber);
        verify(accountRepository, times(1)).save(sourceAccount);
        verify(accountRepository, times(1)).save(destinationAccount);
    }

    @Test
    void testFundsTransfer_InsufficientBalanceException() {
        String sourceAccountNumber = "0123456789";
        String destinationAccountNumber = "9876543210";
        BigDecimal amount = new BigDecimal("1500.00"); // Set an amount greater than the balance
        String narration = "Transfer Test";

        TransferDTO transferDTO = new TransferDTO(destinationAccountNumber, amount, narration);

        Account sourceAccount = new Account();
        sourceAccount.setAccountNumber(sourceAccountNumber);
        sourceAccount.setBalance(new BigDecimal("1000.00")); // Set the balance less than the debit amount

        Account destinationAccount = new Account();
        destinationAccount.setAccountNumber(destinationAccountNumber);
        destinationAccount.setBalance(new BigDecimal("500.00"));

        when(accountRepository.findByAccountNumber(sourceAccountNumber)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber(destinationAccountNumber)).thenReturn(Optional.of(destinationAccount));

        InsufficientBalanceException insufficientBalanceException = assertThrows(InsufficientBalanceException.class, () -> accountService.fundsTransfer(transferDTO, sourceAccountNumber));
        assertEquals("Insufficient balance to perform the debit operation", insufficientBalanceException.getMessage());
    }

    @Test
    void testAccountEnquiry() {
        String accountNumber = "0123456789";
        BigDecimal balance = new BigDecimal("1000.00");

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

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
        when(mapper.map(user, UserResponse.class)).thenReturn(userResponse);

        UserResponse result = accountService.accountEnquiry(accountNumber);

        assertEquals(userResponse.getName(), result.getName());
        assertEquals(userResponse.getEmail(), result.getEmail());
        assertEquals(userResponse.getBio(), result.getBio());
        assertEquals(userResponse.getAccountNumber(), result.getAccountNumber());
        assertEquals(userResponse.getBalance(), result.getBalance());
    }

    @Test
    void testAccountEnquiry_AccountNotFound() {
        String accountNumber = "0123456789";

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());
        assertThrows(AccountException.class, () -> accountService.accountEnquiry(accountNumber));
    }

    @Test
     void testGetTransactionsDetails() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("transactionType", "TRANSFER");
        allRequestParams.put("accountNumber", "123456789");
        allRequestParams.put("startDate", "2023-08-10T00:00:00");
        allRequestParams.put("endDate", "2023-08-20T00:00:00");
        allRequestParams.put("page", "0");
        allRequestParams.put("size", "10");
        allRequestParams.put("draw", "1");
        allRequestParams.put("start", "0");
        allRequestParams.put("length", "10");
        allRequestParams.put("startPage", "0");

        List<Transaction> transactions = List.of(new Transaction(), new Transaction());
        Page<Transaction> page = new PageImpl<>(transactions);

        Specification<Transaction> specification = ArgumentMatchers.any(Specification.class);
        Pageable pageable = ArgumentMatchers.any(Pageable.class);
        Mockito.when(transactionRepository.findAll(specification, pageable)).thenReturn(page);

        Map<String, Object> response = accountService.getTransactionsDetails(allRequestParams);

        assertNotNull(response);
        assertTrue(response.containsKey("data"));
        assertTrue(response.containsKey("draw"));
        assertTrue(response.containsKey("recordsTotal"));
        assertTrue(response.containsKey("recordsFiltered"));
        assertEquals(transactions, response.get("data"));
    }
}