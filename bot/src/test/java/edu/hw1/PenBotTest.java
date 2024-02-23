package edu.hw1;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.model.Bot;
import edu.java.bot.model.BotUser;
import edu.java.bot.model.Chat;
import edu.java.bot.repository.CommandName;
import edu.java.bot.service.UserMessageHandler;
import edu.java.bot.service.PenBot;
import edu.java.bot.service.UserMessageHandlerImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class PenBotTest {
//    @Mock TelegramBot telegramBot = new TelegramBot("12345");
//    BotUser botUser = new BotUser(1, 123, "Test User", false);
//    HashMap<BotUser, CommandName> isWaiting = new HashMap<>();
//
//    Update update = mock(Update.class);
//
//    Message message = mock(Message.class);
//
//    User user = new User(1L);
//
//    UserMessageHandler messageHandler = mock(UserMessageHandlerImpl.class);
//    com.pengrad.telegrambot.model.Chat chat = mock(com.pengrad.telegrambot.model.Chat.class);
//
//    @ParameterizedTest
//    @EnumSource(CommandName.class)
//    void appliesCommands(CommandName command) {
//        Chat chat = new Chat(
//            botUser.chatId(),
//            botUser.id(),
//            botUser.name(),
//            List.of()
//        );
//        isWaiting.put(botUser, null);
//        Bot bot = new Bot(
//            telegramBot,
//            Map.of(botUser, chat),
//            isWaiting
//        );
//        ApplicationConfig applicationConfig = new ApplicationConfig(
//            "12345",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1"
//
//        );
//        try (PenBot penBot = new PenBot(bot, applicationConfig)) {
//            var result = penBot.apply(command);
//
//            assertThat(result).hasNoNullFieldsOrProperties();
//        }
//    }
//
//    @Test
//    void notifiesWhenAreLinks() {
//        Chat chat = new Chat(
//            botUser.chatId(),
//            botUser.id(),
//            botUser.name(),
//            List.of("https://stackoverflow.com/search?q=unsupported%20link")
//        );
//        isWaiting.put(botUser, null);
//        Bot bot = new Bot(
//            telegramBot,
//            Map.of(botUser, chat),
//            isWaiting
//        );
//        ApplicationConfig applicationConfig = new ApplicationConfig(
//            "12345",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "Here you are: ",
//            "1",
//            "1",
//            "1",
//            "1"
//
//        );
//
//        try (PenBot penBot = new PenBot(bot, applicationConfig)) {
//            SendMessage result = penBot.listLinks(botUser);
//
//            assertThat(result.getParameters().get("text")).isEqualTo(
//                ("Here you are: " + Arrays.deepToString(bot.chats().get(botUser).links().toArray())
//                ));
//        }
//    }
//
//    @Test
//    void appliesLinkCommand() {
//        var botUser1 = new BotUser(1L, 1L, null, true);
//        Chat chat1 = new Chat(
//            botUser1.chatId(),
//            botUser1.id(),
//            botUser1.name(),
//            List.of("https://stackoverflow.com/search?q=unsupported%20link")
//        );
//        isWaiting.put(botUser1, null);
//        Bot bot = new Bot(
//            telegramBot,
//            Map.of(botUser1, chat1),
//            isWaiting
//        );
//        ApplicationConfig applicationConfig = new ApplicationConfig(
//            "12345",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "Here you are: ",
//            "1",
//            "1",
//            "1",
//            "1"
//
//        );
//
//        try (PenBot penBot = new PenBot(bot, applicationConfig)) {
//            var result1 = penBot.apply(CommandName.LIST);
//            Mockito.when(messageHandler.extractUser(update)).thenReturn(botUser);
//
//            Mockito.when(update.message()).thenReturn(message);
//            Mockito.when(message.text()).thenReturn("/start");
//            Mockito.when(message.chat()).thenReturn(chat);
//            Mockito.when(message.chat().id()).thenReturn(1L);
//            Mockito.when(message.from()).thenReturn(user);
//
//            var result2 = result1.apply(update);
//
//            assertThat(result2.getParameters().get("text")).isEqualTo(
//                ("Here you are: " + Arrays.deepToString(bot.chats().get(botUser1).links().toArray())
//                ));
//        }
//    }
//
//    @Test
//    void notifiesWhenNoLinks() {
//        Chat chat = new Chat(
//            botUser.chatId(),
//            botUser.id(),
//            botUser.name(),
//            List.of()
//        );
//        isWaiting.put(botUser, null);
//        Bot bot = new Bot(
//            telegramBot,
//            Map.of(botUser, chat),
//            isWaiting
//        );
//        ApplicationConfig applicationConfig = new ApplicationConfig(
//            "12345",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "You have no links being tracked. Print /track to add a link",
//            "1",
//            "1",
//            "1"
//
//        );
//
//        try (PenBot penBot = new PenBot(bot, applicationConfig)) {
//            SendMessage result = penBot.listLinks(botUser);
//
//            assertThat(result.getParameters().get("text")).isEqualTo(
//                ("You have no links being tracked. Print /track to add a link")
//            );
//        }
//    }
//
//    @ParameterizedTest
//    @EnumSource(CommandName.class)
//    void notifiesWhenIsWaitingLink(CommandName command) {
//        Chat chat = new Chat(
//            botUser.chatId(),
//            botUser.id(),
//            botUser.name(),
//            List.of()
//        );
//        Bot bot = new Bot(
//            telegramBot,
//            Map.of(botUser, chat),
//            isWaiting
//        );
//        ApplicationConfig applicationConfig = new ApplicationConfig(
//            "12345",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1234567",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1"
//
//        );
//
//        try (PenBot penBot = new PenBot(bot, applicationConfig)) {
//            SendMessage result = penBot.waitForALink(botUser, command);
//            if (command.equals(CommandName.TRACK) || command.equals(CommandName.UNTRACK)) {
//
//                assertThat(result.getParameters().get("text")).isEqualTo(
//                    ("1234567")
//                );
//            }
//        }
//    }
//
//    @Test
//    void notifiesWhenRegistered() {
//        Bot bot = new Bot(
//            telegramBot,
//            new HashMap<>(),
//            new HashMap<>()
//        );
//        ApplicationConfig applicationConfig = new ApplicationConfig(
//            "12345",
//            "1",
//            "Registered! Hello, dear ",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1"
//
//        );
//
//        try (PenBot penBot = new PenBot(bot, applicationConfig)) {
//            SendMessage result = penBot.registerUser(botUser);
//
//            assertThat(result.getParameters().get("text")).isEqualTo(
//                ("Registered! Hello, dear " + botUser.name())
//            );
//        }
//    }
//
//    @Test
//    void notifiesWhenAlreadyRegistered() {
//        Chat chat = new Chat(
//            botUser.chatId(),
//            botUser.id(),
//            botUser.name(),
//            List.of()
//        );
//        isWaiting.put(botUser, null);
//        Bot bot = new Bot(
//            telegramBot,
//            Map.of(botUser, chat),
//            isWaiting
//        );
//        ApplicationConfig applicationConfig = new ApplicationConfig(
//            "12345",
//            "1",
//            "1",
//            "1234567",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1"
//
//        );
//
//        try (PenBot penBot = new PenBot(bot, applicationConfig)) {
//            SendMessage result = penBot.registerUser(botUser);
//
//            assertThat(result.getParameters().get("text")).isEqualTo(
//                ("1234567")
//            );
//        }
//    }
//
//    @Test
//    void notifiesWhenNotRegistered() {
//        Bot bot = new Bot(
//            telegramBot,
//            Map.of(),
//            Map.of()
//        );
//        ApplicationConfig applicationConfig = new ApplicationConfig(
//            "12345",
//            "Please register using /start command",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1"
//
//        );
//
//        try (PenBot penBot = new PenBot(bot, applicationConfig)) {
//            SendMessage result;
//            if (penBot.isBotHaving(botUser)) {
//                result = penBot.listLinks(botUser);
//            } else {
//                result = penBot.askToRegister(botUser.chatId());
//            }
//
//            assertThat(result.getParameters().get("text")).isEqualTo(
//                ("Please register using /start command")
//            );
//        }
//    }
//
//    @Test
//    void notifiesWhenGetsValidLink() {
//        Chat chat = new Chat(
//            botUser.chatId(),
//            botUser.id(),
//            botUser.name(),
//            new ArrayList<>()
//        );
//        isWaiting.put(botUser, CommandName.TRACK);
//        Bot bot = new Bot(
//            telegramBot,
//            Map.of(botUser, chat),
//            isWaiting
//        );
//        ApplicationConfig applicationConfig = new ApplicationConfig(
//            "12345",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "Done: ",
//            "^(https?://){1}([\\w\\Q$-_+!*'(),%\\E]+\\.)+(\\w{2,63})(:\\d{1,4})?([\\w\\Q/$-_+!*'(),%\\E]+\\.?[\\w\\Q$-_+!*'(),%\\E={0-5}?&.])*/?$",
//            "1"
//
//        );
//
//        try (PenBot penBot = new PenBot(bot, applicationConfig)) {
//            SendMessage result = penBot.convertToLink(
//                botUser,
//                "https://stackoverflow.com/search?q=unsupported%20link",
//                bot.isWaiting().get(botUser)
//            );
//
//            assertThat(
//                result.getParameters().get("text").toString()
//                    .contains("https://stackoverflow.com/search?q=unsupported%20link"))
//                .isTrue();
//        }
//    }
//
//    @Test
//    void notifiesWhenUnknownCommand() {
//        Chat chat = new Chat(
//            botUser.chatId(),
//            botUser.id(),
//            botUser.name(),
//            new ArrayList<>()
//        );
//        isWaiting.put(botUser, null);
//        Bot bot = new Bot(
//            telegramBot,
//            Map.of(botUser, chat),
//            isWaiting
//        );
//        ApplicationConfig applicationConfig = new ApplicationConfig(
//            "12345",
//            "1",
//            "1",
//            "1",
//            "sgkhsjldgkhslgkh",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1",
//            "1"
//
//        );
//
//        try (PenBot penBot = new PenBot(bot, applicationConfig)) {
//            SendMessage result = penBot.invalidUserMessage(botUser.chatId());
//
//            assertThat(result.getParameters().get("text")).isEqualTo(("sgkhsjldgkhslgkh"));
//        }
//    }
//
//    @Test
//    void notifiesWhenBadLink() {
//        Chat chat = new Chat(
//            botUser.chatId(),
//            botUser.id(),
//            botUser.name(),
//            new ArrayList<>()
//        );
//        isWaiting.put(botUser, CommandName.TRACK);
//        Bot bot = new Bot(
//            telegramBot,
//            Map.of(botUser, chat),
//            isWaiting
//        );
//        ApplicationConfig applicationConfig = new ApplicationConfig(
//            "12345",
//            "1",
//            "1",
//            "1",
//            "1234567",
//            "1",
//            "1",
//            "1",
//            "1",
//            "^(https?://){1}([\\w\\Q$-_+!*'(),%\\E]+\\.)+(\\w{2,63})(:\\d{1,4})?([\\w\\Q/$-_+!*'(),%\\E]+\\.?[\\w\\Q$-_+!*'(),%\\E={0-5}?&.])*/?$",
//            "1"
//
//        );
//
//        try (PenBot penBot = new PenBot(bot, applicationConfig)) {
//            SendMessage result =
//                penBot.convertToLink(botUser, "stackoverflow.com/search?q=unsupported%20link", CommandName.TRACK);
//
//            assertThat(result.getParameters().get("text")).isEqualTo(("1234567"));
//        }
//    }

}
