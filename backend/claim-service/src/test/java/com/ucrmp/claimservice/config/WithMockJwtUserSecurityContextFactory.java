package com.ucrmp.claimservice.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WithMockJwtUserSecurityContextFactory 
    implements WithSecurityContextFactory<WithMockJwtUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockJwtUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Extract the data from our custom annotation
        UUID userId = UUID.fromString(annotation.userId());
        List<SimpleGrantedAuthority> authorities = Arrays.stream(annotation.roles())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // This is the *exact* object our JwtAuthenticationFilter creates:
        // The principal is the UUID, and credentials are null.
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                authorities
        );

        context.setAuthentication(authToken);
        return context;
    }
}