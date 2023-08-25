package com.quickpay.web.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QuickPayException extends RuntimeException{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public QuickPayException(String message) {
        super(message);
    }

    public QuickPayException(Object err){
        super(OBJECT_MAPPER.convertValue(err, JsonNode.class).toPrettyString());
    }
}
