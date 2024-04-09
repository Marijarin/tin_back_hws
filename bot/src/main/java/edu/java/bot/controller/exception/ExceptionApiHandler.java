package edu.java.bot.controller.exception;

import edu.java.bot.controller.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handleBotControllerError(MethodArgumentNotValidException exception) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Некорректные параметры запроса",
            "400",
            exception,
            exception.getMessage().split(" ; ")[0].split(":")[1].trim(),
            Arrays.stream(exception.getStackTrace()).map(StackTraceElement::getClassName).limit(5)
                .toArray(String[]::new)
        );

        logger.info(apiErrorResponse.toString());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(apiErrorResponse);
    }
}
