package edu.java.bot.service.model;

import java.util.List;

public record Chat(
    long id,
    long userId,
    String userName,
    List<String> links
) {
}
