package com.quickpay.config;


import com.quickpay.client.PaystackClient;
import com.quickpay.web.exception.QuickPayException;
import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
@Configuration
public class PaystackClientConfig {

    @Value("${paystack.base-url}")
    private String baseUrl;

    @Value("${paystack.secret-key}")
    private String secretKey;


    @Bean
    public PaystackClient createPaystackClient(WebClient.Builder webClientBuilder) {
        HttpClient httpClient = HttpClient
                .create().proxyWithSystemProperties()
                .wiretap("reactor.netty.http.client.HttpClient", LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL, StandardCharsets.UTF_8);

        WebClient.Builder clientBuilder = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + secretKey)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultStatusHandler(HttpStatusCode::isError, res -> {
                    log.error("Error while calling endpoint with status code {}", res.statusCode());
                    var mediaType = res.headers().contentType();
                    if(mediaType.isPresent() && mediaType.get().equals(MediaType.TEXT_HTML)) {
                        return Mono.error(new QuickPayException("CBA is down"));
                    }
                    return res.bodyToMono(Object.class).flatMap(err -> Mono.error(new QuickPayException(err)));
                });

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(clientBuilder.build()))
                .blockTimeout(Duration.ofSeconds(60))
                .build();

        return factory.createClient(PaystackClient.class);
    }
}
