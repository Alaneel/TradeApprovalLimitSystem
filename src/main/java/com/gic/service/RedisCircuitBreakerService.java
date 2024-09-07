package com.gic.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
public class RedisCircuitBreakerService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final CircuitBreaker circuitBreaker;

    public RedisCircuitBreakerService() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .ringBufferSizeInHalfOpenState(2)
                .ringBufferSizeInClosedState(2)
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        circuitBreaker = registry.circuitBreaker("redisCircuitBreaker");
    }

    public Object executeWithCircuitBreaker(String key, Supplier<Object> redisOperation, Supplier<Object> fallback) {
        Supplier<Object> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, redisOperation);

        return Try.ofSupplier(decoratedSupplier)
                .recover(throwable -> fallback.get())
                .get();
    }

    public Object get(String key) {
        return executeWithCircuitBreaker(
                key,
                () -> redisTemplate.opsForValue().get(key),
                () -> {
                    // Fallback logic, e.g., fetch from database
                    return null;
                }
        );
    }

    public void set(String key, Object value) {
        executeWithCircuitBreaker(
                key,
                () -> {
                    redisTemplate.opsForValue().set(key, value);
                    return null;
                },
                () -> {
                    // Fallback logic, e.g., log the failure
                    return null;
                }
        );
    }
}