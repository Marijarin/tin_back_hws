package edu.java.service.jdbc;

import edu.java.domain.JDBCLinkDao;
import edu.java.domain.dao.Link;
import edu.java.service.LinkService;
import java.net.URI;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JdbcLinkService implements LinkService {

    private final JDBCLinkDao linkRepository;

    @Autowired
    public JdbcLinkService(JDBCLinkDao linkRepository) {
        this.linkRepository = linkRepository;
    }

    @Override
    @Transactional
    public Link add(long tgChatId, URI url) {
        return linkRepository.addLink(tgChatId, url);
    }

    @Override
    @Transactional
    public Link remove(long tgChatId, URI url) {
        return linkRepository.deleteLink(tgChatId, url);
    }

    @Override
    public long findLinkId(long tgChatId, URI url) {
        return linkRepository.findByUrlAndChat(tgChatId, url);
    }

    @Override
    @Transactional
    public Collection<Link> listAll(long tgChatId) {
        return linkRepository.findAllLinksFromChat(tgChatId);
    }
}
