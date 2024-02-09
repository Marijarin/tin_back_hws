package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import edu.java.bot.configuration.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationConfig.class)
public class BotApplication {
    public static void main(String[] args){
        SpringApplication.run(BotApplication.class, args);

        TelegramBot bot = new TelegramBot("6367840695:AAEBTsTW2TYB585R3MEm6cNdA1YD9XMOLR8");
        bot.setUpdatesListener(updates-> {
            updates.forEach(System.out::println);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
