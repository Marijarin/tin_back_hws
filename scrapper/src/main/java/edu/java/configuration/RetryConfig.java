package edu.java.configuration;

import edu.java.controller.dto.ApiErrorResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Configuration
@SuppressWarnings("MagicNumber")
public class RetryConfig {

    private final ApplicationConfig applicationConfig;

    private final Map<String, Predicate<Throwable>> errors;

    public RetryConfig(ApplicationConfig applicationConfig, Map<String, Predicate<Throwable>> errors) {
        this.applicationConfig = applicationConfig;
        this.errors = errors;
    }

    final Predicate<Throwable> defaultError =
        (throwable) -> throwable instanceof MethodArgumentNotValidException;

    @Bean
    ExchangeFilterFunction constant() {
        return (request, next) -> next.exchange(request)
            .flatMap(clientResponse -> Mono.just(clientResponse)
                .filter(response -> clientResponse.statusCode().isError())
                .flatMap(response -> clientResponse.toEntity(ApiErrorResponse.class))
                .flatMap(mono -> {
                    var body = Optional.ofNullable(mono.getBody()).orElseThrow();
                    var e = body.exception();
                    return Mono.error(e);
                })
                .thenReturn(clientResponse))
            .retryWhen(Retry.maxInARow(3));
    }

    @Bean
    ExchangeFilterFunction linear() {
        return (request, next) -> next.exchange(request)
            .flatMap(clientResponse -> Mono.just(clientResponse)
                .filter(response -> clientResponse.statusCode().isError())
                .flatMap(response -> clientResponse.toEntity(ApiErrorResponse.class))
                .flatMap(mono -> {
                    var body = Optional.ofNullable(mono.getBody()).orElseThrow();
                    var e = body.exception();
                    return Mono.error(e);
                })
                .thenReturn(clientResponse))
            .retryWhen(Retry.fixedDelay(4, Duration.ofSeconds(2))
                .filter(errors.getOrDefault(applicationConfig.errorFilters().getFirst(), defaultError))
                .filter(errors.getOrDefault(applicationConfig.errorFilters().get(4), defaultError)));
    }

    @Bean
    ExchangeFilterFunction exponential() {
        return (request, next) -> next.exchange(request)
            .flatMap(clientResponse -> Mono.just(clientResponse)
                .filter(response -> clientResponse.statusCode().isError())
                .flatMap(response -> clientResponse.toEntity(ApiErrorResponse.class))
                .flatMap(mono -> {
                    var body = Optional.ofNullable(mono.getBody()).orElseThrow();
                    var e = body.exception();
                    return Mono.error(e);
                })
                .thenReturn(clientResponse))
            .retryWhen(Retry.backoff(4, Duration.ofSeconds(3))
                .filter(errors.getOrDefault(applicationConfig.errorFilters().get(2), defaultError))
                .filter(errors.getOrDefault(applicationConfig.errorFilters().get(3), defaultError)));
    }
}
