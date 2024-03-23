package edu.java.service.jdbc;

import edu.java.domain.jdbc.JDBCLinkDao;
import edu.java.domain.model.LinkDao;
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
    public LinkDao add(long tgChatId, URI url) {
        return linkRepository.addLink(tgChatId, url);
    }

    @Override
    @Transactional
    public void remove(long tgChatId, URI url) {
        linkRepository.deleteLink(tgChatId, url);
    }


    @Override
    @Transactional
    public Collection<LinkDao> listAll(long tgChatId) {
        return linkRepository.findAllLinksFromChat(tgChatId);
    }
}
