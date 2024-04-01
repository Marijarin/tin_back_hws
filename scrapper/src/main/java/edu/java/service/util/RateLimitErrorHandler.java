package edu.java.service.util;

import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

public class RateLimitErrorHandler {
    static Logger logger = LogManager.getLogger();

    private RateLimitErrorHandler() {
        throw new RuntimeException();
    }

    public static void handleTooManyError(
        HttpServletResponse response, ConsumptionProbe consumptionProbe
    ) {
        final long waitForRefill = consumptionProbe.getNanosToWaitForRefill() / 1_000_000_000;
        response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));

        handleResponseError(
            response,
            String.format(
                "You have exhausted your API Request Quota, please try again in [%d] seconds.",
                waitForRefill
            )
        );
    }

    private static void handleResponseError(
        HttpServletResponse response, String errorMessage
    ) {
        try {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), errorMessage);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
