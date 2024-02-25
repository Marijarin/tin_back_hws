package edu.java.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record ResponseGitHubDTO(
    String id,
    @JsonProperty("created_at") OffsetDateTime createdAt
) {
}
