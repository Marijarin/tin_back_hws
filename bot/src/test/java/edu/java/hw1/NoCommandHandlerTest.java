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
import edu.java.bot.service.NoCommandHandler;
import edu.java.bot.service.UserMessageHandler;
import edu.java.bot.service.UserMessageHandlerImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class NoCommandHandlerTest {
    @Mock TelegramBot telegramBot = new TelegramBot("12345");
    HashMap<BotUser, CommandName> isWaiting = new HashMap<>();

    Update update = mock(Update.class);

    Message message = mock(Message.class);

    User user = new User(1L);

    UserMessageHandler messageHandler = new UserMessageHandlerImpl();
    com.pengrad.telegrambot.model.Chat chat = mock(com.pengrad.telegrambot.model.Chat.class);
    BotUser botUser = new BotUser(1L, 1L, null, true);

    ApplicationConfig applicationConfig = new ApplicationConfig(
        "12345",
        "aa",
        "1",
        "2",
        "sorry",
        "3",
        "7",
        "8",
        "4 ",
        "^(https?://){1}([\\w\\Q$-_+!*'(),%\\E]+\\.)+(\\w{2,63})(:\\d{1,4})?([\\w\\Q/$-_+!*'(),%\\E]+\\.?[\\w\\Q$-_+!*'(),%\\E={0-5}?&.])*/?$",
        "6"

    );

    @Test
    void processesUnAuthorized() {
        Bot bot = new Bot(
            telegramBot,
            Map.of(),
            Map.of()
        );
        var handler = new NoCommandHandler(applicationConfig, true);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn(CommandName.LIST.getCommand());
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);

        var result = handler.handle(bot, messageHandler, update);

        assertThat(result.getParameters().get("text")).isEqualTo(
            ("sorry")
        );
    }

    @Test
    void startsTracking() {
        Chat chat1 = new Chat(
            botUser.chatId(),
            botUser.id(),
            botUser.name(),
            new ArrayList<>()
        );
        isWaiting.put(botUser, CommandName.TRACK);
        Bot bot = new Bot(
            telegramBot,
            Map.of(botUser, chat1),
            isWaiting
        );
        var handler = new NoCommandHandler(applicationConfig, true);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("https://stackoverflow.com/search?q=unsupported%20link");
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);

        var result = handler.handle(bot, messageHandler, update);

        assertThat(result.getParameters().get("text")).isEqualTo(
            ("4 https://stackoverflow.com/search?q=unsupported%20link")
        );
    }

    @Test
    void startsUnTracking() {
        Chat chat1 = new Chat(
            botUser.chatId(),
            botUser.id(),
            botUser.name(),
            new ArrayList<>()
        );
        chat1.links().add("https://stackoverflow.com/search?q=unsupported%20link");
        isWaiting.put(botUser, CommandName.UNTRACK);
        Bot bot = new Bot(
            telegramBot,
            Map.of(botUser, chat1),
            isWaiting
        );
        var handler = new NoCommandHandler(applicationConfig, true);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("https://stackoverflow.com/search?q=unsupported%20link");
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);

        var result = handler.handle(bot, messageHandler, update);

        assertThat(result.getParameters().get("text")).isEqualTo(
            ("4 []")
        );
        assertThat(chat1.links()).isEqualTo(new ArrayList<>());
    }

    @Test
    void badLink() {
        Chat chat1 = new Chat(
            botUser.chatId(),
            botUser.id(),
            botUser.name(),
            new ArrayList<>()
        );
        isWaiting.put(botUser, CommandName.TRACK);
        Bot bot = new Bot(
            telegramBot,
            Map.of(botUser, chat1),
            isWaiting
        );
        var handler = new NoCommandHandler(applicationConfig, true);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("stackoverflow.com/search?q=unsupported%20link");
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);

        var result = handler.handle(bot, messageHandler, update);

        assertThat(result.getParameters().get("text")).isEqualTo(
            ("sorry")
        );
    }
}
