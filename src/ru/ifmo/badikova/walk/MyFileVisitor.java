package ru.ifmo.badikova.walk;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class MyFileVisitor extends SimpleFileVisitor<Path> {
    BufferedWriter outputFileWriter;

    MyFileVisitor(BufferedWriter writer) {
        outputFileWriter = writer;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        int hash = Hasher.getHash(file);
        outputFileWriter.write(String.format("%08x %s", hash, file));
        outputFileWriter.newLine();
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.err.println("Failed to read file " + file.toString());
        outputFileWriter.write(String.format("%08x %s", 0, file));
        outputFileWriter.newLine();
        return FileVisitResult.CONTINUE;
    }
}
