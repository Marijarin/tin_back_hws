package edu.java.service;

import edu.java.domain.model.LinkDao;
import edu.java.service.model.EventLink;
import edu.java.service.model.EventName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface LinkUpdater {
    Map<String, EventName> eventMap = EventName.getEventMap();
    List<EventLink> update();

    EventLink checkOneGitHubLink(LinkDao link);
    EventLink checkOneStackOverFlowLink(LinkDao link);
    default List<LinkDao> extractLinksByKeyWord(Map<String, List<LinkDao>> all, String key) {
        var mapKey = all.keySet().stream().filter(it -> it.contains(key)).findFirst().orElseGet(String::new);
        var list = all.get(mapKey);
        if (list != null) {
            return list;
        }
        return List.of();
    }
    default  String decipherEventType(String eventType) {
        if (eventMap.containsKey(eventType)) {
            return eventMap.get(eventType).getDescription();
        } else {
            return "A new unknown event!";
        }
    }
    default  List<EventLink> updateFromGithub(List<LinkDao> gitHubList) {
        var result = new ArrayList<EventLink>();
        if (!gitHubList.isEmpty()) {
            for (LinkDao link : gitHubList) {
                var eventLink = checkOneGitHubLink(link);
                if (eventLink != null && eventLink.getEvent() != null) {
                    result.add(eventLink);
                }
            }
        }
        return result;
    }
    default List<EventLink> updateFromStackOverFlow(List<LinkDao> stackOverFlowList) {
        var result = new ArrayList<EventLink>();
        if (!stackOverFlowList.isEmpty()) {
            for (LinkDao link : stackOverFlowList) {
                var eventLink = checkOneStackOverFlowLink(link);
                if (eventLink != null && eventLink.getEvent() != null) {
                    result.add(eventLink);
                }
            }
        }
        return result;
    }
}
