package edu.java.client.site_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record ResponseGitHubDTO(
    String id,
    @JsonProperty("created_at") OffsetDateTime updatedAt
) {
}
