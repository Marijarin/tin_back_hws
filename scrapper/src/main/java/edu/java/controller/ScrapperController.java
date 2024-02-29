package edu.java.controller;

import edu.java.controller.dto.LinkResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScrapperController {
    Logger logger = LogManager.getLogger();

    @PostMapping("/tg-chat/{id}")
    void registerChat(@PathVariable long id) {
        logger.info(HttpStatus.OK);
    }

    @DeleteMapping("/tg-chat/{id}")
    void deleteChat(@PathVariable long id) {
        logger.info(HttpStatus.OK);
    }

    @GetMapping("/links")
    void getLinks(@RequestHeader("Tg-Chat-Id") long tgChatId) {
        logger.info(HttpStatus.OK);
    }

    @PostMapping("/links")
    LinkResponse startLinkTracking(@RequestHeader("Tg-Chat-Id") long tgChatId, @RequestBody String link) {
        logger.info(HttpStatus.OK + link);
        return new LinkResponse(
            tgChatId,
            link
        );
    }

    @DeleteMapping("/links")
    LinkResponse stopLinkTracking(@RequestHeader("Tg-Chat-Id") long tgChatId, @RequestBody String link) {
        logger.info(HttpStatus.OK + link);
        return new LinkResponse(
            tgChatId,
            link
        );
    }
}
