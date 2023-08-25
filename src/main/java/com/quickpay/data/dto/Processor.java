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
    FLUTTERWAVE("Flutterwave", 3, 1),
    MONIFY("Monify", 2, 2),
    PAYSTACK("Paystack", 5, 3);

    private final String description;
    private final int weight;
    private final int priority;

    public static List<Processor> getSortedProcessors() {
        return Arrays.stream(values())
                .sorted(Comparator.comparingInt(Processor::getPriority))
                .collect(Collectors.toList());
    }

}
