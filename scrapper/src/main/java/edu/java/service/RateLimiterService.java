package edu.java.service;

import edu.java.configuration.ApplicationConfig.ReadWriteLimit;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {
    private final static int TOKEN_COUNT = 1;

    private final Bucket readBucket;
    private final Bucket writeBucket;

    @Autowired
    public RateLimiterService(ReadWriteLimit readWriteLimit) {
        Bandwidth readLimit = Bandwidth.classic(
            readWriteLimit.read().count(), Refill.greedy(
                readWriteLimit.read().tokens(),
                Duration.ofSeconds(readWriteLimit.read().period())
            ));
        Bandwidth writeLimit = Bandwidth.classic(
            readWriteLimit.write().count(), Refill.greedy(
                readWriteLimit.write().tokens(),
                Duration.ofSeconds(readWriteLimit.write().period())
            ));
        this.readBucket = Bucket.builder()
            .addLimit(readLimit)
            .build();
        this.writeBucket = Bucket.builder()
            .addLimit(writeLimit)
            .build();
    }

    public boolean isNotLimitedRead() {
        return readBucket.tryConsume(TOKEN_COUNT);
    }

    public boolean isNotLimitedWrite() {
        return writeBucket.tryConsume(TOKEN_COUNT);
    }
}
