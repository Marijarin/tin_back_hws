package edu.java.controller.dto;

import java.net.URI;

public record LinkResponse(
    long id,
    URI url
) {
}
