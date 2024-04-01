package edu.java.unit;

import edu.java.controller.ScrapperController;
import edu.java.domain.model.ChatDao;
import edu.java.domain.model.LinkDao;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import edu.java.service.RateLimiterService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ScrapperController.class)
public class RateLimiterTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    RateLimiterService rateLimiterService;

    @MockBean ChatService chatService;
    @MockBean LinkService linkService;

    @Test
    void returns429WhenManyRequestsToGetChat() throws Exception {
        Bandwidth bw = Bandwidth.classic(
            1, Refill.greedy(
                1,
                Duration.ofHours(1)
            ));
        Bucket bucket = Bucket.builder().addLimit(bw).build();
        Mockito.when(rateLimiterService.resolveBucketByIp("123")).thenReturn(bucket);
        Mockito.when(chatService.findChatById(1)).thenReturn(new ChatDao(1L, OffsetDateTime.now(), List.of()));
        mvc.perform(get("/tg-chat/1").with(remoteAddr("123")))
            .andExpect(status().isOk());
        mvc.perform(get("/tg-chat/1").with(remoteAddr("123"))).andExpect(status().is4xxClientError());
    }

//    @Test
//    void returns429WhenManyRequestsToGetLink() throws Exception {
//        Bandwidth bw = Bandwidth.classic(
//            1, Refill.greedy(
//                1,
//                Duration.ofHours(1)
//            ));
//        Bucket bucket = Bucket.builder().addLimit(bw).build();
//        Mockito.when(rateLimiterService.resolveBucketByIp("1234")).thenReturn(bucket);
//        URI uri = URI.create("https://www.youtube.com/");
//        Mockito.when(linkService.add(1, uri)).thenReturn(new LinkDao(1L, uri, uri.getHost()));
//        Map<String, String> headers = Map.of("Tg-Chat-Id", "1");
//            HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setAll(headers);
//        mvc.perform(get("/links").with(remoteAddr("1234"))
//                .header(Objects.requireNonNull(httpHeaders.getFirst("Tg-Chat-Id"))))
//            .andExpect(status().isOk());
//        mvc.perform(get("/links").with(remoteAddr("1234"))
//                .header(httpHeaders.getFirst("Tg-Chat-Id")))
//            .andExpect(status().is4xxClientError());
//    }
//
//    private static RequestPostProcessor remoteAddr(final String remoteAddr) {
//        return new RequestPostProcessor() {
//            @Override
//            public @NotNull MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
//                request.setRemoteAddr(remoteAddr);
//                return request;
//            }
//        };
//    }

}
