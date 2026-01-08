package com.ucrmp.apigateway.config;

import com.ucrmp.apigateway.filter.AuthenticationFilter;
import com.ucrmp.apigateway.service.JwtService;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    // 1. Inject the FILTER'S dependencies, not the filter itself
    private final RouterValidator routerValidator;
    private final JwtService jwtService;

    public GatewayConfig(RouterValidator routerValidator, JwtService jwtService) {
        this.routerValidator = routerValidator;
        this.jwtService = jwtService;
    }

    // 2. Create the AuthenticationFilter as a @Bean here
    @Bean
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter(routerValidator, jwtService);
    }

    // 3. Define the routes and use the filter bean
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                
                // --- Route 1: Auth Service (Direct K8s DNS) ---
                .route("auth-service", r -> r.path("/api/v1/auth/**")
                        // STOP using lb:// (Eureka)
                        // START using http://<service-name>:<port> (K8s DNS)
                        .uri("http://auth-service:8081")) 
                
                // --- Route 2: Claim Service (Direct K8s DNS) ---
                .route("claim-service", r -> r.path("/api/v1/claims/**")
                        .filters(f -> f.filter(authenticationFilter().apply(new AuthenticationFilter.Config())))
                        .uri("http://claim-service:8082"))
                
                .build();
    }
}