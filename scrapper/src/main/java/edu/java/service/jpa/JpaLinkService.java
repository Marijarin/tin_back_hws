package edu.java.service.jpa;

import edu.java.domain.model.LinkDao;
import edu.java.service.LinkService;
import java.net.URI;
import java.util.Collection;

public class JpaLinkService implements LinkService {
    @Override
    public LinkDao add(long tgChatId, URI url) {
        return null;
    }

    @Override
    public LinkDao remove(long tgChatId, URI url) {
        return null;
    }

    @Override
    public long findLinkId(long tgChatId, URI url) {
        return 0;
    }

    @Override
    public Collection<LinkDao> listAll(long tgChatId) {
        return null;
    }
}
