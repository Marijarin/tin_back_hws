package edu.java.service;

import edu.java.model.ResponseGitHubDTO;
import edu.java.model.ResponseStackOverflowDTO;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface GitHubService {
    @GetExchange("/notifications/threads/{thread_id}")
    ResponseStackOverflowDTO getResponse(@PathVariable String thread_id);
}
