package edu.java.bot.client.model;

public record ListLinksResponse(
    LinkResponse [] links,
    int size
) {
}
