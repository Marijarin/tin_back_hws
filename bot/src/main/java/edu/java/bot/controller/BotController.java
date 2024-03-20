package edu.java.bot.controller;

import edu.java.bot.controller.dto.LinkUpdate;
import edu.java.bot.service.PenBot;
import edu.java.bot.service.model.SendUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotController {

    private final PenBot penBot;

    Logger logger = LogManager.getLogger();

    @Autowired
    public BotController(PenBot penBot) {
        this.penBot = penBot;
    }

    @Operation(
        summary = "Отправить обновление",
        responses = {
            @ApiResponse(responseCode = "200", description = "Обновление обработано")
        })
    @PostMapping("/updates")
    void postUpdate(@RequestBody @Valid LinkUpdate linkUpdate) {
        var sendUpdate = new SendUpdate(
            linkUpdate.url(),
            linkUpdate.description(),
            linkUpdate.tgChatIds(),
            linkUpdate.description());
        penBot.processUpdateFromScrapper(sendUpdate);
        logger.info(linkUpdate.url());
    }
}
