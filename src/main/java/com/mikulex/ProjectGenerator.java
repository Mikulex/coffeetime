package com.mikulex;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProjectGenerator {

    public static void generateSite(String name) {
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
            Files.createDirectory(projectPath.resolve("_posts"));
            Files.createDirectory(projectPath.resolve("_site"));
            Files.createDirectory(projectPath.resolve("_layouts"));

            // Create Files
            Files.createFile(projectPath.resolve("config.yaml"));
        } catch (Exception e) {
            System.err.println("Failed while creating the base directory");
            System.err.println(e);
            System.exit(1);
        }
    }

    public static void generateFile(String title, String directory) {
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
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no
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