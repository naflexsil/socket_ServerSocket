package org.example.socket_serversocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);

    public static void main(String[] args) {
        File logDirectory = new File("src/main/resources/org/example/socket_serversocket/logs");
        if (!logDirectory.exists() && logDirectory.mkdirs()) {
            logger.info("Папка для логов успешно создана.");
        }

        ServerConfig config = ServerConfig.loadConfig("/org/example/socket_serversocket/server.properties");
        logger.info("Сервер запущен на {}:{}", config.host(), config.port());

        try (ServerSocket serverSocket = new ServerSocket(config.port())) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            logger.error("Ошибка запуска сервера", e);
        }
    }
}
