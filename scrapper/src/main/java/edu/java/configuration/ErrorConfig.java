package edu.java.configuration;

import java.util.function.Predicate;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Configuration
public class ErrorConfig {
    @Bean
    Predicate<Throwable> e400() {
        return (throwable) -> throwable instanceof MethodArgumentNotValidException;
    }

    @Bean
    Predicate<Throwable> e404NotFound() {
        return (throwable) -> throwable instanceof ResourceNotFoundException;
    }

    @Bean
    Predicate<Throwable> e500() {
        return (throwable) -> throwable instanceof NullPointerException;
    }

    @Bean
    Predicate<Throwable> e409() {
        return (throwable) -> throwable instanceof DataIntegrityViolationException;
    }

}
