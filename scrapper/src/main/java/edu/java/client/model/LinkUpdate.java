package edu.java.client.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.net.URI;

public record LinkUpdate(
    @Min(1)
    long id,
    @NotBlank
    URI url,
    String description,
    @NotEmpty
    long[] tgChatIds
) {
}
