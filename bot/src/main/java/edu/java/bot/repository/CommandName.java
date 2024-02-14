package edu.java.bot.repository;

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

}
