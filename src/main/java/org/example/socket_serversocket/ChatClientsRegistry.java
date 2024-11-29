package org.example.socket_serversocket;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatClientsRegistry {
    private static final Map<String, PrintWriter> clients = new ConcurrentHashMap<>();

    public static void addClient(String nickname, PrintWriter out) {
        clients.put(nickname, out);
    }

    public static void removeClient(Socket clientSocket) {
        clients.values().removeIf(PrintWriter::checkError);
    }

    public static Map<String, PrintWriter> getClients() {
        return clients;
    }
}
