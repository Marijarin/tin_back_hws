package edu.java.bot.service.model;

import edu.java.bot.repository.CommandName;

public record CustomCommand(
    CommandName commandName,
    String description
) {
}
