package edu.java.service.jdbc;

import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import edu.java.domain.JDBCLinkDao;
import edu.java.domain.dao.Link;
import edu.java.service.LinkUpdater;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class JdbcLinkUpdater implements LinkUpdater {
    private final JDBCLinkDao linkDao;
    private final GitHubClient gitHubClient;

    private final StackOverflowClient stackOverflowClient;

    @Autowired
    public JdbcLinkUpdater(JDBCLinkDao linkDao, GitHubClient gitHubClient, StackOverflowClient stackOverflowClient) {
        this.linkDao = linkDao;
        this.gitHubClient = gitHubClient;
        this.stackOverflowClient = stackOverflowClient;
    }

    @Override
    public int update() {
        var mapOfNotUpdatedYet = classifySavedLinksNotUpdatedYet(1);
        var stackOverFlowList = extractLinksByKeyWord(mapOfNotUpdatedYet, "stackoverflow");
        var gitHubList = extractLinksByKeyWord(mapOfNotUpdatedYet, "github");

        return updateFromGithub(gitHubList) + updateFromStackOverFlow(stackOverFlowList);
    }

    private Map<String, List<Link>> classifySavedLinksNotUpdatedYet(long days) {
        var checkTime = OffsetDateTime.now().minusDays(days);
        var allLinksNotUpdated = linkDao.findAllLinksWithLastUpdateEarlierThan(checkTime);
        return allLinksNotUpdated.stream().collect(groupingBy(Link::getDescription, toList()));
    }

    private List<Link> extractLinksByKeyWord(Map<String, List<Link>> all, String key) {
        var mapKey = all.keySet().stream().filter(it -> it.contains(key)).findFirst().orElseThrow();
        var list = all.get(mapKey);
        if (list != null) {
            return list;
        }
        return List.of();
    }

    private int updateFromGithub(List<Link> gitHubList) {
        int sum = 0;
        if (!gitHubList.isEmpty()) {
            for (Link link : gitHubList) {
                sum += checkOneGitHubLink(link);
            }
        }
        return sum;
    }

    private int checkOneGitHubLink(Link link) {
        var findOwnerRepoRegexp = "^(.*(github.com/))(?<owner>.*)(/)(?<repo>.*)";
        Pattern pattern = Pattern.compile(findOwnerRepoRegexp);
        Matcher matcher = pattern.matcher(link.getUri().toString());
        String owner = matcher.group("owner");
        String repo = matcher.group("name");
        var updateFromSite = gitHubClient.getResponse(owner, repo);
        if (updateFromSite.getFirst().updatedAt().isAfter(link.getLastUpdated())) {
            linkDao.updateLink(link, updateFromSite.getFirst().updatedAt());
            return 1;
        }
        return 0;
    }

    private int updateFromStackOverFlow(List<Link> stackOverFlowList) {
        int sum = 0;
        if (!stackOverFlowList.isEmpty()) {
            for (Link link : stackOverFlowList) {
                sum += checkOneStackOverFlowLink(link);
            }
        }
        return sum;
    }

    private int checkOneStackOverFlowLink(Link link) {
        var findIdsRegexp = "^(.*(stackoverflow.com/questions/))(?<ids>.*)(/)(?<name>.*)";
        Pattern pattern = Pattern.compile(findIdsRegexp);
        Matcher matcher = pattern.matcher(link.getUri().toString());
        String ids = matcher.group("ids");
        var updateFromSite = stackOverflowClient.getResponse(ids);
        if (updateFromSite.items().getFirst().creationDate().isAfter(link.getLastUpdated())) {
            linkDao.updateLink(link, updateFromSite.items().getFirst().creationDate());
            return 1;
        }
        return 0;
    }
}
