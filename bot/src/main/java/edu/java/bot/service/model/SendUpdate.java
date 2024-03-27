package edu.java.bot.service.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record SendUpdate(
    @NotNull
    URI url,
    String description,
    @NotEmpty
    long[] tgChatIds
) {
}
