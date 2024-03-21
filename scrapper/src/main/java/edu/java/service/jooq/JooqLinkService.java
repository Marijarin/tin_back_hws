package edu.java.service.jooq;

import edu.java.domain.model.LinkDao;
import edu.java.scrapper.domain.jooq.dao.JooqLinkDao;
import edu.java.service.LinkService;
import java.net.URI;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JooqLinkService implements LinkService {
    private final JooqLinkDao linkRepository;

    @Autowired
    public JooqLinkService(JooqLinkDao linkRepository) {
        this.linkRepository = linkRepository;
    }

    @Override
    @Transactional
    public LinkDao add(long tgChatId, URI url) {
        return linkRepository.addLink(tgChatId, url);
    }

    @Override
    @Transactional
    public LinkDao remove(long tgChatId, URI url) {
        return linkRepository.deleteLink(tgChatId, url);
    }

    @Override
    public long findLinkId(long tgChatId, URI url) {
        return linkRepository.findByUrl(url).getId();
    }

    @Override
    @Transactional
    public Collection<LinkDao> listAll(long tgChatId) {
        return linkRepository.findAllLinksFromChat(tgChatId);
    }
}
