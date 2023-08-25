package com.quickpay.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
@Jacksonized
public class BankResponse {
    @JsonProperty("status")
      boolean status;

    @JsonProperty("message")
      String message;

    @JsonProperty("data")
    List<Data> data;

    @Value
    @Builder
    @Jacksonized
    public static class Data {
        @JsonProperty("name")
        String name;

        @JsonProperty("slug")
        String slug;

        @JsonProperty("code")
        String code;

        @JsonProperty("longcode")
        String longcode;

        @JsonProperty("gateway")
        String gateway;

        @JsonProperty("pay_with_bank")
        boolean payWithBank;

        @JsonProperty("active")
        boolean active;

        @JsonProperty("is_deleted")
        boolean deleted;

        @JsonProperty("country")
        String country;

        @JsonProperty("currency")
        String currency;

        @JsonProperty("type")
        String type;

        @JsonProperty("id")
        int id;

        @JsonProperty("createdAt")
        LocalDateTime createdAt;

        @JsonProperty("updatedAt")
        LocalDateTime updatedAt;
    }
}
