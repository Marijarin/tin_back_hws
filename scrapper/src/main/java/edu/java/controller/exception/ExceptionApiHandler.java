package edu.java.controller.exception;

import edu.java.controller.dto.ApiErrorResponse;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@SuppressWarnings({"MultipleStringLiterals", "MagicNumber"})
@RestControllerAdvice
public class ExceptionApiHandler {
    Logger logger = LogManager.getLogger();

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ApiErrorResponse handle400(MethodArgumentNotValidException exception) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Некорректные параметры запроса",
            "400",
            MethodArgumentNotValidException.class.getName(),
            exception.getMessage().split(" ; ")[0].split(":")[1].trim(),
            Arrays.stream(exception.getStackTrace()).map(StackTraceElement::getClassName).limit(5)
                .toArray(String[]::new)
        );
        logger.info(apiErrorResponse.toString());
        return apiErrorResponse;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handle404(RuntimeException exception) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Чат не существует",
            "404",
            RuntimeException.class.getName(),
            exception.getMessage().split(" ; ")[0].split(":")[1].trim(),
            Arrays.stream(exception.getStackTrace()).map(StackTraceElement::getClassName).limit(5)
                .toArray(String[]::new)
        );
        logger.info(apiErrorResponse.toString());
        return apiErrorResponse;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handle500(WebClientResponseException exception) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Link is missing",
            "500",
            RuntimeException.class.getName(),
            exception.getMessage().split(" ; ")[0].split(":")[1].trim(),
            Arrays.stream(exception.getStackTrace())
                .map(StackTraceElement::getClassName).limit(5)
                .toArray(String[]::new)
        );
        logger.info(apiErrorResponse.toString());
        return apiErrorResponse;
    }
}
