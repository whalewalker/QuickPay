package com.quickpay.web.controller;

import com.quickpay.data.dto.DepositDTO;
import com.quickpay.data.dto.ResponseDTO;
import com.quickpay.data.dto.TransferDTO;
import com.quickpay.data.model.Transaction;
import com.quickpay.services.AccountService;
import com.quickpay.services.TransferService;
import com.quickpay.web.response.TransactionResponse;
import com.quickpay.web.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("quickpay/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final TransferService transferService;
    private static final String RESPONSE_MESSAGE = "Successful";

    @PostMapping("inter/deposit")
    public ResponseEntity<ResponseDTO> deposit(@RequestBody @Valid DepositDTO depositDTO, Principal principal) {
        TransactionResponse response = accountService.deposit(depositDTO, principal);
        return  ResponseEntity.ok(new ResponseDTO( true, RESPONSE_MESSAGE, response));
    }

    @PostMapping("inter/transfer")
    public ResponseEntity<ResponseDTO> processInterTransfer(@RequestBody @Valid TransferDTO transferDTO, Principal principal) {
        TransactionResponse response = accountService.processTransfer( transferDTO,  principal.getName());
        return  ResponseEntity.ok(new ResponseDTO( true, RESPONSE_MESSAGE, response));
    }

    @PostMapping("intra/transfer")
    public ResponseEntity<ResponseDTO> processIntraTransfer(@RequestBody @Valid TransferDTO transferDTO) {
        Transaction response = transferService.processTransfer( transferDTO);
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
