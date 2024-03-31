package edu.java.controller.exception;

import edu.java.controller.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Arrays;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@SuppressWarnings({"MultipleStringLiterals", "MagicNumber"})
@RestControllerAdvice
public class ExceptionApiHandler {
    Logger logger = LogManager.getLogger();

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrorResponse handle400(MethodArgumentNotValidException exception) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Некорректные параметры запроса",
            "400",
            exception,
            exception.getMessage().trim(),
            Arrays.stream(exception.getStackTrace()).map(StackTraceElement::getClassName).limit(5)
                .toArray(String[]::new)
        );
        logger.info(apiErrorResponse.toString());
        return apiErrorResponse;
    }

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ApiResponse(responseCode = "429", description = "Слишком много запросов")
    @ExceptionHandler(HttpClientErrorException.class)
    public ApiErrorResponse handle429(HttpClientErrorException exception) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Слишком много запросов",
            "429",
            exception,
            exception.getMessage().trim(),
            Arrays.stream(exception.getStackTrace()).map(StackTraceElement::getClassName).limit(5)
                .toArray(String[]::new)
        );
        logger.info(apiErrorResponse.toString());
        return apiErrorResponse;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(responseCode = "404", description = "Чат не существует")
    @ExceptionHandler({ResourceNotFoundException.class, org.springframework.dao.EmptyResultDataAccessException.class})
    public ApiErrorResponse handle404(ResourceNotFoundException exception) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Объект не существует",
            "404",
            exception,
            exception.getMessage().trim(),
            Arrays.stream(exception.getStackTrace()).map(StackTraceElement::getClassName).limit(5)
                .toArray(String[]::new)
        );
        logger.info(apiErrorResponse.toString());
        return apiErrorResponse;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(responseCode = "500", description = "Ошибка на сервере при обработке данных")
    @ExceptionHandler(NullPointerException.class)
    public ApiErrorResponse handle500(NullPointerException exception) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Ошибка на сервере при обработке данных",
            "500",
            exception,
            exception.getMessage().trim(),
            Arrays.stream(exception.getStackTrace())
                .map(StackTraceElement::getClassName).limit(5)
                .toArray(String[]::new)
        );
        logger.info(apiErrorResponse.toString());
        return apiErrorResponse;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ApiResponse(responseCode = "409", description = "Конфликт при обработке данных")
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ApiErrorResponse handle409(DataIntegrityViolationException exception) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Конфликт при обработке данных",
            "409",
            exception,
            exception.getMessage().split(" ; ")[0].split(":")[1].trim(),
            Arrays.stream(exception.getStackTrace())
                .map(StackTraceElement::getClassName).limit(5)
                .toArray(String[]::new)
        );
        logger.info(apiErrorResponse.toString());
        return apiErrorResponse;
    }
}
