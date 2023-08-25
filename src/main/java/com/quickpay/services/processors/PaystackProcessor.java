package com.quickpay.services.processors;

import com.quickpay.client.PaystackClient;
import com.quickpay.data.dto.TransferDTO;
import com.quickpay.web.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaystackProcessor implements PaymentProcessor {
    private final PaystackClient paystackClient;

    @Override
    public TransactionResponse processTransfer(TransferDTO transferDTO) {
        return null;
    }
}
