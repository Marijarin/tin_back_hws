package edu.java.service.jpa;

import edu.java.domain.model.LinkDao;
import edu.java.service.LinkUpdater;
import edu.java.service.model.EventLink;
import java.util.List;

public class JpaLinkUpdater implements LinkUpdater {
    @Override
    public List<EventLink> update() {
        return null;
    }

    @Override
    public EventLink checkOneGitHubLink(LinkDao link) {
        return null;
    }

    @Override
    public EventLink checkOneStackOverFlowLink(LinkDao link) {
        return null;
    }
}
