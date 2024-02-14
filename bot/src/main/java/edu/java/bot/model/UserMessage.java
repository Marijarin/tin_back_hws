package edu.java.bot.model;

public record UserMessage(
    int id,
    long chatId,
    BotUser from,
    String text
) {
}
