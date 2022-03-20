package com.mikulex;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProjectGenerator {

    public void generateSite(String name) {
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Path projectPath = currentDir.resolve(name);
        try {

            // Create base directory
            if (Files.exists(projectPath)) {
                if (!Files.list(projectPath).findAny().isPresent()) {
                    throw new FileAlreadyExistsException(projectPath + "already exists and is not empty!");
                }
            } else {
                Files.createDirectory(projectPath);
            }

            // Create underlying directories
            copyResources(projectPath);

        } catch (Exception e) {
            System.err.println("Failed while creating the base directory");
            System.err.println(e);
            System.exit(1);
        }
    }

    private void copyResources(Path target) throws URISyntaxException, IOException {
        URI resource = getClass().getResource("").toURI();
        FileSystem fileSystem = FileSystems.newFileSystem(resource, new HashMap<String, String>());

        final Path jarPath = fileSystem.getPath("/defaults");

        Files.walkFileTree(jarPath, new SimpleFileVisitor<Path>() {

            private Path currentTarget;

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                currentTarget = target.resolve(jarPath.relativize(dir).toString());
                Files.createDirectories(currentTarget);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(jarPath.relativize(file).toString()),
                        StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }

        });
    }

    public void generateFile(String title, String directory) {
        Path directoryPath = Paths.get(System.getProperty("user.dir"), directory);
        String fileName = title.concat(".md");
        String[] parts = title.split("-");
        Path file = directoryPath.resolve(fileName);

        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
        }

        String newTitle = String.join(" ", parts);

        if (Files.exists(directoryPath)) {
            try {
                // get ISO-8601 time format
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no
                                                                               // timezone offset

                BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()));
                writer.write("---\n");
                writer.write("date: " + df.format(new Date()) + "\n");
                writer.write("title: " + newTitle + "\n");
                writer.write("---");
                writer.flush();
                writer.close();
                System.out.println("File " + file.getFileName() + " generated");
            } catch (Exception e) {
                System.err.println("Failed to create " + file.toAbsolutePath());
                System.err.println(e);
            }
        } else {
            System.err.println("Directory " + directory + " not found!");
            System.exit(1);
        }
    }
}