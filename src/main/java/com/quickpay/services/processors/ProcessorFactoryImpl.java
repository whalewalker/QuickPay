package com.quickpay.services.processors;

import com.quickpay.client.FlutterwaveClient;
import com.quickpay.client.MonifyClient;
import com.quickpay.client.PaystackClient;
import com.quickpay.data.dto.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Service
public class ProcessorFactoryImpl implements ProcessorFactory {
    private final List<Processor> processors;
    private final int totalWeight;
    private final AtomicInteger currentProcessorIndex = new AtomicInteger(0);

    private final MonifyClient monifyClient;
    private final FlutterwaveClient flutterwaveClient;
    private final PaystackClient paystackClient;


    @Autowired
    public ProcessorFactoryImpl(MonifyClient monifyClient, FlutterwaveClient flutterwaveClient, PaystackClient paystackClient) {
        this.monifyClient = monifyClient;
        this.flutterwaveClient = flutterwaveClient;
        this.paystackClient = paystackClient;
        processors = Processor.getSortedProcessors();
        totalWeight = calculateTotalWeight();
    }

    private int calculateTotalWeight() {
        return processors.stream()
                .mapToInt(Processor::getWeight)
                .sum();
    }

    @Override
    public PaymentProcessor getNextProcessor() {
        int currentIndex = currentProcessorIndex.getAndIncrement() % totalWeight;

        return IntStream.range(0, processors.size())
                .filter(i -> currentIndex < processors.stream()
                        .limit(i + 1L)
                        .mapToInt(Processor::getWeight)
                        .sum())
                .mapToObj(processors::get)
                .findFirst()
                .map(this::createProcessor)
                .orElse(this.createProcessor(processors.get(0)));
    }


    private PaymentProcessor createProcessor(Processor processor) {
        return switch (processor) {
            case MONIFY -> new MonifyProcessor(monifyClient);
            case FLUTTERWAVE -> new FlutterwaveProcessor(flutterwaveClient);
            case PAYSTACK -> new PaystackProcessor(paystackClient);
            default -> throw new IllegalArgumentException("Unsupported processor: " + processor);
        };
    }

}
