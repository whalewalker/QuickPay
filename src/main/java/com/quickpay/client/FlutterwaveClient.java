package com.quickpay.client;

import com.quickpay.web.request.FlutterwaveTransferRequest;
import com.quickpay.web.response.FlutterwaveTransferResponse;
import org.springframework.http.MediaType;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(url = "/v3/transfers", accept = MediaType.APPLICATION_JSON_VALUE, contentType = MediaType.APPLICATION_JSON_VALUE)
public interface FlutterwaveClient{
    @PostExchange
    FlutterwaveTransferResponse transfer(FlutterwaveTransferRequest request);
}
