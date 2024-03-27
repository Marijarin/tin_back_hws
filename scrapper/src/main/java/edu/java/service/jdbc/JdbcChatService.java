package edu.java.service.jdbc;

import edu.java.domain.jdbc.JdbcChatRepository;
import edu.java.domain.model.ChatDao;
import edu.java.service.ChatService;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JdbcChatService implements ChatService {
    private final JdbcChatRepository chatRepository;

    @Autowired
    public JdbcChatService(JdbcChatRepository chatRepository) {
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
