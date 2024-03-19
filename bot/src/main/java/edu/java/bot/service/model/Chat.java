package edu.java.bot.service.model;

import java.util.List;
import java.util.Set;

public record Chat(
    long id,
    long userId,
    String userName,
    Set<String> links
) {
}
