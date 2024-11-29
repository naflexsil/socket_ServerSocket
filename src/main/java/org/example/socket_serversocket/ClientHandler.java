package org.example.socket_serversocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
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
            ChatClientsRegistry.addClient(nickname, out);
            logger.info("Пользователь '{}' подключился к серверу", nickname);

            String message;
            while ((message = in.readLine()) != null) {
                MessageHandler.handleMessage(nickname, message, out);
            }
        } catch (IOException e) {
            logger.warn("Ошибка общения с клиентом: {}", e.getMessage());
        } finally {
            ChatClientsRegistry.removeClient(clientSocket);
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.error("Ошибка закрытия сокета", e);
            }
        }
    }
}

