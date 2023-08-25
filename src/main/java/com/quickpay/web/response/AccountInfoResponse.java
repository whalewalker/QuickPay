package com.quickpay.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AccountInfoResponse {
    @JsonProperty("status")
    boolean status;

    @JsonProperty("message")
    String message;

    @JsonProperty("data")
    Data data;

    @Value
    @Builder
    @Jacksonized
    public static class Data {
        @JsonProperty("account_number")
        String accountNumber;

        @JsonProperty("account_name")
        String accountName;

        @JsonProperty("bank_id")
        int bankId;
    }

}
