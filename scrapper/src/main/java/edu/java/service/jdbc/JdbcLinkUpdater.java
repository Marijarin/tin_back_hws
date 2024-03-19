package edu.java.service.jdbc;

import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import edu.java.domain.JDBCLinkDao;
import edu.java.domain.dao.Link;
import edu.java.service.LinkUpdater;
import java.time.OffsetDateTime;
import java.util.ArrayList;
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
    public List<Link> update() {
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

    private List<Link> updateFromGithub(List<Link> gitHubList) {
        var result = new ArrayList<Link>();
        if (!gitHubList.isEmpty()) {
            for (Link link : gitHubList) {
                if (checkOneGitHubLink(link)) {
                    result.add(link);
                }
            }
        }
        return result;
    }

    @SuppressWarnings({"MagicNumber", "MultipleStringLiterals"})
    private boolean checkOneGitHubLink(Link link) {
//        var findOwnerRepoRegexp = "^(.*(github.com/))(?<owner>.*)(/)(?<repo>.*)";
//        Pattern pattern = Pattern.compile(findOwnerRepoRegexp);
//        String s = link.getUri().toString();
//        Matcher matcher = pattern.matcher(s);
//        String owner = matcher.group("owner");
//        String repo = matcher.group("name");
        var sList = link.getUri().toString().split("/");
        String owner = sList[3];
        String repo = sList[4];
        var updateFromSite = gitHubClient.getResponse(owner, repo);
        if (updateFromSite.getFirst().updatedAt().isAfter(link.getLastUpdated())) {
            linkDao.updateLink(link, updateFromSite.getFirst().updatedAt());
            return true;
        }
        return false;
    }

    private List<Link> updateFromStackOverFlow(List<Link> stackOverFlowList) {
        var result = new ArrayList<Link>();
        if (!stackOverFlowList.isEmpty()) {
            for (Link link : stackOverFlowList) {
                if (checkOneStackOverFlowLink(link)) {
                    result.add(link);
                }
            }
        }
        return result;
    }

    private boolean checkOneStackOverFlowLink(Link link) {
        var findIdsRegexp = "^(.*(stackoverflow.com/questions/))(?<ids>.*)(/)(?<name>.*)";
        Pattern pattern = Pattern.compile(findIdsRegexp);
        Matcher matcher = pattern.matcher(link.getUri().toString());
        String ids = matcher.group("ids");
        var updateFromSite = stackOverflowClient.getResponse(ids);
        if (updateFromSite.items().getFirst().creationDate().isAfter(link.getLastUpdated())) {
            linkDao.updateLink(link, updateFromSite.items().getFirst().creationDate());
            return true;
        }
        return false;
    }
}
