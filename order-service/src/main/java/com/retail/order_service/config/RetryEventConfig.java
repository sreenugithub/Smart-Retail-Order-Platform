package com.retail.order_service.config;

import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RetryEventConfig {

    private final RetryRegistry retryRegistry;

    @PostConstruct
    void registerRetryEventLogger() {
        retryRegistry.retry("inventoryService")
                .getEventPublisher()
                .onRetry(event -> log.warn(
                        "Retry attempt {} for {} after {}",
                        event.getNumberOfRetryAttempts(),
                        event.getName(),
                        event.getLastThrowable().toString()
                ))
                .onError(event -> log.error(
                        "Retry exhausted for {} after {} attempts",
                        event.getName(),
                        event.getNumberOfRetryAttempts()
                ));
    }
}
