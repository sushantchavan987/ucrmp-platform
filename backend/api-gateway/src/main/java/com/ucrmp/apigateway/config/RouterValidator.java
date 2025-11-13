package com.ucrmp.apigateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidator {

    // This is our list of public endpoints that require no authentication
    public static final List<String> openApiEndpoints = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login"
    );

    // This is the updated, more robust logic
    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    // Check if the request path *starts with* any of the openApiEndpoints
                    .noneMatch(uri -> request.getURI().getPath().startsWith(uri));
}