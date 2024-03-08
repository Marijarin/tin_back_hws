package edu.java.controller;

import edu.java.controller.dto.AddLinkRequest;
import edu.java.controller.dto.LinkResponse;
import edu.java.controller.dto.ListLinksResponse;
import edu.java.controller.dto.RemoveLinkRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Operation(
        summary = "Зарегистрировать чат",
        responses = {
            @ApiResponse(responseCode = "200", description = "Чат зарегистрирован")
        })
    @PostMapping("/tg-chat/{id}")
    void registerChat(@PathVariable long id) {
        if (!chatIds.contains(id)) {
            chatIds.add(id);
            links.put(id, new ArrayList<>());
            logger.info(HttpStatus.OK);
        } else {
            throw new DataIntegrityViolationException("Chat already present");
        }
    }

    @Operation(
        summary = "Удалить чат",
        responses = {
            @ApiResponse(responseCode = "200", description = "Чат успешно удалён")
        })
    @DeleteMapping("/tg-chat/{id}")
    void deleteChat(@PathVariable long id) {
        if (chatIds.contains(id)) {
            chatIds.remove(id);
            links.remove(id);
            logger.info(HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("Not found");
        }
    }

    @Operation(
        summary = "Получить все отслеживаемые ссылки",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ссылки успешно получены")
        })
    @GetMapping("/links")
    ListLinksResponse getLinks(@RequestHeader("Tg-Chat-Id") long tgChatId) {
        if (links.get(tgChatId) == null) {
            throw new ResourceNotFoundException("Not found");
        }
        ListLinksResponse listLinksResponse = new ListLinksResponse(links.get(tgChatId), links.get(tgChatId).size());
        logger.info(HttpStatus.OK);
        return listLinksResponse;
    }

    @Operation(
        summary = "Добавить отслеживание ссылки",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена")
        })
    @PostMapping("/links")
    ResponseEntity<LinkResponse> startLinkTracking(
        @RequestHeader("Tg-Chat-Id") long tgChatId,
        @RequestBody @Valid AddLinkRequest linkRequest
    ) {
        LinkResponse linkResponse = new LinkResponse(tgChatId, linkRequest.link());
        if (links.get(linkResponse.id()).contains(linkResponse)) {
            throw new DataIntegrityViolationException("Link already present");
        } else {
            return ResponseEntity.of(Optional.of(linkResponse));
        }
    }

    @Operation(
        summary = "Убрать отслеживание ссылки",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ссылка успешно убрана")
        })
    @DeleteMapping("/links")
    ResponseEntity<LinkResponse> stopLinkTracking(
        @RequestHeader("Tg-Chat-Id") long tgChatId,
        @RequestBody @Valid RemoveLinkRequest linkRequest
    ) {
        LinkResponse linkResponse = new LinkResponse(tgChatId, linkRequest.link());
        if (links.get(linkResponse.id()).remove(linkResponse)) {
            return ResponseEntity.of(Optional.of(linkResponse));
        } else {
            throw new ResourceNotFoundException("Not found");
        }
    }
}
