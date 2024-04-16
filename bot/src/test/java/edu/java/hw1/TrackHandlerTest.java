package edu.java.hw1;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.client.model.ChatResponse;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.repository.CommandName;
import edu.java.bot.service.TrackHandler;
import edu.java.bot.service.UserMessageHandler;
import edu.java.bot.service.UserMessageHandlerImpl;
import edu.java.bot.service.model.Bot;
import edu.java.bot.service.model.BotUser;
import edu.java.bot.service.model.Chat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class TrackHandlerTest {
    @Mock TelegramBot telegramBot = new TelegramBot("12345");
    HashMap<BotUser, CommandName> isWaiting = new HashMap<>();

    @Mock Update update = new Update();

    @Mock Message message = new Message();

    @Mock ScrapperClient scrapperClient;

    @Mock ApplicationConfig applicationConfig;

    User user = new User(1L);

    UserMessageHandler messageHandler = new UserMessageHandlerImpl();
    com.pengrad.telegrambot.model.Chat chat = mock(com.pengrad.telegrambot.model.Chat.class);
    BotUser botUser = new BotUser(1L, 1L, null, true);

    @Test
    void waitsForALink() {
        Chat chat1 = new Chat(
            botUser.chatId(),
            botUser.id(),
            botUser.name(),
            new HashSet<>()
        );
        chat1.links().add("https://stackoverflow.com/search?q=unsupported%20link");
        isWaiting.put(botUser, null);
        var bot = new Bot(
            telegramBot,
            Map.of(botUser, chat1),
            isWaiting
        );
        var handler = new TrackHandler(applicationConfig, scrapperClient);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);
        Mockito.when(applicationConfig.sendLink()).thenReturn("ttt");
        var result = handler.handle(bot, messageHandler, update);

        assertThat(result.getParameters().get("text")).isEqualTo("ttt");
    }

    @Test
    void asksToRegister() {
        Bot bot = new Bot(
            telegramBot,
            new HashMap<>(),
            new HashMap<>()
        );
        var handler = new TrackHandler(applicationConfig, scrapperClient);
        var response = new ChatResponse(0L);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);
        Mockito.when(scrapperClient.findChat(message.chat().id())).thenReturn(response);
        Mockito.when(applicationConfig.register()).thenReturn("aa");
        var result = handler.handle(bot, messageHandler, update);

        assertThat(result.getParameters().get("text")).isEqualTo("aa");
    }
}
