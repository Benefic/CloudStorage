/*
 * Copyright (c) 2021. Benefic
 */

package ru.benefic.cloudStorage.server.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.benefic.cloudStorage.common.Command;
import ru.benefic.cloudStorage.common.commands.FilePart;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandler {

    private static final Logger Log = LogManager.getLogger(ClientHandler.class);

    private final Socket clientSocket;
    private ObjectInputStream in;
    private OutputStream os = null;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void handle() throws IOException {
        // открываем стримы для общения с клиентом
        in = new ObjectInputStream(clientSocket.getInputStream());

        new Thread(() -> {
            try {
                if (isConnected()) {
                    // ждём, что скажет клиент и обрабатываем команду или данные
                    readMessages();
                }
            } catch (IOException e) {
                Log.error("Client handle error", e);
            } finally {
                try {
                    closeConnection();
                } catch (IOException e) {
                    Log.error("Failed to close connection!", e);
                }
            }
        }).start();
    }

    private void readMessages() throws IOException {
        while (isConnected()) {
            Command command = readCommand();
            if (command == null) {
                continue;
            }
            Log.info("Incoming command: " + command);

            switch (command.getType()) {
                case FILE_TRANSFER: {
                    // TODO: здесь надо будет добавить обработку ошибки с отправкой клиенту
                    FilePart fileData = (FilePart) command.getData();
                    if (os == null) {
                        os = new FileOutputStream(fileData.getFileName(), true);
                    }
                    os.write(fileData.getData());
                    if (fileData.isEnd()) {
                        os.close();
                        os = null;
                    }
                    break;
                }
                case EXIT: {
                    closeConnection();
                    return;
                }
                default:
                    Log.error("Unknown type of command: " + command.getType());
            }
        }
    }

    private Command readCommand() throws IOException {
        try {
            return (Command) in.readObject();
        } catch (ClassNotFoundException e) {
            String error = "Unknown type of object from client";
            Log.error(error, e);
            return null;
        }
    }

    private boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected();
    }

    private void closeConnection() throws IOException {
        if (isConnected()) {
            clientSocket.close();
        }
    }
}

