package edu.java.hw1;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.service.model.Bot;
import edu.java.bot.service.model.BotUser;
import edu.java.bot.service.model.Chat;
import edu.java.bot.repository.CommandName;
import edu.java.bot.service.ListHandler;
import edu.java.bot.service.UserMessageHandler;
import edu.java.bot.service.UserMessageHandlerImpl;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest {
    @Mock TelegramBot telegramBot = new TelegramBot("12345");
    HashMap<BotUser, CommandName> isWaiting = new HashMap<>();

    @Mock Update update = new Update();

    @Mock Message message = new Message();

    User user = new User(1L);

    UserMessageHandler messageHandler = new UserMessageHandlerImpl();
    com.pengrad.telegrambot.model.Chat chat = mock(com.pengrad.telegrambot.model.Chat.class);
    BotUser botUser = new BotUser(1L, 1L, null, true);

    ApplicationConfig applicationConfig = new ApplicationConfig(
        "12345",
        "aa",
        "1",
        "1",
        "1",
        "1",
        "Here you are: ",
        "You have no links being tracked. Print /track to add a link",
        "1",
        "1",
        "1",
        ""

    );

    @Test
    void appliesListCommand() {
        Chat chat1 = new Chat(
            botUser.chatId(),
            botUser.id(),
            botUser.name(),
            List.of("https://stackoverflow.com/search?q=unsupported%20link")
        );
        isWaiting.put(botUser, null);
        var bot = new Bot(
            telegramBot,
            Map.of(botUser, chat1),
            isWaiting
        );

        var handler = new ListHandler(applicationConfig, true);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);

        var result = handler.handle(bot, messageHandler, update);

        assertThat(result.getParameters().get("text")).isEqualTo(
            ("Here you are: " + Arrays.deepToString(bot.chats().get(botUser).links().toArray())
            ));
    }

    @Test
    void notifiesWhenNoLinks() {
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
        var handler = new ListHandler(applicationConfig, true);

        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);

        var result = handler.handle(bot, messageHandler, update);

        assertThat(result.getParameters().get("text")).isEqualTo(
            ("You have no links being tracked. Print /track to add a link")
        );
    }

    @Test
    void asksToRegister() {
        Bot bot = new Bot(
            telegramBot,
            Map.of(),
            Map.of()
        );
        var handler = new ListHandler(applicationConfig, true);

        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);

        var result = handler.handle(bot, messageHandler, update);

        assertThat(result.getParameters().get("text")).isEqualTo(
            ("aa")
        );
    }
}

