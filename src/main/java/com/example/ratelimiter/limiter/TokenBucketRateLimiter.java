package com.example.ratelimiter.limiter;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicDouble;

@Component
public class TokenBucketRateLimiter {
    
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    public boolean tryConsume(String key, int tokens, int capacity, double refillRate) {
        Bucket bucket = buckets.compute(key, (k, existingBucket) -> {
            if (existingBucket == null) {
                return new Bucket(capacity, refillRate);
            }
            existingBucket.refill();
            return existingBucket;
        });
        
        return bucket.tryConsume(tokens);
    }
    
    public boolean tryConsume(String key, int tokens) {
        // Default values: 10 tokens capacity, 1 token per second refill rate
        return tryConsume(key, tokens, 10, 1.0);
    }
    
    public void resetBucket(String key) {
        buckets.remove(key);
    }
    
    public void resetAllBuckets() {
        buckets.clear();
    }
    
    public int getBucketCount() {
        return buckets.size();
    }
    
    private static class Bucket {
        private final int capacity;
        private final double refillRate; // tokens per second
        private final AtomicDouble tokens;
        private volatile Instant lastRefillTime;
        
        public Bucket(int capacity, double refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.tokens = new AtomicDouble(capacity);
            this.lastRefillTime = Instant.now();
        }
        
        public void refill() {
            Instant now = Instant.now();
            double elapsedSeconds = Duration.between(lastRefillTime, now).toNanos() / 1_000_000_000.0;
            double tokensToAdd = elapsedSeconds * refillRate;
            
            if (tokensToAdd > 0) {
                tokens.updateAndGet(currentTokens -> 
                    Math.min(capacity, currentTokens + tokensToAdd));
                lastRefillTime = now;
            }
        }
        
        public boolean tryConsume(int requestedTokens) {
            return tokens.updateAndGet(currentTokens -> {
                if (currentTokens >= requestedTokens) {
                    return currentTokens - requestedTokens;
                }
                return currentTokens; // Don't consume if not enough tokens
            }) >= 0;
        }
        
        public double getAvailableTokens() {
            refill();
            return tokens.get();
        }
    }
}
