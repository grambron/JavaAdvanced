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
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public class ImplementorFileUtils {
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

    static Path createTempDirectories(Path path) throws ImplerException {
        Path tmpPath;
        try {
            tmpPath = Files.createTempDirectory(path, "tmp");
        } catch (IOException e) {
            throw new ImplerException("Unable to prepare temp directory" + e.getMessage());
        }
        return tmpPath;
    }

    static void deleteDirectories(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File subFile : files) {
                deleteDirectories(subFile);
            }
        }
        file.delete();
    }

    static String getPath(Class<?> token) {
        return String.join(File.separator, token.getPackageName().split("\\.")) +
                File.separator + token.getSimpleName();
    }

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

    static void compile(Class<?> token, Path tmpPath) throws ImplerException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new ImplerException("Can not find java compiler");
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
            throw new ImplerException("Can not find valid class path" + e.getMessage());
        }

        String[] compilerArgs = {
                "-cp",
                tmpPath.toString() + File.pathSeparator + originalPath.toString(),
                Path.of(tmpPath.toString(),
                        token.getPackageName().replace('.', File.separatorChar),
                        token.getSimpleName() + "Impl.java").toString()
        };
        if (compiler.run(null, null, null, compilerArgs) != 0) {
            throw new ImplerException("Implementation compilation returned nonzero code");
        }
    }
}
