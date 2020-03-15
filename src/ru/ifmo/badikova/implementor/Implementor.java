package ru.ifmo.badikova.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static ru.ifmo.badikova.implementor.ImplementorFileUtils.*;

/**
 * Class implementing {@link Impler}  and {@link JarImpler} interfaces.
 * Provides public methods to create implementations for given interfaces and packing it into {@code .jar}.
 *
 * @see Impler
 * @see JarImpler
 *
 * @author Anastasiia Badikova
 * @version 1.0
 */
public class Implementor implements Impler, JarImpler {
    /**
     * Suffix defining the name of the resulting class.
     */
    private static final String OUTFILE_SUFFIX = "Impl.java";

    /**
     * Creates missed parent directories of given path.
     * @param path {@link Path} generate parent directory for
     * @throws IOException when I|O exception occurs
     */
    private static void createPath(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    /**
     *Produces code implementing interface specified by provided {@code token}.
     * Generated {@code .java} file location is specified by {@code root}.
     * @param token type token to create implementation for.
     * @param root root directory.
     * @throws ImplerException when implementation cannot be generated.
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        if (token.isPrimitive() || token.isArray() || token.isEnum() || Modifier.isFinal(token.getModifiers()) || Modifier.isPrivate(token.getModifiers())) {
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

    /**
     * Main method. A command line utility for {@link Implementor}.
     * Runs in two modes:
     * <ul>
     *     <li>2 arguments: {@code <className> <outputPath>}. Creates a {@code .java} file, {@link #implement(Class, Path)} is invoked.</li>
     *     <li>3 arguments: {@code -jar <className> <outputPath>}. Creates a {@code .jar} file, {@link #implementJar(Class, Path)} is invoked.</li>
     * </ul>
     * If any error occurs, function stops and displays a message about it.
     * @param args of command line arguments
     */
    public static void main(String[] args) {
        if (args == null || (args.length != 2 && args.length != 3)) {
            System.err.println("Expected 2 or 3 nonnull arguments: [-jar] <className> <outputPath>");
            return;
        } else {
            for (String arg : args) {
                if (arg == null) {
                    System.err.println("Expected nonnull arguments");
                    return;
                }
            }
        }
        try {
            if (args.length == 2) {
                new Implementor().implement(Class.forName(args[0]), Paths.get(args[1]));
            } else if (!"-jar".equals(args[0]) && !"--jar".equals(args[0])) {
                System.err.println("Invalid arguments: only optional available is -jar");
            } else {
                new Implementor().implementJar(Class.forName(args[1]), Paths.get(args[2]));
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found by name " + e.getMessage());
        } catch (InvalidPathException e) {
            System.err.println("Invalid root directory " + e.getMessage());
        } catch (ImplerException e) {
            System.err.println("Failed to generate implementation code for given class " + e.getMessage());
        }
    }

    /**
     * Produces {@code .jar} file implementing interface specified by provided {@code token}.
     * Generated {@code .jar} file location is specified by {@code jarFile}.
     *
     * @param token type token to create implementation for.
     * @param jarFile target <var>.jar</var> file.
     * @throws ImplerException when implementation cannot be generated.
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        if (token == null || jarFile == null) {
            throw new ImplerException("Expected nonnull arguments");
        }

        Path parentPath = createParentDirectories(jarFile);
        Path tmpPath = createTempDirectories(parentPath);

        try {
            implement(token, tmpPath);
            compile(token, tmpPath);
            buildJar(token, jarFile, tmpPath);
        } finally {
            deleteDirectories(tmpPath.toFile());
        }
    }
}
