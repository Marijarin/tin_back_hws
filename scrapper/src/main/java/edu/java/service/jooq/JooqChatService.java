package edu.java.service.jooq;

import edu.java.domain.jooq.JooqChatRepository;
import edu.java.domain.model.ChatDao;
import edu.java.service.ChatService;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//@Service
public class JooqChatService implements ChatService {
    private final JooqChatRepository chatRepository;

   // @Autowired
    public JooqChatService(JooqChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    @Transactional
    public void register(long tgChatId) {
        chatRepository.addChat(tgChatId);
    }

    @Override
    @Transactional
    public void unregister(long tgChatId) {
        chatRepository.deleteChat(tgChatId);
    }

    @Override
    @Transactional
    public ChatDao findChatById(long tgChatId) {
        return chatRepository.findChat(tgChatId);
    }

    @Override
    @Transactional
    public List<ChatDao> findAllChatsWithLink(URI url) {
        return chatRepository.findAllChatsWithLink(url);
    }
}
