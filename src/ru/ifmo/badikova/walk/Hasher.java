package ru.ifmo.badikova.walk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Hasher {
    public static int getHash(Path path) {
        int hash = 0x811c9dc5;
        try (InputStream reader = Files.newInputStream(path)) {

                byte[] bytes = new byte[1024];
                int counter;
                while ((counter = reader.read(bytes)) != -1) {
                    for (int i = 0; i < counter; i++) {
                        hash = (hash * 0x01000193) ^ (bytes[i] & 0xff);
                    }
                }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            hash = 0;
        }
        return hash;
    }
}

