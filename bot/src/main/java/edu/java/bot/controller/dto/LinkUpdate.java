package edu.java.bot.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record LinkUpdate(
    @Min(1)
    long id,
    @NotNull
    URI url,
    String description,
    @NotEmpty
    long[] tgChatIds
) {
}
