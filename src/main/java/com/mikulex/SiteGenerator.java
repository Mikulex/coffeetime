package com.mikulex;

import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

public class SiteGenerator {
    private SiteConfig config;
    private Path root;
    private Parser mdParser;
    private HtmlRenderer renderer;
    private Path postsFolder;
    private Path pagesFolder;
    private Path siteFolder;

    public SiteGenerator() {
        config = new SiteConfig();
        root = Paths.get(System.getProperty("user.dir"));
        postsFolder = root.resolve("_posts");
        pagesFolder = root.resolve("_pages");
        siteFolder = root.resolve("_site");
        mdParser = Parser.builder().build();
        renderer = HtmlRenderer.builder().build();
    }

    private void cleanSiteFolder() {
        System.out.println("Clean site folder");
        if (Files.exists(siteFolder) && Files.isDirectory(siteFolder)) {
            try {
                Files.walk(siteFolder).sorted(Comparator.reverseOrder()).map(path -> path.toFile())
                        .forEach(path -> path.delete());
            } catch (Exception e) {
                System.err.println("Error while traversing " + siteFolder.toAbsolutePath());
                System.err.println("Aborting!");
                System.err.println(e);
                System.exit(1);
            }
        }

        try {
            Files.createDirectory(siteFolder);
        } catch (Exception e) {
            System.err.println("Failed to create " + siteFolder.toAbsolutePath());
            System.err.println("Aborting!");
            System.err.println(e);
            System.exit(1);
        }
    }

    private void generatePosts() {
        List<Path> posts = collectPosts();
        System.out.println("Converting posts");
        for (Path post : posts) {
            createFile(post, postsFolder);
        }

    }

    private void generatePages() {
        List<Path> pages = collectPages();
        System.out.println("Converting pages");
        for (Path page : pages) {
            createFile(page, pagesFolder);
        }
    }

    private void createFile(Path file, Path target) {
        System.out.println("Creating post for " + file);
        try {
            Node document = mdParser.parse(Files.readString(file));
            String html = renderer.render(document);

            Path relativeFile = target.toAbsolutePath().relativize(file);

            String[] fileNameSplit = relativeFile.toString().split("\\.");
            fileNameSplit[fileNameSplit.length - 1] = ".html";
            String fileName = "";
            for (String part : fileNameSplit) {
                fileName = fileName.concat(part);
            }
            Path newFile = siteFolder.resolve(fileName);

            Files.createDirectories(newFile.getParent());
            Files.createFile(newFile);
            Files.writeString(newFile, html, StandardOpenOption.WRITE);
        } catch (Exception e) {
            System.err.println("Failed while creating post for " + file.toAbsolutePath());
            System.err.println(e);
        }
    }

    private List<Path> collectPosts() {
        List<Path> posts = new ArrayList<>();

        System.out.println("Collecting posts in " + postsFolder.getFileName());
        try {
            if (Files.notExists(postsFolder) || !Files.isDirectory(postsFolder)) {
                throw new FileNotFoundException("Missing directory: " + postsFolder.toAbsolutePath());
            }

            posts = Files.walk(postsFolder).filter(Files::isRegularFile)
                    .filter(file -> file.toString().toLowerCase().endsWith(".md"))
                    .collect(Collectors.toCollection(ArrayList::new));

        } catch (Exception e) {
            System.err.println("Failed to collect posts!");
            System.err.println(e);
            System.err.println("Aborting");
            System.exit(1);
        }
        System.out.println("Post count: " + posts.size());
        return posts;

    }

    private List<Path> collectPages() {
        List<Path> pages = new ArrayList<>();
        try {
            pages = Files.list(Paths.get(System.getProperty("user.dir"))).filter(Files::isRegularFile)
                    .filter(file -> file.endsWith(".md")).collect(Collectors.toCollection(ArrayList::new));
        } catch (Exception e) {
            System.err.println(e);
        }
        return pages;

    }

    public void build() {
        cleanSiteFolder();
        generatePosts();
    }
}
