package edu.java.bot;

import edu.java.bot.service.PenBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
    @Autowired PenBot penBot;

    @Override
    public void run(String... args) throws Exception {
        penBot.start();
    }
}
