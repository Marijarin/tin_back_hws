package edu.java.bot.client;

import edu.java.bot.client.model.AddLinkRequest;
import edu.java.bot.client.model.LinkResponse;
import edu.java.bot.client.model.RemoveLinkRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface ScrapperClient {
    @PostExchange("/tg-chat/{id}")
    ResponseEntity<Void> registerChat(@PathVariable long id);

    @DeleteExchange("/tg-chat/{id}")
    ResponseEntity<Void> deleteChat(@PathVariable long id);

    @GetExchange("/links")
    ResponseEntity<Void> getLinksFromTG(@RequestHeader("Tg-Chat-Id") long tgChatId);

    @PostExchange("/links")
    LinkResponse startLinkTracking(@RequestHeader("Tg-Chat-Id") long tgChatId, @RequestBody AddLinkRequest linkRequest);

    @DeleteExchange("/links")
    LinkResponse stopLinkTracking(
        @RequestHeader("Tg-Chat-Id") long tgChatId,
        @RequestBody RemoveLinkRequest linkRequest
    );

}
