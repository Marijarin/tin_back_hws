package edu.java.client.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import java.util.List;

public record LinkUpdate(
    @Min(1)
    long id,
    @NotBlank
    URI url,
    String description,
    List<Long> tgChatIds,
    String eventDescription
) {
}
