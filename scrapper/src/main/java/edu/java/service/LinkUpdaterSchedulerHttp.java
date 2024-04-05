package edu.java.service;

import edu.java.client.BotClient;
import edu.java.client.model.LinkUpdate;
import edu.java.configuration.ApplicationConfig;
import edu.java.domain.model.ChatDao;
import edu.java.service.model.EventLink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LinkUpdaterSchedulerHttp implements LinkUpdaterScheduler {
    Logger logger = LogManager.getLogger();

    private final BotClient botClient;

    private final LinkUpdater linkUpdater;

    private final ChatService chatService;
    private final ApplicationConfig applicationConfig;

    @Autowired
    public LinkUpdaterSchedulerHttp(
            BotClient botClient,
            LinkUpdater linkUpdater, ChatService chatService, ApplicationConfig applicationConfig
    ) {
        this.botClient = botClient;
        this.linkUpdater = linkUpdater;
        this.chatService = chatService;
        this.applicationConfig = applicationConfig;
    }

    @Override
    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        if (!applicationConfig.useQueue()) {
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
    }

    private LinkUpdate makeItFromLink(EventLink eventLink) {
        return new LinkUpdate(
            eventLink.getLink().getId(),
            eventLink.getLink().getUri(),
            eventLink.getEvent().getDescription(),
            chatService.findAllChatsWithLink(eventLink.getLink().getUri()).stream().map(ChatDao::getId).toList(),
            eventLink.getEvent().getDescription()
        );
    }
}
