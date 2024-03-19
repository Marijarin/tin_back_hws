package edu.java.configuration;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "spring.datasource", ignoreUnknownFields = false)
public record DataBaseConfig(
    @NotNull
    String url,
    @NotNull
    String username,
    @NotNull
    String password,
    @NotNull
    String driverClassName
) {
}
