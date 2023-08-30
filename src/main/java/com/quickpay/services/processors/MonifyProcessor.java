package com.quickpay.services.processors;

import com.quickpay.client.MonifyClient;
import com.quickpay.data.dto.TransactionDTO;
import com.quickpay.data.dto.TransferDTO;
import com.quickpay.web.request.MonifyTransferRequest;
import com.quickpay.web.response.MonifyTransferResponse;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.quickpay.utils.Utils.monifyRequestMapper;
import static com.quickpay.utils.Utils.monifyResponseMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonifyProcessor implements PaymentProcessor {
    private final MonifyClient  monifyClient;
    @Override
    public TransactionDTO processTransfer(TransferDTO transferDTO, Retry retry) {
        MonifyTransferRequest request = monifyRequestMapper(transferDTO);
        MonifyTransferResponse response = retry.executeSupplier(()-> monifyClient.initiateTransfer(request));
        return monifyResponseMapper(response);
    }
}
