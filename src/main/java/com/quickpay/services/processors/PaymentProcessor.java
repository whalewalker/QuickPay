package com.quickpay.services.processors;

import com.quickpay.data.dto.TransactionDTO;
import com.quickpay.data.dto.TransferDTO;
import io.github.resilience4j.retry.Retry;

public interface PaymentProcessor {
    TransactionDTO processTransfer(TransferDTO transferDTO, Retry retry);
}
