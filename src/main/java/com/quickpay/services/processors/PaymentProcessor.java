package com.quickpay.services.processors;

import com.quickpay.data.dto.TransferDTO;
import com.quickpay.web.response.TransactionResponse;

public interface PaymentProcessor {
    TransactionResponse processTransfer(TransferDTO transferDTO);
}
