package edu.java.service.jdbc;

import edu.java.domain.JDBCChatRepository;
import edu.java.domain.dao.Chat;
import edu.java.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JdbcChatService implements ChatService {
    private final JDBCChatRepository chatRepository;

    @Autowired
    public JdbcChatService(JDBCChatRepository chatRepository) {
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
    public Chat findChatById(long tgChatId) {
        return chatRepository.findChat(tgChatId);
    }
}
