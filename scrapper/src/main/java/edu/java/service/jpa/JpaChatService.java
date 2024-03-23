package edu.java.service.jpa;

import edu.java.domain.jpa.JpaChatRepository;
import edu.java.domain.jpa.entity.ChatEntity;
import edu.java.domain.jpa.entity.LinkEntity;
import edu.java.domain.model.ChatDao;
import edu.java.service.ChatService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaChatService implements ChatService {
    private final JpaChatRepository chatRepository;

 @Autowired
    public JpaChatService(JpaChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    @Transactional
    public void register(long tgChatId) {
        var chat = new ChatEntity();
        chat.setCreatedAt(OffsetDateTime.now());
        if (tgChatId > 0) {
            chat.setId(tgChatId);
            chatRepository.save(chat);
        }
    }

    @Override
    @Transactional
    public ChatDao findChatById(long tgChatId) {
        var entity = chatRepository.findChatEntityById(tgChatId);
        if (entity != null) {
            return new ChatDao(entity.getId(), entity.getCreatedAt(), List.of());
        } else {
            return new ChatDao(0L, OffsetDateTime.now(), List.of());
        }
    }

    @Override
    @Transactional
    public void unregister(long tgChatId) {
        chatRepository.deleteChatEntityById(tgChatId);
    }

    @Override
    @Transactional
    public List<ChatDao> findAllChatsWithLink(URI url) {
        //var link = linkDao.findByUrl(url.toString());
        var chats = chatRepository
            .findAll()
            .stream()
            .filter(chatEntity -> chatEntity.getLinks().stream().map(LinkEntity::getUrl).toList()
                .contains(url.toString()));
        return chats
            .map(chatEntity -> new ChatDao(chatEntity.getId(), chatEntity.getCreatedAt(), List.of()))
            .toList();
    }
}
