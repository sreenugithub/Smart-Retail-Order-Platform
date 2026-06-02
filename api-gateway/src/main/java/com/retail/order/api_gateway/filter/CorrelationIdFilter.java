package com.retail.order.api_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final String CORRELATION_ID = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        String correlationId = exchange.getRequest()
                .getHeaders()
                .getFirst(CORRELATION_ID);
        MDC.put("correlationId", correlationId);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header(CORRELATION_ID, correlationId)
                .build();

         log.info(  "Gateway request received with CorrelationId={}", correlationId);
        return chain.filter(exchange.mutate().request(request).build()).doFinally(signal -> MDC.clear());
    }

    @Override
    public int getOrder() {
        return -2;
    }
}