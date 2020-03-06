package ru.ifmo.badikova.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Implementor implements Impler {
    private static final String OUTFILE_SUFFIX = "Impl.java";

    private static void createPath(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        if (token.isPrimitive() || token.isArray() || token.isEnum() || Modifier.isFinal(token.getModifiers())) {
            throw new ImplerException("Unsupported tokens given");
        }

        Path outfile;

        try {
            outfile = Path.of(root.toString(), token.getPackageName().replace('.', File.separatorChar),
                    token.getSimpleName() + OUTFILE_SUFFIX);
            createPath(outfile);
        } catch (InvalidPathException | IOException e) {
            throw new ImplerException(e.getMessage());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outfile)) {
            writer.write(ImplementorCode.generateCode(token));
        } catch (IOException e) {
            throw new ImplerException(e.getMessage());
        }

    }

    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Expected 2 nonnull arguments");
            return;
        }
        try {
            new Implementor().implement(Class.forName(args[0]), Paths.get(args[1]));
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found by name " + e.getMessage());
        } catch (InvalidPathException e) {
            System.err.println("Invalid root directory " + e.getMessage());
        } catch (ImplerException e) {
            System.err.println("Failed to generate implementation code for given class " + e.getMessage());
        }
    }
}
