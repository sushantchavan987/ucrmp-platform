package com.ucrmp.claimservice.config;

import org.springframework.security.test.context.support.WithSecurityContext;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockJwtUserSecurityContextFactory.class)
public @interface WithMockJwtUser {

    // We can pass our mock user's ID here
    String userId() default "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11";

    // We can also pass mock roles
    String[] roles() default {"ROLE_EMPLOYEE"};
}