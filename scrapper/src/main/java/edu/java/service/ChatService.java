package edu.java.service;

import edu.java.domain.dao.Chat;

public interface ChatService {
    void register(long tgChatId);

    Chat findChatById(long tgChatId);

    void unregister(long tgChatId);
}
