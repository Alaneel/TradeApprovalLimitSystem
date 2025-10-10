package com.gic.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            // Add custom health check logic here
            // For example, check if critical services are available
            int errorCode = check();
            if (errorCode != 0) {
                return Health.down()
                        .withDetail("Error Code", errorCode)
                        .build();
            }
            return Health.up()
                    .withDetail("status", "Trading System is running")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }

    private int check() {
        // Implement actual health check logic
        return 0;
    }
}
