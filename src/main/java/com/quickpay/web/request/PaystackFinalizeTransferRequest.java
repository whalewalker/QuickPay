package com.quickpay.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class PaystackFinalizeTransferRequest {
    @JsonProperty("transfer_code")
    String transferCode;

    @JsonProperty("otp")
    String otp;
}
