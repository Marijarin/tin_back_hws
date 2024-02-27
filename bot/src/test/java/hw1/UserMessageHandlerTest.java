package hw1;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import edu.java.bot.model.BotUser;
import edu.java.bot.model.UserMessage;
import edu.java.bot.repository.CommandName;
import edu.java.bot.service.UserMessageHandlerImpl;
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
public class UserMessageHandlerTest {
    BotUser botUser = new BotUser(1, 123, "Test User", false);
    UserMessageHandlerImpl messageHandler = new UserMessageHandlerImpl();
    @Mock Update update = new Update();

    @Mock Message message = new Message();
    Chat chat = mock(Chat.class);
    User user = new User(1L);

    @ParameterizedTest
    @EnumSource(CommandName.class)
    void findsCommand(CommandName command) {
        UserMessage userMessage = new UserMessage(1, botUser.chatId(), botUser, command.getCommand());

        var result = messageHandler.isCommand(userMessage);

        assertThat(result).isEqualTo(true);
    }

    @Test
    void printsCommands() {
        UserMessage userMessage = new UserMessage(1, botUser.chatId(), botUser, "/list");

        var result = messageHandler.listCommands(userMessage);

        assertThat(result.getParameters().get("text")).isEqualTo("""

            /list - Receive a list of tracked links
            /help - Look up available commands
            /track - Request to wait for a link to track, is followed by a message containing a link
            /start - Register to track links
            /untrack - Request to wait for a link to untrack, is followed by a message containing a link""");
    }

    @Test
    void extractsUser() {
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);

        var result = messageHandler.extractUser(update);

        assertThat(result).isEqualTo(new BotUser(1L, 1L, null, true));
    }

    @Test
    void convertsUpdate() {
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.messageId()).thenReturn(1);
        Mockito.when(message.text()).thenReturn("/start");
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.chat().id()).thenReturn(1L);
        Mockito.when(message.from()).thenReturn(user);

        var result = messageHandler.convert(update);
        var bUser = messageHandler.extractUser(update);

        assertThat(result).isEqualTo(new UserMessage(
            1,
            1L,
            bUser,
            "/start"
        ));
    }
}
