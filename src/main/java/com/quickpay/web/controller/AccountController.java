package com.quickpay.web.controller;

import com.quickpay.data.dto.DepositDTO;
import com.quickpay.data.dto.ResponseDTO;
import com.quickpay.data.dto.TransferDTO;
import com.quickpay.services.AccountService;
import com.quickpay.web.response.TransactionResponse;
import com.quickpay.web.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("api/v1/quick-pay/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private static final String RESPONSE_MESSAGE = "Successful";

    @PostMapping("/deposit")
    public ResponseEntity<ResponseDTO> deposit(@RequestBody @Valid DepositDTO depositDTO, Principal principal) {
        TransactionResponse response = accountService.deposit(depositDTO, principal);
        return  ResponseEntity.ok(new ResponseDTO( true, RESPONSE_MESSAGE, response));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ResponseDTO> transfer(@RequestBody @Valid TransferDTO transferDTO, Principal principal) {
        TransactionResponse response = accountService.fundsTransfer( transferDTO,  principal.getName());
        return  ResponseEntity.ok(new ResponseDTO( true, RESPONSE_MESSAGE, response));
    }

    @GetMapping("account-info/{accountNumber}")
    public ResponseEntity<ResponseDTO> getAccountInfo(@PathVariable String accountNumber) {
        UserResponse response = accountService.accountEnquiry(accountNumber);
        return  ResponseEntity.ok(new ResponseDTO( true, RESPONSE_MESSAGE, response));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ResponseDTO> fetchTransactions(@RequestParam Map<String, String> allRequestParams, Principal principal) {
        Map<String, Object> response = accountService.getTransactionsDetails(allRequestParams, principal);
        return  ResponseEntity.ok(new ResponseDTO( true, RESPONSE_MESSAGE, response));
    }
}
