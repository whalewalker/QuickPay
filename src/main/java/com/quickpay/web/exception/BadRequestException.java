package com.quickpay.web.exception;

public class BadRequestException extends QuickPayException{
    public BadRequestException(String message) {
        super(message);
    }
}
