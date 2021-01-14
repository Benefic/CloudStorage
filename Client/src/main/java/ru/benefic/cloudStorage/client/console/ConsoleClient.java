/*
 * Copyright (c) 2021. Benefic
 */

package ru.benefic.cloudStorage.client.console;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.benefic.cloudStorage.client.Client;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ConsoleClient {
    private static final Logger Log = LogManager.getLogger(ConsoleClient.class);

    public static void main(String[] args) {
        System.out.println("Укажите путь к файлу...");
        Scanner scanner = new Scanner(System.in);
        String filePath = scanner.nextLine();
        File userFile = new File(filePath);
        if (userFile.exists()) {
            Client client = new Client();
            if (client.connect()) {
                boolean success;
                try {
                    success = client.uploadFile(userFile);
                } catch (IOException e) {
                    success = false;
                    Log.error("Ошибка:", e);
                }
                if (success) {
                    Log.info("Файл успешно выгружен!");
                } else {
                    Log.info("Не удалось выгрузить файл!");
                }
            } else {
                Log.error("Не удалось соединиться с сервером");
            }
        } else {
            Log.error("Не удалось прочитать файл. Нет доступа или он не существует!");
        }
    }
}
