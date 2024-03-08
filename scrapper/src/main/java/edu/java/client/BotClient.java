package edu.java.client;

import edu.java.client.model.LinkUpdate;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface BotClient {
    @PostExchange(url = "/updates", accept = "application/json")
    ResponseEntity<Void> postUpdate(@RequestBody @Valid LinkUpdate linkUpdate);
}
