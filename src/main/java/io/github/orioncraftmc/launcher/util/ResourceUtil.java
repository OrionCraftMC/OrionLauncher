package io.github.orioncraftmc.launcher.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;

public class ResourceUtil {

    // Find all resources in the classpath with a certain glob pattern
    public static Collection<String> findResources(String pattern) {
        ArrayList<String> resources = new ArrayList<>();

        String[] classPath = System.getProperty("java.class.path").split(System.getProperty("path.separator"));

        for (String classLoc : classPath) {
            Path uri = new File(classLoc).toPath();

            try {
                FileSystem fileSystem = Files.isDirectory(uri) ? FileSystems.getDefault() : FileSystems.newFileSystem(
                        uri);
                try {
                    PathMatcher matcher = fileSystem.getPathMatcher("glob:" + pattern);

                    Path path = fileSystem.getPath(Files.isDirectory(uri) ? uri.toAbsolutePath().toString() : "/");
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
                throw new RuntimeException(e);
            }

        }
        return resources;
    }

}
