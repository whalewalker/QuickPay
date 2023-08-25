package com.quickpay.client;


import com.quickpay.web.request.PaystackFinalizeTransferRequest;
import com.quickpay.web.request.PaystackInitiateTransferRequest;
import com.quickpay.web.response.BankResponse;
import com.quickpay.web.response.PaysatckInitiateTransferResponse;
import com.quickpay.web.response.PaystackFinalizeTransferResponse;
import org.springframework.http.MediaType;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE, contentType = MediaType.APPLICATION_JSON_VALUE)
public interface PaystackClient{

    @PostExchange(value = "/transfer")
    PaysatckInitiateTransferResponse initiateTransfer(PaystackInitiateTransferRequest request);

    @PostExchange(value = "/finalize_transfer")
    PaystackFinalizeTransferResponse finalizeTransfer(PaystackFinalizeTransferRequest request);

    @GetExchange(value = "/bank")
    BankResponse banks();
}
