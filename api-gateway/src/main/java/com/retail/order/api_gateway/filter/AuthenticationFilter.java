package com.retail.order.api_gateway.filter;

import com.retail.order.api_gateway.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter
        implements GlobalFilter, Ordered {

    private final RouteValidator routeValidator;
    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        String path =
                exchange.getRequest()
                        .getURI()
                        .getPath();

        if (routeValidator.isSecured.test(path)) {

            if (!exchange.getRequest()
                    .getHeaders()
                    .containsKey(HttpHeaders.AUTHORIZATION)) {

                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);

                return exchange.getResponse().setComplete();
            }

            String authHeader =
                    exchange.getRequest()
                            .getHeaders()
                            .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null ||
                    !authHeader.startsWith("Bearer ")) {

                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);

                return exchange.getResponse().setComplete();
            }

            String token =
                    authHeader.substring(7);

            try {
                jwtService.validateToken(token);
                Claims claims =
                        jwtService.extractClaims(token);

                String role =
                        claims.get("role", String.class);

                if (isAdminPath(path)
                        && !"ROLE_ADMIN".equals(role)) {
                    System.out.println("PATH = " + path);
                    System.out.println("ROLE = " + role);
                    exchange.getResponse()
                            .setStatusCode(HttpStatus.FORBIDDEN);

                    return exchange.getResponse()
                            .setComplete();
                }
            } catch (Exception ex) {

                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);

                return exchange.getResponse().setComplete();
            }
        }

        return chain.filter(exchange);
    }
    private boolean isAdminPath(String path) {

        return path.startsWith("/api/users/admin");
    }
    @Override
    public int getOrder() {
        return -1;
    }
}