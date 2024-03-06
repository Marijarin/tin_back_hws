package edu.java;

import edu.java.dao.ChatRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.OffsetDateTime;

@Configuration
public class TestConfig {
    @Bean
    Long id(){
        return 1L;
    }
}
