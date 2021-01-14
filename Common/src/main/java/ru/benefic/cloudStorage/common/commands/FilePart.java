/*
 * Copyright (c) 2021. Benefic
 */

package ru.benefic.cloudStorage.common.commands;

import java.util.Arrays;

public class FilePart implements CommandData {
    private final String fileName;
    private final boolean isEnd;
    private final byte[] data;

    public FilePart(String fileName, byte[] data, boolean isEnd) {
        this.fileName = fileName;
        this.isEnd = isEnd;
        this.data = data;
    }

    public FilePart(String fileName, byte[] data) {
        this.fileName = fileName;
        this.isEnd = false;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public byte[] getData() {
        return data;
    }

//    @Override
//    public String toString() {
//        return "FilePart{" +
//                "fileName='" + fileName + '\'' +
//                ", isEnd=" + isEnd +
//                ", data length=" + data.length +
//                '}';
//    }


    @Override
    public String toString() {
        return "FilePart{" +
                "fileName='" + fileName + '\'' +
                ", isEnd=" + isEnd +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
