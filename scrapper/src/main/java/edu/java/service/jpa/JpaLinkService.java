package edu.java.service.jpa;

import edu.java.domain.jpa.JpaChatRepository;
import edu.java.domain.jpa.JpaLinkDao;
import edu.java.domain.jpa.entity.ChatEntity;
import edu.java.domain.jpa.entity.LinkEntity;
import edu.java.domain.model.LinkDao;
import edu.java.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//@Service
public class JpaLinkService implements LinkService {
    private final JpaLinkDao linkDao;

    private final JpaChatRepository chatRepository;

  //  @Autowired
    public JpaLinkService(JpaLinkDao linkDao, JpaChatRepository chatRepository) {
        this.linkDao = linkDao;
        this.chatRepository = chatRepository;
    }

    @Override
    @Transactional
    public LinkDao add(long tgChatId, URI url) {
        var entity = new LinkEntity();
        entity.setUrl(url.toString());
        entity.setLastUpdated(OffsetDateTime.now());
        entity.setDescription(url.getHost());
        entity.getChats().add(chatRepository.findChatEntityById(tgChatId));
        linkDao.save(entity);
        var entitySaved = linkDao.findByUrl(String.valueOf(url));
        return new LinkDao(
            entitySaved.getId(),
            URI.create(entitySaved.getUrl()),
            entitySaved.getDescription()
        );

    }

    @Override
    @Transactional
    public void remove(long tgChatId, URI url) {
        var entitySaved = linkDao.findByUrl(String.valueOf(url));
        linkDao.deleteByUrl(url.toString());
        new LinkDao(
            entitySaved.getId(),
            URI.create(entitySaved.getUrl()),
            entitySaved.getDescription()
        );
    }

    @Override
    @Transactional
    public Collection<LinkDao> listAll(long tgChatId) {
       // var chat = chatRepository.findChatEntityById(tgChatId);
        var links = linkDao.findAll()
            .stream()
            .filter(linkEntity -> linkEntity.getChats().stream().map(ChatEntity::getId).toList().contains(tgChatId));
        return links
            .map(link -> new LinkDao(link.getId(), URI.create(link.getUrl()), link.getDescription()))
            .toList();
    }
}
