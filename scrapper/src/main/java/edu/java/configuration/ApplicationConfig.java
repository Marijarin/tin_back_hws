package edu.java.configuration;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
@ConfigurationPropertiesScan
public record ApplicationConfig(
    @NotNull
    @Bean
    Scheduler scheduler,
    @NotEmpty
    String baseUrlGitHub,
    @NotEmpty
    String baseUrlStackOverflow,
    @NotEmpty
    String baseUrlBot,
    AccessType databaseAccessType,
    Boolean useQueue,
    @NotEmpty
    String typeLinear,
    @NotEmpty
    String typeConstant,

    @NotEmpty

    String typeExponential,

    List<String> errorFilters,

    int filterCode,
    int count,

    int tokens,
    int period

) {
    @Bean
    public DefaultConfigurationCustomizer postgresJooqCustomizer() {
        return (DefaultConfiguration c) -> c.settings()
            .withRenderSchema(false)
            .withRenderFormatted(true)
            .withRenderQuotedNames(RenderQuotedNames.NEVER);
    }

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public enum AccessType {
        JDBC,
        JPA,
        JOOQ
    }

    public record RateLimit(@NotNull int count, int tokens, int period) {
    }
}
