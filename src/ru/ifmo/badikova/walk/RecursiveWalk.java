package ru.ifmo.badikova.walk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RecursiveWalk {
    private static void walk(String inputFileName, String outputFileName) throws WalkException {
        Path inputPath;
        Path outputPath;

        try {
            outputPath = Paths.get(outputFileName);
            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }
        } catch (InvalidPathException e) {
            throw new WalkException("Invalid path");
        } catch (IOException e) {
            throw new WalkException("Cannot create parent directory");
        }

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFileName))) {
            try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
                String file;
                MyFileVisitor visitor = new MyFileVisitor(writer);
                while ((file = reader.readLine()) != null) {
                    try {
                        Files.walkFileTree(Paths.get(file), visitor);
                    } catch (InvalidPathException e) {
                        writer.write(String.format("%08x %s", 0, file));
                        writer.newLine();
                    }
                }
            } catch (IOException e) {
                throw new WalkException("Error with opening output file: " + outputFileName);
            }
        } catch (IOException e) {
            throw new WalkException("Error with opening input file: " + inputFileName);
        } catch (InvalidPathException e) {
            throw new WalkException("Invalid path of input file: " + inputFileName);
        }
    }

    public static void main(String[] args) {
        try {
            if (args == null || args.length != 2) {
                throw new WalkException("Invalid amount of arguments: 2 files expected");
            }
            if (args[0] == null) {
                throw new WalkException("Invalid type of input file");
            }
            if (args[1] == null) {
                throw new WalkException("Invalid type of output file");
            }
            RecursiveWalk.walk(args[0], args[1]);
        } catch (WalkException e) {
            System.err.println(e.getMessage());
        }
    }
}
