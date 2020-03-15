package ru.ifmo.badikova.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 *Class providing tools for operations with file system for {@link Implementor}
 *
 * @author Anastasiia Badikova
 * @version 1.0
 */
public class ImplementorFileUtils {
    /**
     * Creates missing parent directories of given path.
     * @param path {@link Path} to generate parent directories
     * @return Generated parent directory
     * @throws ImplerException when unable to prepare source directory
     */
    static Path createParentDirectories(Path path) throws ImplerException {
        Path parentPath = path.toAbsolutePath().normalize().getParent();
        if (parentPath != null) {
            try {
                Files.createDirectories(parentPath);
            } catch (IOException e) {
                throw new ImplerException("Unable to prepare source directory " + e.getMessage());
            }
        }
        return parentPath;
    }

    /**
     *Creates temp directories in given {@link Path}.
     * @param path where temp directories needed to create
     * @return {@link Path} of created temporary directory
     * @throws ImplerException when unable to prepare temp directory
     */
    static Path createTempDirectories(Path path) throws ImplerException {
        Path tmpPath;
        try {
            tmpPath = Files.createTempDirectory(path, "tmp");
        } catch (IOException e) {
            throw new ImplerException("Unable to prepare temp directory" + e.getMessage());
        }
        return tmpPath;
    }

    /**
     * Deletes all files of directory {@link File} recursively.
     * @param file target directory {@link Path}
     */
    static void deleteDirectories(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File subFile : files) {
                deleteDirectories(subFile);
            }
        }
        file.delete();
    }

    /**
     *Generates full name of given {@link Path}
     * @param token {@link Class} to get name
     * @return {@link String} with full name of given {@link Path}
     */
    static String getPath(Class<?> token) {
        return String.join("/", token.getPackageName().split("\\.")) +
                "/" + token.getSimpleName();
    }

    /**
     *Creates {@code .jar} containing containing compiled implementation of {@code token}.
     * @param token {@link Class} to pack implementation
     * @param jarPath {@link Path} where result code will be created
     * @param tmpPath {@link Path} where source code is stored
     * @throws ImplerException when unable to write jar
     */
    static void buildJar(Class<?> token, Path jarPath, Path tmpPath) throws ImplerException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        try (JarOutputStream stream = new JarOutputStream(Files.newOutputStream(jarPath), manifest)) {
            String name = getPath(token) + "Impl.class";
            stream.putNextEntry(new ZipEntry(name));
            Files.copy(Paths.get(tmpPath.toString(), name), stream);
        } catch (IOException e) {
            throw new ImplerException("Unable to write jar " + e.getMessage());
        }
    }

    /**
     * Compiles code of token implementation stored in temporary directory.
     * @param token {@link Class} to compile
     * @param tmpPath {@link Path} of directory where implementation code source will be stored
     * @throws ImplerException in case implementation compilation returned nonzero code
     * @throws ImplerException in case cannot find java compiler
     * @throws ImplerException in case cannot find valid class path
     */
    static void compile(Class<?> token, Path tmpPath) throws ImplerException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new ImplerException("Cannot find java compiler");
        }

        Path originalPath;
        try {
            CodeSource codeSource = token.getProtectionDomain().getCodeSource();
            String uri =  codeSource == null ? "" : codeSource.getLocation().getPath();
            if (uri.startsWith("/")) {
                uri = uri.substring(1);
            }
            originalPath = Path.of(uri);
        } catch (InvalidPathException e) {
            throw new ImplerException("Cannot find valid class path: " + e.getMessage());
        }

        String[] compilerArgs = {
                "-cp",
                tmpPath.toString() + File.pathSeparator + originalPath.toString(),
                Path.of(tmpPath.toString(),
                        token.getPackageName().replace('.', File.separatorChar),
                        token.getSimpleName() + "Impl.java").toString()
        };
        System.out.println(Arrays.toString(compilerArgs));
        if (compiler.run(null, null, null, compilerArgs) != 0) {
            throw new ImplerException("Implementation compilation returned nonzero code");
        }
    }
}
