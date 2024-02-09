package edu.java.bot.service;

import edu.java.bot.model.CustomCommand;

public interface CommandHandler {
    CustomCommand command();
    String description();

}
