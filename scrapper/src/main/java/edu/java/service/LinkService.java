package edu.java.service;

import edu.java.domain.model.LinkDao;
import java.net.URI;
import java.util.Collection;

public interface LinkService {
    LinkDao add(long tgChatId, URI url);

    void remove(long tgChatId, URI url);

    Collection<LinkDao> listAll(long tgChatId);
}
