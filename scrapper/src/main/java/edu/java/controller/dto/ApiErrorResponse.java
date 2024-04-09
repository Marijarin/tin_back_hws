package edu.java.controller.dto;

public record ApiErrorResponse(
    String description,
    String code,
    Throwable exception,
    String exceptionMessage,
    String[] stacktrace
) {
}
