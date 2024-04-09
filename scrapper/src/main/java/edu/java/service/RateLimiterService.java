package edu.java.service;

import edu.java.configuration.ApplicationConfig;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    private final ApplicationConfig applicationConfig;

    private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();

    @Autowired
    public RateLimiterService(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public Bucket resolveBucketByIp(String ipAddress) {
        return ipBuckets.computeIfAbsent(ipAddress, s -> newBucket());
    }

    private Bucket newBucket() {
        Bandwidth bw = Bandwidth.classic(
            applicationConfig.count(), Refill.greedy(
                applicationConfig.tokens(),
                Duration.ofSeconds(applicationConfig.period())
            ));
        return Bucket.builder().addLimit(bw).build();
    }
}
