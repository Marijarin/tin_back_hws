package edu.java.service.jdbc;

import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import edu.java.domain.jdbc.JdbcLinkDao;
import edu.java.domain.model.LinkDao;
import edu.java.service.LinkUpdater;
import edu.java.service.model.EventLink;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class JdbcLinkUpdater implements LinkUpdater {
    private final JdbcLinkDao linkDao;
    private final GitHubClient gitHubClient;

    private final StackOverflowClient stackOverflowClient;

    @Autowired
    public JdbcLinkUpdater(JdbcLinkDao linkDao, GitHubClient gitHubClient, StackOverflowClient stackOverflowClient) {
        this.linkDao = linkDao;
        this.gitHubClient = gitHubClient;
        this.stackOverflowClient = stackOverflowClient;
    }

    @Override
    @Transactional
    public Map<String, List<LinkDao>> classifySavedLinksNotUpdatedYet(long days) {
        var checkTime = OffsetDateTime.now().minusDays(days);
        var allLinksNotUpdated = linkDao.findAllLinksWithLastUpdateEarlierThan(checkTime);
        return allLinksNotUpdated.stream().collect(groupingBy(LinkDao::getDescription, toList()));
    }

    @SuppressWarnings({"MagicNumber", "MultipleStringLiterals"})
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
                var linkResult = linkDao.updateLink(link, update.updatedAt());
                String description = decipherEventType(update.eventType());
                var eventResult = linkDao.putEventType(link.getId(), description);
                return new EventLink(linkResult, eventResult);
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
                var linkResult = linkDao.updateLink(link, update.creationDate());
                String description = decipherEventType(update.eventType());
                var eventResult = linkDao.putEventType(link.getId(), description);
                return new EventLink(linkResult, eventResult);
            }
        }
        return null;
    }
}
