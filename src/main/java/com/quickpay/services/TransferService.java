package com.quickpay.services;

import com.quickpay.data.dto.TransferDTO;
import com.quickpay.services.processors.ProcessorFactory;
import com.quickpay.services.processors.PaymentProcessor;
import com.quickpay.web.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final ProcessorFactory processorFactory;

    public TransactionResponse processTransfer(TransferDTO transferDTO) {
        PaymentProcessor processor = processorFactory.getNextProcessor();
        TransactionResponse response = processor.processTransfer(transferDTO);
        return null;
    }

}
