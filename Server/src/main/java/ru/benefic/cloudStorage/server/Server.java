/*
 * Copyright (c) 2021. Benefic
 */

package ru.benefic.cloudStorage.server;

import org.apache.log4j.Logger;
import ru.benefic.cloudStorage.server.storage.StorageServer;

import java.io.IOException;

/**
 * Основной класс приложения, стартует сервер на дефолтном или опциональном порту.
 * Для указания нестандартного порта необходимо указать его как параметр строки запуска сервера
 */

public class Server {

    private static final int DEFAULT_PORT = 8189;
    private static final Logger Log = Logger.getLogger(Server.class);

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }
        try {
            new StorageServer(port).start();
        } catch (IOException e) {
            Log.error("Failed to start StorageServer", e);
            System.exit(1);
        }
    }
}