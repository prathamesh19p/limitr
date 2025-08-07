# Rate Limiter Spring Boot Application

A simple and effective rate limiter implementation using Spring Boot with Token Bucket algorithm. This project demonstrates how to implement rate limiting in a Spring Boot application with configurable limits for different endpoints.

## Features

- **Token Bucket Algorithm**: Efficient rate limiting with configurable capacity and refill rates
- **Annotation-based**: Simple `@RateLimit` annotation to protect endpoints
- **Flexible Key Generation**: Support for IP-based, user-based, or custom rate limiting keys
- **HTTP 429 Responses**: Proper error handling with Retry-After headers
- **Thread-safe**: ConcurrentHashMap-based implementation for high concurrency

## Technology Stack

- **Spring Boot 3.2.0**
- **Java 17**
- **Spring AOP** for aspect-oriented programming
- **Spring Expression Language (SpEL)** for dynamic key resolution
- **Spring Actuator** for monitoring
- **Maven** for dependency management

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone and navigate to the project directory:**
   ```bash
   cd rate-limiter
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application:**
   - Application will start on `http://localhost:8080`

## API Endpoints

### 1. Hello Endpoint
- **URL**: `GET /api/hello`
- **Rate Limit**: 5 requests per minute (1 per 12 seconds)
- **Description**: Simple hello world endpoint

### 2. Data Endpoint
- **URL**: `GET /api/data`
- **Rate Limit**: 10 requests per 5 seconds (2 per second)
- **Description**: Returns sample data

### 3. Submit Endpoint
- **URL**: `POST /api/submit`
- **Rate Limit**: 3 requests per 6 seconds (0.5 per second)
- **Description**: Accepts JSON data and returns confirmation

### 4. User Profile Endpoint
- **URL**: `GET /api/user/{userId}/profile`
- **Rate Limit**: 20 requests per 4 seconds (5 per second) per user
- **Description**: User-specific rate limiting based on IP + userId

### 5. Admin Stats Endpoint
- **URL**: `GET /api/admin/stats`
- **Rate Limit**: 100 requests per 10 seconds (10 per second) for admin
- **Description**: Returns current rate limiter statistics

### 6. Admin Reset Endpoint
- **URL**: `DELETE /api/admin/reset`
- **Rate Limit**: 5 requests per 25 seconds (0.2 per second) for admin
- **Description**: Resets all rate limit buckets

## Rate Limiting Configuration

### @RateLimit Annotation Parameters

```java
@RateLimit(
    key = "#{request.remoteAddr}",     // Rate limiting key (SpEL expression)
    tokens = 1,                        // Tokens consumed per request
    capacity = 10,                     // Maximum tokens in bucket
    refillRate = 1.0,                  // Tokens refilled per second
    message = "Custom error message"    // Custom error message
)
```

### Key Examples

- `"#{request.remoteAddr}"` - Rate limit by IP address
- `"#{request.remoteAddr + ':' + userId}"` - Rate limit by IP + user ID
- `"admin"` - Fixed key for admin operations
- `"#{#user.id}"` - Rate limit by authenticated user ID

### Using Postman or similar tools

1. Import the following collection or create requests manually
2. Send multiple requests in quick succession to see rate limiting in action
3. Check the response headers for `Retry-After` when rate limited

## Response Examples

### Successful Response
```json
{
  "message": "Hello, World!",
  "timestamp": "2024-01-15T10:30:45.123",
  "endpoint": "/api/hello",
  "rateLimit": "5 requests per minute (1 per 12 seconds)"
}
```

### Rate Limit Exceeded Response (HTTP 429)
```json
{
  "timestamp": "2024-01-15T10:30:46.456",
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Please try again later.",
  "path": "/api/hello"
}
```

## Architecture

### Core Components

1. **TokenBucketRateLimiter**: Implements the token bucket algorithm
2. **@RateLimit**: Custom annotation for marking rate-limited endpoints
3. **GlobalExceptionHandler**: Handles rate limit exceptions globally

### Token Bucket Algorithm

- Each client gets a "bucket" with a maximum capacity
- Tokens are consumed for each request
- Tokens are refilled at a constant rate over time
- Requests are allowed if tokens are available, rejected otherwise

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is open source and available under the MIT License.
