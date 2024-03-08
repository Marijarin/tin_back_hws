package edu.java.bot.controller;

import edu.java.bot.controller.dto.LinkUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotController {

    Logger logger = LogManager.getLogger();

    @Operation(
        summary = "Отправить обновление",
        responses = {
            @ApiResponse(responseCode = "200", description = "Обновление обработано")
        })
    @PostMapping("/updates")
    void postUpdate(@RequestBody @Valid LinkUpdate linkUpdate) {
        logger.info(HttpStatus.OK);
    }
}
