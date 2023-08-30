package com.quickpay.data.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum Processor {
    FLUTTERWAVE("Flutterwave", 0, 0),
    MONIFY("Monify", 7, 7),
    PAYSTACK("Paystack", 0, 0);

    private final String description;
    private final int weight;
    private final int priority;

    public static List<Processor> getSortedProcessors() {
        return Arrays.stream(values())
                .sorted(Comparator.comparingInt(Processor::getPriority))
                .collect(Collectors.toList());
    }

}
