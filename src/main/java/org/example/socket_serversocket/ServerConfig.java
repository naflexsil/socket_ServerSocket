package org.example.socket_serversocket;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public record ServerConfig(String host, int port) {

    public static ServerConfig loadConfig(String filePath) {
        Properties properties = new Properties();
        try (InputStream input = ServerConfig.class.getResourceAsStream(filePath)) {
            if (input == null) {
                throw new IOException("Файл конфигурации не найден: " + filePath);
            }
            properties.load(input);
            String host = properties.getProperty("server.host", "localhost");
            int port = Integer.parseInt(properties.getProperty("server.port", "12345"));

            return new ServerConfig(host, port);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки конфигурации: " + e.getMessage(), e);
        }
    }
}
