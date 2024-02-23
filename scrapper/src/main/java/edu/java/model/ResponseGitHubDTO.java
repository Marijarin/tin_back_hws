package edu.java.model;

import java.time.OffsetDateTime;

public record ResponseGitHubDTO(
    String id,
    GitHubRepo repo,
    OffsetDateTime created_at
) {
}
