package edu.java.service.jdbc;

import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import edu.java.domain.jdbc.JDBCLinkDao;
import edu.java.domain.model.LinkDao;
import edu.java.service.LinkUpdater;
import edu.java.service.model.EventLink;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

//@Service
public class JdbcLinkUpdater implements LinkUpdater {
    private final JDBCLinkDao linkDao;
    private final GitHubClient gitHubClient;

    private final StackOverflowClient stackOverflowClient;

    //@Autowired
    public JdbcLinkUpdater(JDBCLinkDao linkDao, GitHubClient gitHubClient, StackOverflowClient stackOverflowClient) {
        this.linkDao = linkDao;
        this.gitHubClient = gitHubClient;
        this.stackOverflowClient = stackOverflowClient;
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

    private Map<String, List<LinkDao>> classifySavedLinksNotUpdatedYet(long days) {
        var checkTime = OffsetDateTime.now().minusDays(days);
        var allLinksNotUpdated = linkDao.findAllLinksWithLastUpdateEarlierThan(checkTime);
        return allLinksNotUpdated.stream().collect(groupingBy(LinkDao::getDescription, toList()));
    }

    @SuppressWarnings({"MagicNumber", "MultipleStringLiterals"})
    public EventLink checkOneGitHubLink(LinkDao link) {
        var sList = link.getUri().toString().split("/");
        String owner;
        String repo;
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

    public EventLink checkOneStackOverFlowLink(LinkDao link) {
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
}
