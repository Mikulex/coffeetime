package com.mikulex;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
            Files.createDirectory(projectPath.resolve("_pages"));
            Files.createDirectory(projectPath.resolve("_site"));

            // Create Files
            Files.createFile(projectPath.resolve("config.yaml"));
        } catch (Exception e) {
            System.err.println("Failed while creating the base directory");
            System.err.println(e);
        }
    }
}