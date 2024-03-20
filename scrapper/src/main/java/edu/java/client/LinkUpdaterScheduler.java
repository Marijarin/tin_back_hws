package edu.java.client;

import edu.java.client.model.LinkUpdate;
import edu.java.domain.dao.Chat;
import edu.java.service.ChatService;
import edu.java.service.LinkUpdater;
import edu.java.service.model.EventLink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LinkUpdaterScheduler {
    Logger logger = LogManager.getLogger();

    private final BotClient botClient;

    private final LinkUpdater linkUpdater;

    private final ChatService chatService;

    @Autowired
    public LinkUpdaterScheduler(
        BotClient botClient,
        LinkUpdater linkUpdater, ChatService chatService
    ) {
        this.botClient = botClient;
        this.linkUpdater = linkUpdater;
        this.chatService = chatService;
    }

    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    void update() {
        var links = linkUpdater.update();
        if (!links.isEmpty()) {
            var linkUpdates = links.stream().map(this::makeItFromLink).toList();
            for (LinkUpdate linkUpdate : linkUpdates) {
                botClient.postUpdate(linkUpdate);
            }
        } else {
            logger.info("No updates");
        }
    }

    private LinkUpdate makeItFromLink(EventLink eventLink) {
        return new LinkUpdate(
            eventLink.getLink().getId(),
            eventLink.getLink().getUri(),
            eventLink.getEvent().getDescription(),
            chatService.findAllChatsWithLink(eventLink.getLink().getUri()).stream().map(Chat::getId).toList(),
            eventLink.getEvent().getDescription()
        );
    }
}
