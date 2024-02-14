package edu.java.bot.model;

import edu.java.bot.repository.CommandName;

public record CustomCommand(
    CommandName commandName,
    String description
) {
}
