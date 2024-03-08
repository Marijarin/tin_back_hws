package edu.java.bot.service.model;

public record BotUser(
    long id,
    long chatId,
    String name,
    boolean isBot
) {
}
