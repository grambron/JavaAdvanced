package ru.ifmo.badikova.walk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Walk {

    public static void main(String[] args) throws WalkException {
        if (args == null || args.length != 2) {
            throw new WalkException("Invalid amount of arguments: 2 files expected");
        }
        if (args[0] == null) {
            throw new WalkException("Invalid type of input file");
        }
        if (args[1] == null) {
            throw new WalkException("Invalid type of output file");
        }

        Path input;
        Path output;

        try {
            input = Paths.get(args[0]);
            output = Paths.get(args[1]);
            if (output.getParent() != null) {
                Files.createDirectories(output.getParent());
            }
        } catch (InvalidPathException e) {
            System.err.println("Invalid path" + e.getMessage());
            return;
        } catch (IOException e) {
            System.err.println("Can't create output directory" + e.getMessage());
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(input)) {
            try (BufferedWriter writer = Files.newBufferedWriter(output)) {
                String inputPath;
                while ((inputPath = reader.readLine()) != null) {
                    try {
                        Path path = Paths.get(inputPath);
                        writer.write(String.format("%08x %s\n", Hasher.getHash(path), inputPath));
                    } catch (InvalidPathException e) {
                        writer.write(String.format("%08x %s\n", 0,  inputPath));
                    }
                }
            } catch (IOException e) {
                System.err.println("Error with output file" + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Error with input file" + e.getMessage());
        }
    }
}
