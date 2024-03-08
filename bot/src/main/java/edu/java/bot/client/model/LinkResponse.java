package edu.java.bot.client.model;

import java.net.URI;

public record LinkResponse(
    long id,
    URI url
) {
}
