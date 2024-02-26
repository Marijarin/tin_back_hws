package edu.java.service;

import edu.java.model.ResponseStackOverflowDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface StackOverflowClient {
    @GetExchange("/questions/{ids}/timeline?site=stackoverflow")
    ResponseStackOverflowDTO getResponse(@PathVariable String ids);
}
