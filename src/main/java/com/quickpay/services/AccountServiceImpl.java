package com.quickpay.services;

import com.quickpay.data.dto.DepositDTO;
import com.quickpay.data.dto.FilterDTO;
import com.quickpay.data.dto.TransferDTO;
import com.quickpay.data.model.Account;
import com.quickpay.data.model.Transaction;
import com.quickpay.data.model.TransactionType;
import com.quickpay.data.model.User;
import com.quickpay.data.repository.AccountRepository;
import com.quickpay.data.repository.TransactionRepository;
import com.quickpay.specification.TransactionSpecification;
import com.quickpay.web.exception.AccountException;
import com.quickpay.web.exception.InsufficientBalanceException;
import com.quickpay.web.response.TransactionResponse;
import com.quickpay.web.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

import static com.quickpay.utils.Utils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ModelMapper mapper;


    @Override
    public TransactionResponse deposit(DepositDTO depositDTO, Principal principal) {
        String accountNumber = principal.getName();
        Account accountToFund = findAccountByAccountNumber(accountNumber);

        String description = generateTransactionDescription(depositDTO.getAmount());
        credit(accountToFund, depositDTO.getAmount(), description);

        saveAccount(accountToFund);

        return new TransactionResponse(
                TransactionType.DEPOSIT.toString(),
                description,
                depositDTO.getAmount(),
                accountToFund.getBalance(),
                formatDateTime(LocalDateTime.now()));
    }

    @Override
    public TransactionResponse fundsTransfer(TransferDTO transferDTO, String sourceAccountNumber) {
        Account sourceAccount = findAccountByAccountNumber(sourceAccountNumber);
        Account destinationAccount = findAccountByAccountNumber(transferDTO.getAccountNumber());

        debit(sourceAccount, transferDTO.getAmount(), transferDTO.getNarration());
        credit(destinationAccount, transferDTO.getAmount(), transferDTO.getNarration());

        saveAccount(sourceAccount);
        saveAccount(destinationAccount);

        return new TransactionResponse(
                TransactionType.TRANSFER.toString(),
                "Wallet-to-wallet transfer",
                transferDTO.getAmount(),
                sourceAccount.getBalance(),
                formatDateTime(LocalDateTime.now()),
                destinationAccount.getAccountNumber()
        );
    }

    @Override
    public Map<String, Object> getTransactionsDetails(Map<String, String> allRequestParams) {
        FilterDTO filterDTO = new FilterDTO(
                getValueOrDefault(allRequestParams, "transactionType"),
                getValueOrDefault(allRequestParams, "transactionId"),
                getValueOrDefault(allRequestParams, "accountNumber"),
                parseLocalDateTime(allRequestParams.get("startDate")),
                parseLocalDateTime(allRequestParams.get("endDate")),
                Integer.parseInt(allRequestParams.getOrDefault("page", "0")),
                Integer.parseInt(allRequestParams.getOrDefault("size", "10")),
                Integer.parseInt(allRequestParams.getOrDefault("draw", "1")),
                Integer.parseInt(allRequestParams.getOrDefault("start", "0")),
                Integer.parseInt(allRequestParams.getOrDefault("length", "10")),
                Integer.parseInt(allRequestParams.getOrDefault("startPage", "0"))
        );

        return getTransactionsDetails(filterDTO);
    }

    private Map<String, Object> getTransactionsDetails(FilterDTO filterDTO) {
        Map<String, Object> response;

        Specification<Transaction> specification = Specification
                .where(TransactionSpecification.withTransactionId(filterDTO.getTransactionId()))
                .and(TransactionSpecification.withBetweenDate(filterDTO.getStartDate(), filterDTO.getEndDate()))
                .and(TransactionSpecification.withTransactionType(filterDTO.getTransactionType()))
                .and(TransactionSpecification.withAccount(filterDTO.getAccountNumber()));

        Page<Transaction> page = transactionRepository.findAll(specification,
                PageRequest.of(filterDTO.getPage(), filterDTO.getSize(), Sort.by(Sort.Direction.DESC, "id")));

        response = Map.of(
                "data", page.getContent(),
                "draw", filterDTO.getDraw(),
                "recordsTotal", page.getTotalElements(),
                "recordsFiltered", page.getTotalElements()
        );
        return response;
    }

    @Override
    public UserResponse accountEnquiry(String accountNumber) {
        Account account = findAccountByAccountNumber(accountNumber);
        User user = account.getUser();
        UserResponse userResponse = mapper.map(user, UserResponse.class);
        userResponse.setAccountNumber(accountNumber);
        userResponse.setBalance(account.getBalance());
        return userResponse;
    }

    private Account findAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException("No account found with that account number: " + accountNumber));
    }

    private Transaction createTransaction(String transactionType, String narration, BigDecimal amount, BigDecimal accountBalance) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(transactionType);
        transaction.setNarration(narration);
        transaction.setAmount(amount);
        transaction.setAccountBalance(accountBalance);
        transaction.setTransactionId(generateRandomValue("TT", 12));
        return transaction;
    }

    private void debit(Account account, BigDecimal amount, String narration) {
        BigDecimal newBalance = account.getBalance().subtract(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException("Insufficient balance to perform the debit operation");
        }

        account.setBalance(newBalance);
        Transaction debitTransaction = createTransaction(TransactionType.DEBIT.name(), narration, amount, newBalance);
        debitTransaction.setAccount(account);
        Transaction savedDebitTransaction = saveTransaction(debitTransaction);
        account.addTransaction(savedDebitTransaction);
    }


    private void credit(Account account, BigDecimal amount, String narration) {
        account.setBalance(account.getBalance().add(amount));
        Transaction creditTransaction = createTransaction(TransactionType.CREDIT.name(), narration, amount, account.getBalance());
        creditTransaction.setAccount(account);
        Transaction savedCreditTransaction = saveTransaction(creditTransaction);
        account.addTransaction(savedCreditTransaction);
    }


    private Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    private void saveAccount(Account account) {
        accountRepository.save(account);
    }
}
