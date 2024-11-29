package org.example.socket_serversocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.PrintWriter;
import java.util.Map;

public class MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public static void handleMessage(String sender, String message, PrintWriter out) {
        if (message.equals("/users")) {
            handleUserListRequest(out);
        } else if (message.startsWith("/w")) {
            handlePrivateMessage(sender, message, out);
        } else {
            handleBroadcastMessage(sender, message);
        }
    }

    private static void handleUserListRequest(PrintWriter out) {
        out.println("Список пользователей: " + String.join(", ", ChatClientsRegistry.getClients().keySet()));
    }

    private static void handlePrivateMessage(String sender, String message, PrintWriter out) {
        String[] parts = message.split(" ", 3);
        if (parts.length < 3) {
            out.println("Ошибка формата. Используйте: /w <ник> <сообщение>");
            return;
        }
        String targetNick = parts[1];
        String privateMessage = parts[2];
        PrintWriter targetOut = ChatClientsRegistry.getClients().get(targetNick);
        if (targetOut != null) {
            logger.info("ЛС от '{}' к '{}': {}", sender, targetNick, privateMessage);
            targetOut.println("[ЛС от " + sender + "]: " + privateMessage);
        } else {
            out.println("Пользователь " + targetNick + " не найден.");
        }
    }

    private static void handleBroadcastMessage(String sender, String message) {
        logger.info("Широковещательное сообщение от '{}': {}", sender, message);
        for (Map.Entry<String, PrintWriter> entry : ChatClientsRegistry.getClients().entrySet()) {
            if (!entry.getKey().equals(sender)) {
                entry.getValue().println("[От " + sender + "]: " + message);
            }
        }
    }
}
