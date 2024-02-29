package edu.java.controller.dto;

public record ListLinksResponse(
    LinkResponse [] links,
    int size
) {
}
