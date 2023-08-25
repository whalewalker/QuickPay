package com.quickpay.web.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlutterwaveTransferRequest {
    @JsonProperty("account_bank")
    String accountBank;

    @JsonProperty("account_number")
    String accountNumber;

    @JsonProperty("amount")
    int amount;

    @JsonProperty("narration")
    int narration;

    @JsonProperty("currency")
    String currency;

    @JsonProperty("reference")
    String reference;

    @JsonProperty("debitCurrency")
    String debitCurrency;
}
