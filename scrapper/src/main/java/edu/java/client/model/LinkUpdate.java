package edu.java.client.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record LinkUpdate(
    @Min(1)
    long id,
    @NotBlank
    String url,
    String description,
    @NotEmpty
    long[] tgChatIds
) {
}
