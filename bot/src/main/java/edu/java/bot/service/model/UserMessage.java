package edu.java.bot.service.model;

public record UserMessage(
    int id,
    long chatId,
    BotUser from,
    String text
) {
}
