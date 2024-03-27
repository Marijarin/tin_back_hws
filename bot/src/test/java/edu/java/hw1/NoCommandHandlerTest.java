package edu.java.hw1;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.client.model.AddLinkRequest;
import edu.java.bot.client.model.ChatResponse;
import edu.java.bot.client.model.LinkResponse;
import edu.java.bot.client.model.RemoveLinkRequest;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.repository.CommandName;
import edu.java.bot.service.NoCommandHandler;
import edu.java.bot.service.UserMessageHandler;
import edu.java.bot.service.UserMessageHandlerImpl;
import edu.java.bot.service.model.Bot;
import edu.java.bot.service.model.BotUser;
import edu.java.bot.service.model.Chat;
import java.net.URI;
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
public class NoCommandHandlerTest {
    @Mock TelegramBot telegramBot = new TelegramBot("12345");
    HashMap<BotUser, CommandName> isWaiting = new HashMap<>();

    @Mock Update update = new Update();

    @Mock Message message = new Message();

    @Mock ScrapperClient scrapperClient;

    User user = new User(1L);

    UserMessageHandler messageHandler = new UserMessageHandlerImpl();
    com.pengrad.telegrambot.model.Chat chat = mock(com.pengrad.telegrambot.model.Chat.class);
    BotUser botUser = new BotUser(1L, 1L, null, true);

    ApplicationConfig applicationConfig = new ApplicationConfig(
        "12345",
        "register",
        "1",
        "2",
        "sorry",
        "3",
        "7",
        "8",
        "4 ",
        "^(https?://){1}([\\w\\Q$-_+!*'(),%\\E]+\\.)+(\\w{2,63})(:\\d{1,4})?([\\w\\Q/$-_+!*'(),%\\E]+\\.?[\\w\\Q$-_+!*'(),%\\E={0-5}?&.])*/?$",
        "6",
        "",
        "",
        ""

    );

    @Test
    void processesUnAuthorized() {
        Bot bot = new Bot(
            telegramBot,
            Map.of(),
            Map.of()
        );
        var handler = new NoCommandHandler(applicationConfig, scrapperClient);
        var response = new ChatResponse(0L);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn(CommandName.LIST.getCommand());
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);
        Mockito.when(scrapperClient.findChat(message.chat().id())).thenReturn(response);

        var result = handler.handle(bot, messageHandler, update);

        assertThat(result.getParameters().get("text")).isEqualTo(
            ("register")
        );
    }

    @Test
    void startsTracking() {
        Chat chat1 = new Chat(
            botUser.chatId(),
            botUser.id(),
            botUser.name(),
            new HashSet<>()
        );
        isWaiting.put(botUser, CommandName.TRACK);
        Bot bot = new Bot(
            telegramBot,
            Map.of(botUser, chat1),
            isWaiting
        );
        var handler = new NoCommandHandler(applicationConfig, scrapperClient);
        var linkRequest = new AddLinkRequest(URI.create("https://stackoverflow.com/search?q=unsupported%20link"));

        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("https://stackoverflow.com/search?q=unsupported%20link");
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);
        Mockito.when(scrapperClient.startLinkTracking(botUser.chatId(), linkRequest))
            .thenReturn(new LinkResponse(1L, URI.create("https://stackoverflow.com/search?q=unsupported%20link")));
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
            new HashSet<>()
        );
        chat1.links().add("https://stackoverflow.com/search?q=unsupported%20link");
        isWaiting.put(botUser, CommandName.UNTRACK);
        Bot bot = new Bot(
            telegramBot,
            Map.of(botUser, chat1),
            isWaiting
        );
        var handler = new NoCommandHandler(applicationConfig, scrapperClient);
        var linkRequest = new RemoveLinkRequest(URI.create("https://stackoverflow.com/search?q=unsupported%20link"));
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("https://stackoverflow.com/search?q=unsupported%20link");
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);
        Mockito.when(scrapperClient.stopLinkTracking(botUser.chatId(), linkRequest))
            .thenReturn(new LinkResponse(1L, URI.create("https://stackoverflow.com/search?q=unsupported%20link")));
        var result = handler.handle(bot, messageHandler, update);

        assertThat(result.getParameters().get("text")).isEqualTo(
            ("4 []")
        );
        assertThat(chat1.links()).isEqualTo(new HashSet<>());
    }

    @Test
    void badLink() {
        Chat chat1 = new Chat(
            botUser.chatId(),
            botUser.id(),
            botUser.name(),
            new HashSet<>()
        );
        isWaiting.put(botUser, CommandName.TRACK);
        Bot bot = new Bot(
            telegramBot,
            Map.of(botUser, chat1),
            isWaiting
        );
        var handler = new NoCommandHandler(applicationConfig, scrapperClient);
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
