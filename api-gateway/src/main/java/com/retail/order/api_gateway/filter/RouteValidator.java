package com.retail.order.api_gateway.filter;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints =
            List.of(
                    "/api/auth/register",
                    "/api/auth/login",
                    "/actuator/prometheus",
                    "/actuator/info",
                    "/actuator/health"
            );

    public Predicate<String> isSecured =
            uri -> openApiEndpoints
                    .stream()
                    .noneMatch(uri::startsWith);
}