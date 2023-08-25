package com.quickpay.services.processors;

public interface ProcessorFactory {
    PaymentProcessor getNextProcessor();
}
