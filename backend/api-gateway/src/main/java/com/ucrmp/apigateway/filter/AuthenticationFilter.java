package com.ucrmp.apigateway.filter;

import com.ucrmp.apigateway.config.RouterValidator;
import com.ucrmp.apigateway.service.JwtService;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;


public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouterValidator validator;
    private final JwtService jwtService;

    public AuthenticationFilter(RouterValidator validator, JwtService jwtService) {
        super(Config.class);
        this.validator = validator;
        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. Check if the endpoint is public (not secured)
            if (validator.isSecured.test(request)) {

                // 2. It's a secured endpoint, so check for auth header
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return this.onError(exchange, "Missing authorization header", HttpStatus.UNAUTHORIZED);
                }

                String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return this.onError(exchange, "Authorization header is invalid", HttpStatus.UNAUTHORIZED);
                }

                String token = authHeader.substring(7);

                // 3. Validate the token
                if (!jwtService.isTokenValid(token)) {
                    return this.onError(exchange, "Token is not valid", HttpStatus.UNAUTHORIZED);
                }

                // 4. Token is valid! Extract claims and add them as headers
                UUID userId = jwtService.extractUserId(token);
                String userEmail = jwtService.extractUsername(token);
                List<String> roles = jwtService.extractRoles(token);

                // 5. Mutate the request to add new headers
                ServerHttpRequest newRequest = request.mutate()
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Email", userEmail)
                        .header("X-User-Roles", String.join(",", roles))
                        .build();

                return chain.filter(exchange.mutate().request(newRequest).build());
            }

            // It's a public endpoint, let it pass
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        // You can set a JSON error body here if you want
        return response.setComplete();
    }

    public static class Config {
        // Empty config class
    }
}