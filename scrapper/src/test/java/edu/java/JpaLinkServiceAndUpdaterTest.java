package edu.java;

import edu.java.service.jpa.JpaLinkService;
import edu.java.service.jpa.JpaLinkUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JpaLinkServiceAndUpdaterTest extends IntegrationTest {
    @Autowired JpaLinkService linkService;
    @Autowired JpaLinkUpdater linkUpdater;
}
