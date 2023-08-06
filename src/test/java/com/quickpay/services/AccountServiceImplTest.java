package com.quickpay.services;

import com.quickpay.data.dto.DepositDTO;
import com.quickpay.data.model.Account;
import com.quickpay.data.model.Transaction;
import com.quickpay.data.model.TransactionType;
import com.quickpay.data.repository.AccountRepository;
import com.quickpay.data.repository.TransactionRepository;
import com.quickpay.web.exception.AccountException;
import com.quickpay.web.response.TransactionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.quickpay.utils.Utils.formatDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        savedTransaction.setNarration("Deposit of ₦" + depositAmount + " made on " + formatDateTime(LocalDateTime.now()));
        savedTransaction.setAmount(depositAmount);
        savedTransaction.setAccountBalance(newAccountBalance);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any())).thenReturn(savedTransaction);

        TransactionResponse transactionResponse = accountService.deposit(depositDTO, principal);

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
        verify(transactionRepository, times(1)).save(any());
        verify(accountRepository, times(1)).save(account);

        assertEquals(accountNumber, transactionResponse.accountNumber());
        assertEquals(TransactionType.DEPOSIT.toString(), transactionResponse.transactionType());
        assertEquals("Deposit of ₦" + depositAmount + " made on " + formatDateTime(LocalDateTime.now()), transactionResponse.description());
        assertEquals(depositAmount, transactionResponse.amount());
        assertEquals(newAccountBalance, transactionResponse.newAccountBalance());
        assertEquals(LocalDateTime.now().toString(), transactionResponse.transactionDate());
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

}