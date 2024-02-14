package edu.java.bot.model;

public record BotUser(
    long id,
    long chatId,
    String name,
    boolean isBot
) {
}
