package edu.java.bot.client;

import edu.java.bot.client.model.AddLinkRequest;
import edu.java.bot.client.model.ChatResponse;
import edu.java.bot.client.model.LinkResponse;
import edu.java.bot.client.model.ListLinksResponse;
import edu.java.bot.client.model.RemoveLinkRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface ScrapperClient {
    @PostExchange("/tg-chat/{id}")
    void registerChat(@PathVariable long id);

    @DeleteExchange("/tg-chat/{id}")
    void deleteChat(@PathVariable long id);
    @GetExchange("/tg-chat/{id}")
    ChatResponse findChat(@PathVariable long id);

    @GetExchange("/links")
    ListLinksResponse getLinksFromTG(@RequestHeader("Tg-Chat-Id") long tgChatId);

    @PostExchange("/links")
    LinkResponse startLinkTracking(@RequestHeader("Tg-Chat-Id") long tgChatId, @RequestBody AddLinkRequest linkRequest);

    @DeleteExchange("/links")
    LinkResponse stopLinkTracking(
        @RequestHeader("Tg-Chat-Id") long tgChatId,
        @RequestBody RemoveLinkRequest linkRequest
    );
}
