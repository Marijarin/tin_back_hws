package edu.java.bot.repository;

import edu.java.bot.model.CustomCommand;

public enum CommandName {
    START("/start"),
    HELP("/help"),
    TRACK("/track"),
    UNTRACK("/untrack"),
    LIST("/list");

    private final String s;

    CommandName(String s) {
        this.s = s;
    }

    public String getCommand() {
        return s;
    }

    private CustomCommand[] addDescriptions() {
        CustomCommand[] descriptedCommands = new CustomCommand[CommandName.values().length];
        for (CommandName c : CommandName.values()) {
            descriptedCommands[c.ordinal()] = switch (c) {
                case START -> new CustomCommand(START, "Register to track links");
                case HELP -> new CustomCommand(HELP, "Look up available commands");
                case TRACK -> new CustomCommand(
                    TRACK,
                    "Request to wait for a link to track, is followed by a message containing a link"
                );
                case UNTRACK -> new CustomCommand(
                    UNTRACK,
                    "Request to wait for a link to untrack, is followed by a message containing a link"
                );
                case LIST -> new CustomCommand(LIST, "Receive a list of tracked links");
            };
        }
        return descriptedCommands;
    }

    public CustomCommand[] getCommandsWithDescriptions() {
        return addDescriptions();
    }

}
