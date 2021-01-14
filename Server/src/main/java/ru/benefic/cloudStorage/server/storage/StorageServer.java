package ru.benefic.cloudStorage.server.storage;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Класс, отвечающий за обработку клиентских соединений и делегирования исполнения команд от клиентов
 * Открывает сокет и ожидает новых подключений
 */

public class StorageServer {

    private static final Logger Log = Logger.getLogger(StorageServer.class);

    private final ServerSocket serverSocket;

    public StorageServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void start() throws IOException {

    }
}

