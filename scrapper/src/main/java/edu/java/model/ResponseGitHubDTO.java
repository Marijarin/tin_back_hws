package edu.java.model;

import java.time.OffsetDateTime;

public record ResponseGitHubDTO(
    String url,
    OffsetDateTime lastUpdated
) {
}
