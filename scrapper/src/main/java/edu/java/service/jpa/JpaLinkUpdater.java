package edu.java.service.jpa;

import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import edu.java.domain.jpa.JpaEventRepository;
import edu.java.domain.jpa.JpaLinkDao;
import edu.java.domain.jpa.entity.EventEntity;
import edu.java.domain.jpa.entity.LinkEntity;
import edu.java.domain.model.EventDao;
import edu.java.domain.model.LinkDao;
import edu.java.service.LinkUpdater;
import edu.java.service.model.EventLink;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class JpaLinkUpdater implements LinkUpdater {

    private final JpaLinkDao linkDao;

    private final JpaEventRepository eventRepository;
    private final GitHubClient gitHubClient;

    private final StackOverflowClient stackOverflowClient;

   @Autowired
    public JpaLinkUpdater(
        JpaLinkDao linkDao,
        JpaEventRepository eventRepository,
        GitHubClient gitHubClient,
        StackOverflowClient stackOverflowClient
    ) {
        this.linkDao = linkDao;
        this.eventRepository = eventRepository;
        this.gitHubClient = gitHubClient;
        this.stackOverflowClient = stackOverflowClient;
    }

    @Override
    @Transactional
    public EventLink checkOneGitHubLink(LinkDao link) {
        var list = getParametersForGitHubRequest(link.getUri().toString());
        var owner = list.getFirst();
        var repo = list.getLast();
        var updateFromSite = gitHubClient.getResponse(owner, repo);
        if (!updateFromSite.isEmpty()) {
            if (updateFromSite.getFirst().updatedAt().isAfter(link.getLastUpdated())) {
                var update = updateFromSite.getFirst();
                var linkRequest = linkDao.findByUrl(link.getUri().toString());
                linkRequest.setLastUpdated(update.updatedAt());
                var result = linkDao.save(linkRequest);
                String description = decipherEventType(update.eventType());
                var eventResult = eventRepository.save(makeEntity(link, description));
                return new EventLink(mapFromEntity(result), mapFromEntity(eventResult));
            }
        }
        return null;
    }

    @Override
    @Transactional
    public EventLink checkOneStackOverFlowLink(LinkDao link) {
        String ids = getIdsForSOFRequest(link.getUri().toString());
        var updateFromSite = stackOverflowClient.getResponse(ids);
        if (updateFromSite != null && updateFromSite.items() != null) {
            if (updateFromSite.items().getFirst().creationDate().isAfter(link.getLastUpdated())) {
                var update = updateFromSite.items().getFirst();
                var linkRequest = linkDao.findByUrl(link.getUri().toString());
                linkRequest.setLastUpdated(update.creationDate());
                var result = linkDao.save(linkRequest);
                String description = decipherEventType(update.eventType());
                var eventResult = eventRepository.save(makeEntity(link, description));
                return new EventLink(mapFromEntity(result), mapFromEntity(eventResult));
            }
        }
        return null;
    }

    @Override
    @Transactional
    public Map<String, List<LinkDao>> classifySavedLinksNotUpdatedYet(long days) {
        var checkTime = OffsetDateTime.now().minusDays(days);
        var allLinksNotUpdated = linkDao.findLinkEntitiesByLastUpdatedBefore(checkTime);
        return allLinksNotUpdated.stream()
            .map(this::mapFromEntity)
            .collect(groupingBy(LinkDao::getDescription, toList()));
    }

    private LinkEntity mapFromLinkDao(LinkDao link) {
        var entity = new LinkEntity();
        entity.setId(link.getId());
        entity.setUrl(link.getUri().toString());
        entity.setDescription(link.getDescription());
        entity.setLastUpdated(link.getLastUpdated());
        return entity;
    }

    private LinkDao mapFromEntity(LinkEntity entity) {
        return new LinkDao(
            entity.getId(),
            URI.create(entity.getUrl()),
            entity.getDescription(),
            entity.getLastUpdated()
        );
    }

    private EventDao mapFromEntity(EventEntity entity) {
        return new EventDao(
            entity.getType(),
            entity.getLink().getId()
        );
    }

    private EventEntity makeEntity(LinkDao link, String description) {
        var entity = new EventEntity();
        entity.setLink(mapFromLinkDao(link));
        entity.setType(description);
        return entity;
    }
}
