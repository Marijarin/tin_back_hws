package edu.java.service.jpa;

import edu.java.domain.model.ChatDao;
import edu.java.service.ChatService;
import java.net.URI;
import java.util.List;

public class JpaChatService implements ChatService {
    @Override
    public void register(long tgChatId) {

    }

    @Override
    public ChatDao findChatById(long tgChatId) {
        return null;
    }

    @Override
    public void unregister(long tgChatId) {

    }

    @Override
    public List<ChatDao> findAllChatsWithLink(URI url) {
        return null;
    }
}
