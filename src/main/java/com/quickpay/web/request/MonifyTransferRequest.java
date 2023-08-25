package com.quickpay.web.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonifyTransferRequest {
    @JsonProperty("amount")
    int amount;

    @JsonProperty("reference")
    String reference;

    @JsonProperty("narration")
    String narration;

    @JsonProperty("destinationBankCode")
    String destinationBankCode;

    @JsonProperty("destinationAccountNumber")
    String destinationAccountNumber;

    @JsonProperty("currency")
    String currency;

    @JsonProperty("sourceAccountNumber")
    String sourceAccountNumber;
}
