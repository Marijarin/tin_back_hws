package edu.java.service.jdbc;

import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import edu.java.domain.JDBCLinkDao;
import edu.java.domain.dao.Link;
import edu.java.service.LinkUpdater;
import edu.java.service.model.EventLink;
import edu.java.service.model.EventName;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class JdbcLinkUpdater implements LinkUpdater {
    private final JDBCLinkDao linkDao;
    private final GitHubClient gitHubClient;

    private final StackOverflowClient stackOverflowClient;

    private final Map<String, EventName> eventMap;

    @Autowired
    public JdbcLinkUpdater(JDBCLinkDao linkDao, GitHubClient gitHubClient, StackOverflowClient stackOverflowClient) {
        this.linkDao = linkDao;
        this.gitHubClient = gitHubClient;
        this.stackOverflowClient = stackOverflowClient;
        this.eventMap = EventName.getEventMap();
    }

    @Override
    public List<EventLink> update() {
        var mapOfNotUpdatedYet = classifySavedLinksNotUpdatedYet(1);
        if (!mapOfNotUpdatedYet.isEmpty()) {
            var stackOverFlowList = extractLinksByKeyWord(mapOfNotUpdatedYet, "stackoverflow");
            var gitHubList = extractLinksByKeyWord(mapOfNotUpdatedYet, "github");
            var result = updateFromGithub(gitHubList);
            result.addAll(updateFromStackOverFlow(stackOverFlowList));
            return result;
        }
        return List.of();
    }

    private Map<String, List<Link>> classifySavedLinksNotUpdatedYet(long days) {
        var checkTime = OffsetDateTime.now().minusDays(days);
        var allLinksNotUpdated = linkDao.findAllLinksWithLastUpdateEarlierThan(checkTime);
        return allLinksNotUpdated.stream().collect(groupingBy(Link::getDescription, toList()));
    }

    private List<Link> extractLinksByKeyWord(Map<String, List<Link>> all, String key) {
        var mapKey = all.keySet().stream().filter(it -> it.contains(key)).findFirst().orElseGet(String::new);
        var list = all.get(mapKey);
        if (list != null) {
            return list;
        }
        return List.of();
    }

    private List<EventLink> updateFromGithub(List<Link> gitHubList) {
        var result = new ArrayList<EventLink>();
        if (!gitHubList.isEmpty()) {
            for (Link link : gitHubList) {
                var eventLink = checkOneGitHubLink(link);
                if (eventLink != null && eventLink.getEvent() != null) {
                    result.add(eventLink);
                }
            }
        }
        return result;
    }

    @SuppressWarnings({"MagicNumber", "MultipleStringLiterals"})
    private EventLink checkOneGitHubLink(Link link) {
        var sList = link.getUri().toString().split("/");
        String owner = "";
        String repo = "";
        if (sList.length > 4) {
            owner = sList[3];
            repo = sList[4];
        } else {
            return null;
        }
        var updateFromSite = gitHubClient.getResponse(owner, repo);
        if (!updateFromSite.isEmpty()) {
            if (updateFromSite.getFirst().updatedAt().isAfter(link.getLastUpdated())) {
                var update = updateFromSite.getFirst();
                var linkResult = linkDao.updateLink(link, update.updatedAt());
                String description = decipherEventType(update.eventType());
                var eventResult = linkDao.putEventType(link.getId(), description);
                return new EventLink(linkResult, eventResult);
            }
        }
        return null;
    }

    private List<EventLink> updateFromStackOverFlow(List<Link> stackOverFlowList) {
        var result = new ArrayList<EventLink>();
        if (!stackOverFlowList.isEmpty()) {
            for (Link link : stackOverFlowList) {
                var eventLink = checkOneStackOverFlowLink(link);
                if (eventLink != null && eventLink.getEvent() != null) {
                    result.add(eventLink);
                }
            }
        }
        return result;
    }

    private EventLink checkOneStackOverFlowLink(Link link) {
        String ids = link.getUri().toString().split("stackoverflow.com/questions/")[1].split("/")[0];
        var updateFromSite = stackOverflowClient.getResponse(ids);
        if (updateFromSite != null && updateFromSite.items() != null) {
            if (updateFromSite.items().getFirst().creationDate().isAfter(link.getLastUpdated())) {
                var update = updateFromSite.items().getFirst();
                var linkResult = linkDao.updateLink(link, update.creationDate());
                String description = decipherEventType(update.eventType());
                var eventResult = linkDao.putEventType(link.getId(), description);
                return new EventLink(linkResult, eventResult);
            }
        }
        return null;
    }

    private String decipherEventType(String eventType) {
        if (eventMap.containsKey(eventType)) {
            return eventMap.get(eventType).getDescription();
        } else {
            return "A new unknown event!";
        }
    }
}
