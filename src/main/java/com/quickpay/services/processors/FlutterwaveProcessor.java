package com.quickpay.services.processors;

import com.quickpay.client.FlutterwaveClient;
import com.quickpay.data.dto.TransferDTO;
import com.quickpay.web.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlutterwaveProcessor implements PaymentProcessor {
    private final FlutterwaveClient flutterwaveClient;

    @Override
    public TransactionResponse processTransfer(TransferDTO transferDTO) {
        return null;
    }
}
