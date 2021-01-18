/*
 * Copyright (c) 2021. Benefic
 */

package nio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class NioServer {

    private static final Logger Log = LogManager.getLogger(NioServer.class);
    private final ServerSocketChannel serverChannel = ServerSocketChannel.open();
    private final Selector selector = Selector.open();
    private final ByteBuffer buffer = ByteBuffer.allocate(5);
    private final int port = 8189;
    private final Path serverPath = Paths.get("serverDir");
    private Path currentPath = serverPath;
    private SocketChannel channel;

    public NioServer() throws IOException {
        Log.info("Запуск сервера на порту " + port);

        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false);

        Log.info("Регистрация селектора...");
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (serverChannel.isOpen()) {
            selector.select(); // block
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                Log.info("Селект ключа в итераторе...");
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    Log.info("Accept");
                    handleAccept(key);
                }
                if (key.isReadable()) {
                    Log.info("Read");
                    handleRead(key);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NioServer();
    }

    private void handleRead(SelectionKey key) throws IOException {
        channel = (SocketChannel) key.channel();
        StringBuilder msg = new StringBuilder();
        while (channel.read(buffer) > 0) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                msg.append((char) buffer.get());
            }
            buffer.clear();
        }
        String userData = msg.toString().replaceAll("[\n|\r]", "");
        String[] userDataContent = userData.split(" ", 2);
        String command = userDataContent[0];
        Log.info("Получена команда: " + userData);
        switch (command) {
            case "ls":
                String files = Files.list(currentPath)
                        .map(path -> path.getFileName().toString())
                        .collect(Collectors.joining(", "));
                writeToChannel(files);
                break;
            case "mkdir":
                if (checkArgument(userDataContent)) {
                    Files.createDirectory(Paths.get(currentPath.toString(), userDataContent[1]));
                }
                break;
            case "touch":
                if (checkArgument(userDataContent)) {
                    Files.createFile(Paths.get(currentPath.toString(), userDataContent[1]));
                }
                break;
            case "cd":
                if (checkArgument(userDataContent)) {
                    currentPath = Paths.get(currentPath.toString(), userDataContent[1]);
                }
                break;
            case "cat":
                if (checkArgument(userDataContent)) {
                    Path filePath = currentPath.resolve(userDataContent[1]);
                    if (Files.exists(filePath)) {
                        try (RandomAccessFile file = new RandomAccessFile(filePath.toString(), "r")) {
                            FileChannel fileChannel = file.getChannel();
                            ByteBuffer fileBuffer = ByteBuffer.allocate(512);
                            while (fileChannel.read(fileBuffer) > 0) {
                                fileBuffer.flip();
                                writeToChannel(fileBuffer);
                                fileBuffer.clear();
                            }
                            fileChannel.close();
                            // перенос строки после вывода файла
                            writeToChannel("");
                        }
                    } else {
                        writeToChannel("File not found!");
                    }
                }
                break;
            default:
                writeToChannel(userData);
                break;
        }
    }

    private boolean checkArgument(String[] userDataContent) throws IOException {
        if (userDataContent.length < 2) {
            writeToChannel("Invalid argument");
            return false;
        } else {
            return true;
        }
    }

    private void writeToChannel(ByteBuffer byteBuffer) throws IOException {
        channel.write(byteBuffer);
    }

    private void writeToChannel(String msg) throws IOException {
        writeToChannel(ByteBuffer.wrap((msg + "\n\r").getBytes(StandardCharsets.UTF_8)));
    }

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
    }
}