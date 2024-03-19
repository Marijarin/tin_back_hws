package edu.java.service;

import edu.java.domain.dao.Chat;
import java.net.URI;
import java.util.List;

public interface ChatService {
    void register(long tgChatId);

    Chat findChatById(long tgChatId);

    void unregister(long tgChatId);

    List<Chat> findAllChatsWithLink(URI url);
}
