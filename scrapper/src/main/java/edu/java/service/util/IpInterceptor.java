package edu.java.service.util;

import edu.java.service.RateLimiterService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class IpInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;

    public IpInterceptor(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public boolean preHandle(
        HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object object
    ) {
        final String ip = request.getRemoteAddr();
        final Bucket tokenBucket = rateLimiterService.resolveBucketByIp(ip);
        final ConsumptionProbe consumptionProbe = tokenBucket.tryConsumeAndReturnRemaining(1);
        if (consumptionProbe.isConsumed()) {
            response.addHeader(
                "X-Rate-Limit-Remaining", String.valueOf(consumptionProbe.getRemainingTokens()));
            return true;
        }
        RateLimitErrorHandler.handleTooManyError(response, consumptionProbe);
        return false;
    }
}
