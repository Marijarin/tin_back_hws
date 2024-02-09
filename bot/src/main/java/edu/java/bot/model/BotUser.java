package edu.java.bot.model;

public record BotUser(
    int id,
    int chatId,
    String name,
    boolean isBot
) {
}
