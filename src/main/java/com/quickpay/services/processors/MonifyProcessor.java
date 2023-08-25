package com.quickpay.services.processors;

import com.quickpay.client.MonifyClient;
import com.quickpay.data.dto.TransferDTO;
import com.quickpay.web.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonifyProcessor implements PaymentProcessor {
    private final MonifyClient  monifyClient;
    @Override
    public TransactionResponse processTransfer(TransferDTO transferDTO) {
        monifyClient.initiateTransfer(null);
        return null;
    }
}
