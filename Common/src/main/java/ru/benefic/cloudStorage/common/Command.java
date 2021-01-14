/*
 * Copyright (c) 2021. Benefic
 */

package ru.benefic.cloudStorage.common;

import ru.benefic.cloudStorage.common.commands.CommandData;
import ru.benefic.cloudStorage.common.commands.FilePart;

import java.io.Serializable;

public class Command implements Serializable {

    private CommandType type;
    private CommandData data;

    public static Command filePartTransferCommand(String fileName, byte[] fileData, boolean isEnd) {
        Command command = new Command();
        command.type = CommandType.FILE_TRANSFER;
        command.data = new FilePart(fileName, fileData, isEnd);
        return command;
    }

    public static Command exitCommand() {
        Command command = new Command();
        command.type = CommandType.EXIT;
        return command;
    }

    public CommandType getType() {
        return type;
    }

    public CommandData getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Command{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }
}
