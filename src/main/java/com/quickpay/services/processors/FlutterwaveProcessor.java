package com.quickpay.services.processors;

import com.quickpay.client.FlutterwaveClient;
import com.quickpay.data.dto.TransactionDTO;
import com.quickpay.data.dto.TransferDTO;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlutterwaveProcessor implements PaymentProcessor {
    private final FlutterwaveClient flutterwaveClient;

    @Override
    public TransactionDTO processTransfer(TransferDTO transferDTO, Retry retry) {
        return null;
    }
}
