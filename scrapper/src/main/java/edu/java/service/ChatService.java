package edu.java.service;

import edu.java.domain.model.ChatDao;
import java.net.URI;
import java.util.List;

public interface ChatService {
    void register(long tgChatId);

    ChatDao findChatById(long tgChatId);

    void unregister(long tgChatId);

    List<ChatDao> findAllChatsWithLink(URI url);
}
