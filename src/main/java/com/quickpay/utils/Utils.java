package com.quickpay.utils;

import com.quickpay.data.dto.TransactionDTO;
import com.quickpay.data.dto.TransferDTO;
import com.quickpay.data.model.Transaction;
import com.quickpay.data.model.TransactionType;
import com.quickpay.web.request.MonifyTransferRequest;
import com.quickpay.web.response.MonifyTransferResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class Utils {
    private static final String CURRENCY_NAIRA = "naira";
    private static final String SOURCE_ACCOUNT = "3934178936";
    private static final ModelMapper mapper = new ModelMapper();

    public static String generateRandomValue(String pad, int length) {
        StringBuilder accountNumberBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = ThreadLocalRandom.current().nextInt(10);
            accountNumberBuilder.append(digit);
        }
        return pad.concat(accountNumberBuilder.toString());
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }

    public static String generateTransactionDescription(BigDecimal depositAmount) {
        return String.format("Deposit of %s %s made on %s", depositAmount, CURRENCY_NAIRA, formatDateTime(LocalDateTime.now()));
    }

    public static LocalDateTime convertToLastHourOfDay(LocalDateTime dateTime) {
        return dateTime.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }

    public static LocalDateTime convertToStartOfDay(LocalDateTime dateTime) {
        return dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public static boolean isNullOrEmpty(String statement) {
        return (statement == null || statement.trim().isEmpty());
    }

    public static String getValueOrDefault(Map<String, String> map, String key) {
        String value = map.get(key);
        return isNullOrEmpty(value) ? null : value;
    }

    public static LocalDateTime parseLocalDateTime(String dateString) {
        if (isNullOrEmpty(dateString)) {
            return null;
        }

        try {
            LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return LocalDateTime.of(date, LocalTime.MIN);
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date: {}", dateString);
            return null;
        }
    }

    public static MonifyTransferRequest monifyRequestMapper(TransferDTO transferDTO){
        return MonifyTransferRequest.builder()
                .currency("NGN")
                .reference(generateRandomValue("REF", 12))
                .narration(transferDTO.getNarration())
                .destinationAccountNumber(transferDTO.getBeneficiaryAccountNumber())
                .destinationBankCode(transferDTO.getBankCode())
                .sourceAccountNumber(SOURCE_ACCOUNT)
                .amount(transferDTO.getAmount().intValue())
                .build();
    }

    public static TransactionDTO monifyResponseMapper(MonifyTransferResponse response){
        return TransactionDTO.builder()
                .transactionType(TransactionType.TRANSFER)
                .amount(BigDecimal.valueOf(response.getData().getAmount()))
                .narration(response.getData().getNarration())
                .responseCode(response.getResponseCode())
                .responseMessage(response.getResponseMessage())
                .bankName(response.getData().getBankName())
                .bankCode(response.getData().getBankCode())
                .accountNumber(response.getData().getAccountNumber())
                .build();
    }

    public static  Transaction createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction =  mapper.map(transactionDTO, Transaction.class);
        transaction.setTransactionId(generateRandomValue("TT", 12));
        return transaction;
    }

}
