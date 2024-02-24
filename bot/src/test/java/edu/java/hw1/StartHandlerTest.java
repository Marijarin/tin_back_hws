package edu.java.hw1;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.model.Bot;
import edu.java.bot.model.BotUser;
import edu.java.bot.model.Chat;
import edu.java.bot.repository.CommandName;
import edu.java.bot.service.StartHandler;
import edu.java.bot.service.UserMessageHandler;
import edu.java.bot.service.UserMessageHandlerImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class StartHandlerTest {
    @Mock TelegramBot telegramBot = new TelegramBot("12345");

    Update update = mock(Update.class);
    HashMap<BotUser, CommandName> isWaiting = new HashMap<>();
    Message message = mock(Message.class);

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
            "1"

        );
        var handler = new StartHandler(applicationConfig, true);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn(CommandName.START.getCommand());
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);

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
            List.of()
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
            "1"

        );
        var handler = new StartHandler(applicationConfig, true);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn(CommandName.START.getCommand());
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);

        var result = handler.handle(bot, messageHandler, update);

        assertThat(result.getParameters().get("text")).isEqualTo(
            ("1234567")
        );
    }
}
