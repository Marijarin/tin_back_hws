package edu.java.bot.controller.exception;

import edu.java.bot.controller.dto.ApiErrorResponse;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionApiHandler {
    Logger logger = LogManager.getLogger();

    @SuppressWarnings("MagicNumber")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handleBotControllerError(MethodArgumentNotValidException exception) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Некорректные параметры запроса",
            "400",
            MethodArgumentNotValidException.class.getName(),
            exception.getMessage().split(" ; ")[0].split(":")[1].trim(),
            Arrays.stream(exception.getStackTrace()).map(StackTraceElement::getClassName).limit(5)
                .toArray(String[]::new)
        );
        HttpHeaders headers = new HttpHeaders();
        headers.clear();
        headers.set("Description", apiErrorResponse.description());
        logger.info(apiErrorResponse.toString());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .headers(headers)
            .contentType(MediaType.APPLICATION_JSON)
            .body(apiErrorResponse);
    }
}
