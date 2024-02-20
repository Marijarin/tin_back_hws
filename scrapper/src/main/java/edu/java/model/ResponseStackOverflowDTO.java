package edu.java.model;

import java.time.OffsetDateTime;

public record ResponseStackOverflowDTO(
    String url,
    OffsetDateTime lastUpdated
) {
}
