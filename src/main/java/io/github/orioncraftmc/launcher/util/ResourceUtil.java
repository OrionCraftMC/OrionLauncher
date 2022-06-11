package io.github.orioncraftmc.launcher.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class ResourceUtil {

    // Find all resources in the classpath with a certain glob pattern
    public static Collection<String> findResources(String pattern) {
        ArrayList<String> resources = new ArrayList<>();

        Path[] classPath = getClassPath();

        for (Path file : classPath) {
            resources.addAll(findResources(file, pattern));
        }
        return resources;
    }

    public static ArrayList<String> findResources(Path file, String pattern) {
        ArrayList<String> resources = new ArrayList<>();
        try {
            FileSystem fileSystem = Files.isDirectory(file) ? FileSystems.getDefault() : FileSystems.newFileSystem(
                    file);
            try {
                PathMatcher matcher = fileSystem.getPathMatcher("glob:" + pattern);

                Path path = fileSystem.getPath(Files.isDirectory(file) ? file.toAbsolutePath().toString() : "/");
                FileVisitor<Path> visitor = new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        if (matcher.matches(file)) {
                            resources.add(file.getFileName().toString());
                        }
                        return FileVisitResult.CONTINUE;
                    }
                };
                Files.walkFileTree(path, visitor);
            } finally {
                if (fileSystem != FileSystems.getDefault()) fileSystem.close();
            }
        } catch (IOException e) {
            // Ignore
        }

        return resources;
    }

    public static Path[] getClassPath() {
        return Arrays.stream(System.getProperty("java.class.path").split(System.getProperty("path.separator")))
                .map(File::new)
                .filter(File::exists)
                .map(File::toPath)
                .toArray(Path[]::new);
    }

}
