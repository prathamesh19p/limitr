package com.example.ratelimiter.controller;

import com.example.ratelimiter.annotation.RateLimit;
import com.example.ratelimiter.limiter.TokenBucketRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private TokenBucketRateLimiter rateLimiter;

    @GetMapping("/hello")
    @RateLimit(tokens = 1, capacity = 5, refillRate = 1.0)
    public ResponseEntity<Map<String, Object>> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello, World!");
        response.put("timestamp", LocalDateTime.now());
        response.put("endpoint", "/api/hello");
        response.put("rateLimit", "5 requests per minute (1 per 12 seconds)");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/data")
    @RateLimit(tokens = 2, capacity = 10, refillRate = 2.0)
    public ResponseEntity<Map<String, Object>> getData() {
        Map<String, Object> response = new HashMap<>();
        response.put("data", "Some sample data");
        response.put("timestamp", LocalDateTime.now());
        response.put("endpoint", "/api/data");
        response.put("rateLimit", "10 requests per 5 seconds (2 per second)");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit")
    @RateLimit(tokens = 3, capacity = 3, refillRate = 0.5)
    public ResponseEntity<Map<String, Object>> submitData(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Data submitted successfully");
        response.put("receivedData", data);
        response.put("timestamp", LocalDateTime.now());
        response.put("endpoint", "/api/submit");
        response.put("rateLimit", "3 requests per 6 seconds (0.5 per second)");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/profile")
    @RateLimit(key = "#{request.remoteAddr + ':' + userId}", tokens = 1, capacity = 20, refillRate = 5.0)
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("profile", "User profile data for " + userId);
        response.put("timestamp", LocalDateTime.now());
        response.put("endpoint", "/api/user/{userId}/profile");
        response.put("rateLimit", "20 requests per 4 seconds (5 per second) per user");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/stats")
    @RateLimit(key = "admin", tokens = 1, capacity = 100, refillRate = 10.0)
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        Map<String, Object> response = new HashMap<>();
        response.put("activeBuckets", rateLimiter.getBucketCount());
        response.put("timestamp", LocalDateTime.now());
        response.put("endpoint", "/api/admin/stats");
        response.put("rateLimit", "100 requests per 10 seconds (10 per second) for admin");
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/reset")
    @RateLimit(key = "admin", tokens = 1, capacity = 5, refillRate = 0.2)
    public ResponseEntity<Map<String, Object>> resetAllBuckets() {
        rateLimiter.resetAllBuckets();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "All rate limit buckets have been reset");
        response.put("timestamp", LocalDateTime.now());
        response.put("endpoint", "/api/admin/reset");
        response.put("rateLimit", "5 requests per 25 seconds (0.2 per second) for admin");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Rate Limiter API");
        
        return ResponseEntity.ok(response);
    }
}
