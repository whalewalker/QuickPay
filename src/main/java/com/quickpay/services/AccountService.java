package com.quickpay.services;

import com.quickpay.data.dto.DepositDTO;
import com.quickpay.data.dto.TransferDTO;
import com.quickpay.web.response.TransactionResponse;
import com.quickpay.web.response.UserResponse;

import java.security.Principal;
import java.util.Map;

public interface AccountService {
    TransactionResponse deposit(DepositDTO depositDTO, Principal principal);

    TransactionResponse fundsTransfer(TransferDTO transferDTO, String sourceAccountNumber);

    Map<String, Object> getTransactionsDetails(Map<String, String> allRequestParams, Principal principal);

    UserResponse accountEnquiry(String accountNumber);
}
