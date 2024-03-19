package edu.java.client;

import edu.java.client.model.LinkUpdate;
import edu.java.service.ChatService;
import edu.java.service.LinkUpdater;
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
            var linkUpdates = links.stream().map(link -> new LinkUpdate(
                    link.getId(),
                    link.getUri(),
                    link.getDescription(),
                    chatService.findAllChatsWithLink(link.getUri()).stream().map(chat -> chat.getId()).toList()
                )
            ).toList();
            for (LinkUpdate linkUpdate : linkUpdates) {
                botClient.postUpdate(linkUpdate);
            }
        } else {
            logger.info("No updates");
        }
    }
}
