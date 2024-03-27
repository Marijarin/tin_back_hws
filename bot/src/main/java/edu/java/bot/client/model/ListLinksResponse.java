package edu.java.bot.client.model;

import java.util.List;

public record ListLinksResponse(
    List<LinkResponse> links,
    int size
) {
}
