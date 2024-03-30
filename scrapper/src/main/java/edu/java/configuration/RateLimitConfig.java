package edu.java.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {
    private final ApplicationConfig applicationConfig;

    public RateLimitConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Bean
    ApplicationConfig.ReadWriteLimit readWriteLimit() {
        return new ApplicationConfig.ReadWriteLimit(applicationConfig.read(), applicationConfig.write());
    }
}
