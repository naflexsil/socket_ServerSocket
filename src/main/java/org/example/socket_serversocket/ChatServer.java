package org.example.socket_serversocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);
    private static final Map<String, PrintWriter> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        File logDirectory = new File("src/main/resources/org/example/socket_serversocket/logs");
        if (!logDirectory.exists()) {
            boolean dirCreated = logDirectory.mkdirs();
            if (dirCreated) {
                logger.info("Папка для логов успешно создана.");
            } else {
                logger.error("Не удалось создать папку для логов.");
            }
        }

        Properties properties = new Properties();

        InputStream input = ChatServer.class.getResourceAsStream("/org/example/socket_serversocket/server.properties");
        if (input == null) {
            logger.error("Файл server.properties не найден.");
            return;
        } else {
            logger.info("Файл server.properties успешно загружен.");
        }

        String host = properties.getProperty("server.host", "localhost");
        int port = Integer.parseInt(properties.getProperty("server.port", "12345"));

        logger.info("Сервер запущен на {}:{}", host, port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            logger.error("Ошибка запуска сервера", e);
        }
        File logDir = new File("./src/main/resources/org/example/socket_serversocket/logs");
        if (!logDir.exists()) {
            if (logDir.mkdirs()) {
                logger.info("Папка для логов была успешно создана.");
            } else {
                logger.error("Не удалось создать папку для логов.");
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String nickname = in.readLine();
                clients.put(nickname, out);
                logger.info("Пользователь '{}' подключился к серверу", nickname);

                String message;
                while ((message = in.readLine()) != null) {
                    handleMessage(nickname, message, out);
                }
            } catch (IOException e) {
                logger.warn("Ошибка общения с клиентом: {}", e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    logger.error("Ошибка закрытия сокета", e);
                }
            }
        }

        private void handleMessage(String nickname, String message, PrintWriter out) {
            if (message.equals("/users")) {
                handleUserListRequest(out);
            } else if (message.startsWith("/w")) {
                handlePrivateMessage(nickname, message, out);
            } else {
                handleBroadcastMessage(nickname, message);
            }
        }

        private void handleUserListRequest(PrintWriter out) {
            out.println("Список пользователей: " + String.join(", ", clients.keySet()));
        }

        private void handlePrivateMessage(String sender, String message, PrintWriter out) {
            String[] parts = message.split(" ", 3);
            if (parts.length < 3) {
                out.println("Ошибка формата. Используйте: /w <ник> <сообщение>");
                return;
            }
            String targetNick = parts[1];
            String privateMessage = parts[2];
            PrintWriter targetOut = clients.get(targetNick);
            if (targetOut != null) {
                logger.info("ЛС от '{}' к '{}': {}", sender, targetNick, privateMessage);
                targetOut.println("[ЛС от " + sender + "]: " + privateMessage);
            } else {
                out.println("Пользователь " + targetNick + " не найден.");
            }
        }

        private void handleBroadcastMessage(String sender, String message) {
            logger.info("Широковещательное сообщение от '{}': {}", sender, message);
            for (Map.Entry<String, PrintWriter> entry : clients.entrySet()) {
                if (!entry.getKey().equals(sender)) {
                    entry.getValue().println("[От " + sender + "]: " + message);
                }
            }
        }
    }
}
