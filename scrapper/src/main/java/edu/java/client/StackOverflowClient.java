package edu.java.client;

import edu.java.client.site_dto.ResponseStackOverflowDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface StackOverflowClient {
    @GetExchange("/questions/{ids}/timeline?site=stackoverflow")
    ResponseStackOverflowDTO getResponse(@PathVariable String ids);
}
