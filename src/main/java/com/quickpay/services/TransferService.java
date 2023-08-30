package com.quickpay.services;

import com.quickpay.data.dto.TransactionDTO;
import com.quickpay.data.dto.TransferDTO;
import com.quickpay.data.model.Transaction;
import com.quickpay.data.repository.TransactionRepository;
import com.quickpay.services.processors.PaymentProcessor;
import com.quickpay.services.processors.ProcessorFactory;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.quickpay.utils.Utils.createTransaction;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final ProcessorFactory processorFactory;
    private final TransactionRepository transactionRepository;
    private final RetryRegistry retryRegistry;

    public Transaction processTransfer(TransferDTO transferDTO) {
        Retry retry = retryRegistry.retry("default");
        PaymentProcessor processor = processorFactory.getNextProcessor();
        TransactionDTO response = processor.processTransfer(transferDTO, retry);
        Transaction transaction = createTransaction(response);
        return transactionRepository.save(transaction);
    }

}
