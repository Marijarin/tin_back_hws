package edu.java.bot.model;

import edu.java.bot.enums.CommandName;

public record CustomCommand(
    CommandName commandName,
    String description
) {
}
