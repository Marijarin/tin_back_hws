package edu.java.service;

import edu.java.domain.model.LinkDao;
import java.net.URI;
import java.util.Collection;

public interface LinkService {
    LinkDao add(long tgChatId, URI url);

    LinkDao remove(long tgChatId, URI url);

    long findLinkId(long tgChatId, URI url);

    Collection<LinkDao> listAll(long tgChatId);
}
