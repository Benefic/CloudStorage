/*
 * Copyright (c) 2021. Benefic
 */

package ru.benefic.cloudStorage.server.storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.benefic.cloudStorage.server.Server;
import ru.benefic.cloudStorage.server.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Класс, отвечающий за обработку клиентских соединений и делегирования исполнения команд от клиентов
 * Открывает сокет и ожидает новых подключений
 */

public class StorageServer {

    private static final Logger Log = LogManager.getLogger(Server.class);

    private final ServerSocket serverSocket;

    // инициализация сервера
    public StorageServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void start() throws IOException {
        Log.info("Starting server...");
        try {
            // бесконечно ожидаем подключения, обрабатываем и снова ждём
            while (!serverSocket.isClosed()) {
                waitAndProcessNewClientConnection();
            }
        } catch (IOException | ExecutionException | InterruptedException e) {
            Log.error("Failed to accept new connection", e);
        } finally {
            serverSocket.close();
        }
    }

    private void waitAndProcessNewClientConnection() throws ExecutionException, InterruptedException, IOException {
        // подключение клиента отправляем в новый поток и обрабатываем уже там
        ExecutorService clientConnectionService = Executors.newSingleThreadExecutor();
        Future<Boolean> connectionSuccess = clientConnectionService.submit(() -> {

            Log.info("Waiting for new connection....");
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
                Log.info("Client connected");
                // подключились, пошли работать с ним
                processClientConnection(clientSocket);
                return true;
            } catch (IOException e) {
                Log.error("Error connecting client", e);
                return false;
            }
        });

        if (!connectionSuccess.get()) {
            throw new IOException();
        }
    }

    private void processClientConnection(Socket clientSocket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(clientSocket);
        clientHandler.handle();
    }
}

