package edu.java.bot.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reactivestreams.Publisher;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;


public class CustomRetry  extends Retry {
    final int maxAttempt;
    final int coefficient;
    final Duration startDelay;

    private final Logger logger = LogManager.getLogger();

    public CustomRetry(int maxAttempt, int coefficient, Duration startDelay) {
        this.maxAttempt = maxAttempt;
        this.coefficient = coefficient;
        this.startDelay = startDelay;
    }
    @Override
    public Publisher<?> generateCompanion(Flux<RetrySignal> flux) {
        return flux.flatMap(this::getRetry);
    }
    private Mono<Long> getRetry(Retry.RetrySignal rs) {
        if (rs.totalRetries() < maxAttempt) {
            Duration delay;
            if (rs.totalRetries() == 0) {
               delay = Duration.ofSeconds(0);
            } else {
                delay = startDelay
                    .multipliedBy(rs.totalRetries() * coefficient);
            }
            logger.error("retry {} with backoff {}sec", rs.totalRetries(), delay.toSeconds());
            return Mono.delay(delay)
                .thenReturn(rs.totalRetries());
        } else {
            throw Exceptions.propagate(rs.failure());
        }
    }
}
