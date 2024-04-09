package edu.java;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import edu.java.service.jdbc.JdbcLinkService;
import edu.java.service.jdbc.JdbcLinkUpdater;
import edu.java.service.model.EventLink;
import edu.java.service.model.EventName;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {JdbcTestConfig.class})
@Sql(value = "classpath:sql/put_chat.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class JdbcLinkUpdaterTest extends IntegrationTest {
    private final JdbcLinkService linkService;
    private final JdbcLinkUpdater linkUpdater;

    private final int offset = 365;

    @Autowired
    public JdbcLinkUpdaterTest(JdbcLinkService linkService, JdbcLinkUpdater linkUpdater) {
        this.linkService = linkService;
        this.linkUpdater = linkUpdater;
    }

    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
        .options(wireMockConfig().port(8080))
        .build();

    @Test
    @Transactional
    @Rollback
    void updatesGHLink() throws IOException {
        var eventList = new ArrayList<EventLink>();
        long chatId = 10000L;
        var linkDao = linkService.add(chatId, URI.create("https://github.com/Marijarin/tocook"));
        wm.stubFor(get(urlPathMatching("/repos/Marijarin/tocook/events"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(Files.readString(Path.of("src/test/resources/json/gh-push.json")))
                .withStatus(200)));
        try {
            linkDao.setLastUpdated(OffsetDateTime.now().minus(Duration.ofDays(offset)));
            eventList = (ArrayList<EventLink>) linkUpdater
                .updateFromGithub(List.of(linkDao));
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        assertThat(eventList.getFirst().getEvent().getDescription())
            .isEqualTo(EventName.getEventMap().get("PushEvent")
                .getDescription());
        wm.verify(getRequestedFor(urlPathMatching("/repos/.*")));
    }

    @Test
    @Transactional
    @Rollback
    void updatesSOFink() throws IOException {
        var eventList = new ArrayList<EventLink>();
        long chatId = 10000L;
        var linkDao = linkService.add(chatId, URI.create("https://stackoverflow.com/questions/1/how-to"));
        wm.stubFor(get(urlPathMatching("/questions/1/timeline.*"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(Files.readString(Path.of("src/test/resources/json/sof-answer.json")))
                .withStatus(200)));
        try {
            linkDao.setLastUpdated(OffsetDateTime.now().minus(Duration.ofDays(offset)));
            eventList = (ArrayList<EventLink>) linkUpdater
                .updateFromStackOverFlow(List.of(linkDao));
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        assertThat(eventList.getFirst().getEvent().getDescription())
            .isEqualTo(EventName.getEventMap().get("answer")
                .getDescription());
        wm.verify(getRequestedFor(urlPathMatching("/questions/1/timeline.*")));
    }

    @Test
    @Transactional
    @Rollback
    void triesToUpdateButListIsEmpty() throws IOException {
        var eventList = new ArrayList<EventLink>();
        long chatId = 10000L;
        var linkDao = linkService.add(chatId, URI.create("https://stackoverflow.com/questions/1/how-to"));
        wm.stubFor(get(urlPathMatching("/questions/1/timeline.*"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(Files.readString(Path.of("src/test/resources/json/empty-sof.json")))
                .withStatus(200)));
        try {
            linkDao.setLastUpdated(OffsetDateTime.now().minus(Duration.ofDays(1)));
            eventList = (ArrayList<EventLink>) linkUpdater
                .updateFromStackOverFlow(List.of(linkDao));
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        assertThat(eventList.isEmpty()).isTrue();
        wm.verify(getRequestedFor(urlPathMatching("/questions/1/timeline.*")));
    }
}
