package edu.java.bot.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI botOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("Bot API")
                .description("Bot for sites updates scrapping")
                .version("1.0.0"));
    }
}

