package com.example.ratelimiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    
    /**
     * The key to use for rate limiting. Can be a SpEL expression.
     * Defaults to the client's IP address.
     */
    String key() default "#{request.remoteAddr}";
    
    /**
     * Number of tokens to consume per request.
     */
    int tokens() default 1;
    
    /**
     * Maximum number of tokens in the bucket.
     */
    int capacity() default 10;
    
    /**
     * Rate at which tokens are refilled (tokens per second).
     */
    double refillRate() default 1.0;
    
    /**
     * Custom error message when rate limit is exceeded.
     */
    String message() default "Rate limit exceeded. Please try again later.";
}
