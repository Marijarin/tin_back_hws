package edu.java.service;

import edu.java.model.ResponseGitHubDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import java.util.List;

public interface GitHubClient {
    @GetExchange("/repos/{owner}/{repo}/events")
    List<ResponseGitHubDTO> getResponse(@PathVariable String owner, @PathVariable String repo);
}
