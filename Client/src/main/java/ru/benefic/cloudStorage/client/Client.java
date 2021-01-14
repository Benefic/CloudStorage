/*
 * Copyright (c) 2021. Benefic
 */

package ru.benefic.cloudStorage.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.benefic.cloudStorage.common.Command;

import java.io.*;
import java.net.Socket;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8189;
    private static final Logger Log = LogManager.getLogger(Client.class);

    private final String host;
    private final int port;
    private Socket socket;
    private ObjectOutputStream outputStream;

    public Client() {
        this(SERVER_ADDRESS, SERVER_PORT);
    }

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean connect() {
        try {
            socket = new Socket(host, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            Log.info("Соединение установлено!");
            return true;
        } catch (IOException e) {
            Log.error("Соединение не было установлено!", e);
            return false;
        }
    }

    public boolean uploadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int read;
        boolean isEnd = false;
        while ((read = is.read(buffer)) != -1) {
            if (read < buffer.length) {
                byte[] tmp = new byte[read];
                System.arraycopy(buffer, 0, tmp, 0, read);
                buffer = tmp;
                isEnd = true;
            }
            Command filePart = Command.filePartTransferCommand(file.getName(), buffer, isEnd);
            outputStream.writeObject(filePart);
            outputStream.flush();
            outputStream.reset();
        }
        outputStream.flush();
        is.close();
        return true;
    }

}
