package edu.java.hw1;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.client.model.ChatResponse;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.repository.CommandName;
import edu.java.bot.service.StartHandler;
import edu.java.bot.service.UserMessageHandler;
import edu.java.bot.service.UserMessageHandlerImpl;
import edu.java.bot.service.model.Bot;
import edu.java.bot.service.model.BotUser;
import edu.java.bot.service.model.Chat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class StartHandlerTest {
    @Mock TelegramBot telegramBot = new TelegramBot("12345");

    @Mock Update update = new Update();

    @Mock Message message = new Message();
    @Mock ScrapperClient scrapperClient;
    HashMap<BotUser, CommandName> isWaiting = new HashMap<>();
    User user = new User(1L);

    UserMessageHandler messageHandler = new UserMessageHandlerImpl();
    com.pengrad.telegrambot.model.Chat chat = mock(com.pengrad.telegrambot.model.Chat.class);
    BotUser botUser = new BotUser(1L, 1L, null, true);

    @Test
    void registerNewUser() {
        Bot bot = new Bot(
            telegramBot,
            new HashMap<>(),
            new HashMap<>()
        );
        ApplicationConfig applicationConfig = new ApplicationConfig(
            "12345",
            "1",
            "Registered! Hello, dear ",
            "123123",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            "",
            "",
            "",
            "",
            "",
            "",
            List.of(),
            0

        );
        var handler = new StartHandler(applicationConfig, scrapperClient);
        var response = new ChatResponse(0L);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);
        Mockito.when(scrapperClient.findChat(message.chat().id())).thenReturn(response);

        var result = handler.handle(bot, messageHandler, update);

        assertThat(result.getParameters().get("text")).isEqualTo(
            ("Registered! Hello, dear " + botUser.name())
        );
    }

    @Test
    void saysIsAlreadyRegistered() {
        Chat chat1 = new Chat(
            botUser.chatId(),
            botUser.id(),
            botUser.name(),
            new HashSet<>()
        );
        isWaiting.put(botUser, null);
        Bot bot = new Bot(
            telegramBot,
            Map.of(botUser, chat1),
            isWaiting
        );
        ApplicationConfig applicationConfig = new ApplicationConfig(
            "12345",
            "1",
            "1",
            "1234567",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            "",
            "",
            "",
            "",
            "",
            "",
            List.of(),
            0

        );
        var handler = new StartHandler(applicationConfig, scrapperClient);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);

        var result = handler.handle(bot, messageHandler, update);

        assertThat(result.getParameters().get("text")).isEqualTo(
            ("1234567")
        );
    }
}
