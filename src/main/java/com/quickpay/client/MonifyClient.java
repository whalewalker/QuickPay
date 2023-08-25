package com.quickpay.client;

import com.quickpay.web.request.MonifyTransferRequest;
import com.quickpay.web.response.MonifyTransferResponse;
import org.springframework.http.MediaType;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(url = "/api/v2/disbursements/single", accept = MediaType.APPLICATION_JSON_VALUE, contentType = MediaType.APPLICATION_JSON_VALUE)

public interface MonifyClient{
    @PostExchange
    MonifyTransferResponse initiateTransfer(MonifyTransferRequest request);
}
