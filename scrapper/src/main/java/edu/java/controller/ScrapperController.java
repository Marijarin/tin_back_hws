package edu.java.controller;

import edu.java.controller.dto.AddLinkRequest;
import edu.java.controller.dto.LinkResponse;
import edu.java.controller.dto.ListLinksResponse;
import edu.java.controller.dto.RemoveLinkRequest;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("MultipleStringLiterals")
@RestController
public class ScrapperController {
    Logger logger = LogManager.getLogger();
    List<Long> chatIds = new ArrayList<>();
    Map<Long, List<LinkResponse>> links = new HashMap<>();

    @PostMapping("/tg-chat/{id}")
    ResponseEntity<String> registerChat(@PathVariable long id) {
        if (chatIds.contains(id)) {
            logger.info(HttpStatus.ALREADY_REPORTED);
            return ResponseEntity
                .status(HttpStatus.ALREADY_REPORTED)
                .header("Description", "Already present")
                .build();
        } else {
            chatIds.add(id);
            links.put(id, new ArrayList<>());
            logger.info(HttpStatus.OK);
            return ResponseEntity
                .status(HttpStatus.OK)
                .header("Description", "Chat registered")
                .build();
        }
    }

    @DeleteMapping("/tg-chat/{id}")
    void deleteChat(@PathVariable long id) {
        logger.info(HttpStatus.OK);
    }

    @GetMapping("/links")
    ListLinksResponse getLinks(@RequestHeader("Tg-Chat-Id") long tgChatId) {
        ListLinksResponse listLinksResponse = new ListLinksResponse(links.get(tgChatId), links.get(tgChatId).size());
        logger.info(HttpStatus.OK);
        return listLinksResponse;
    }

    @PostMapping("/links")
    ResponseEntity<LinkResponse> startLinkTracking(
        @RequestHeader("Tg-Chat-Id") long tgChatId,
        @RequestBody @Valid AddLinkRequest linkRequest
    ) {
        LinkResponse linkResponse = new LinkResponse(tgChatId, linkRequest.link());
        ResponseEntity<LinkResponse> result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        try {
            result = handleStartTracking(linkResponse);
        } catch (NullPointerException npe) {
            logger.info("NullPointerException when chat is missing. Chat id sent: " + tgChatId);
        }
        return result;
    }

    @DeleteMapping("/links")
    ResponseEntity<LinkResponse> stopLinkTracking(
        @RequestHeader("Tg-Chat-Id") long tgChatId,
        @RequestBody @Valid RemoveLinkRequest linkRequest
    ) {
        LinkResponse linkResponse = new LinkResponse(tgChatId, linkRequest.link());
        ResponseEntity<LinkResponse> result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        try {
            result = handleStopTracking(linkResponse);
        } catch (NullPointerException npe) {
            logger.info("NullPointerException when tried to track missing link");
        }
        return result;
    }

    private ResponseEntity<LinkResponse> handleStartTracking(LinkResponse linkResponse) {
        if (links.get(linkResponse.id()).contains(linkResponse)) {
            logger.info(HttpStatus.ALREADY_REPORTED);
            return ResponseEntity
                .status(HttpStatus.ALREADY_REPORTED)
                .header("Description", "Already present")
                .build();
        } else {
            links.get(linkResponse.id()).add(linkResponse);
            logger.info(HttpStatus.OK);
            return ResponseEntity
                .status(HttpStatus.OK)
                .header("Description", "Chat registered")
                .body(linkResponse);
        }
    }

    private ResponseEntity<LinkResponse> handleStopTracking(LinkResponse linkResponse) {
        if (links.get(linkResponse.id()).remove(linkResponse)) {
            logger.info(HttpStatus.OK);
            return ResponseEntity
                .status(HttpStatus.OK)
                .header("Description", "Chat registered")
                .body(linkResponse);
        } else {
            logger.info(HttpStatus.NOT_FOUND + linkResponse.url().toString());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header("Description", "Link is missing")
                .body(linkResponse);
        }
    }
}
