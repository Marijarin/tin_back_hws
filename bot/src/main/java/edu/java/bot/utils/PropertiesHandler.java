package edu.java.bot.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHandler {

    Properties properties = new Properties();
    String token;

    public PropertiesHandler() {
        try {
            properties.load(new FileInputStream(".env"));
            token = properties.getProperty("TELEGRAM_TOKEN").split("\"")[1];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getToken() {
        return token;
    }
}
