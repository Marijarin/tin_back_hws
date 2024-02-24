package edu.hw1;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import edu.java.bot.model.Bot;
import edu.java.bot.model.BotUser;
import edu.java.bot.model.Chat;
import edu.java.bot.repository.CommandName;
import edu.java.bot.service.HelpHandler;
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

public class HelpHandlerTest {
    @Mock TelegramBot telegramBot = new TelegramBot("12345");
    HashMap<BotUser, CommandName> isWaiting = new HashMap<>();

    Update update = mock(Update.class);

    Message message = mock(Message.class);

    User user = new User(1L);

    UserMessageHandler messageHandler = new UserMessageHandlerImpl();
    com.pengrad.telegrambot.model.Chat chat = mock(com.pengrad.telegrambot.model.Chat.class);
    BotUser botUser1 = new BotUser(1L, 1L, null, true);
    Chat chat1 = new Chat(
        botUser1.chatId(),
        botUser1.id(),
        botUser1.name(),
        List.of("https://stackoverflow.com/search?q=unsupported%20link")
    );

    @Test
    void handlesHelpCommand() {

        isWaiting.put(botUser1, null);
        var bot = new Bot(
            telegramBot,
            Map.of(botUser1, chat1),
            isWaiting
        );
        var handler = new HelpHandler();
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn(CommandName.HELP.getCommand());
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);

        var result = handler.handle(bot, messageHandler, update);

        assertThat(result.getParameters().get("text")).isEqualTo("""

            /list - Receive a list of tracked links
            /help - Look up available commands
            /track - Request to wait for a link to track, is followed by a message containing a link
            /start - Register to track links
            /untrack - Request to wait for a link to untrack, is followed by a message containing a link""");
    }
}
