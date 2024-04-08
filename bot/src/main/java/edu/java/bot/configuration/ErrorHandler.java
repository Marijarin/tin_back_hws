package edu.java.bot.configuration;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class ErrorHandler implements ConsumerAwareListenerErrorHandler {
    private static final Logger ERROR = LogManager.getLogger();

    @Override
    public Object handleError(
        Message<?> message,
        ListenerExecutionFailedException exception,
        @NotNull Consumer<?, ?> consumer
    ) {
        ERROR.error(message.getPayload().getClass() + exception.getMessage());
        return null;
    }
}
