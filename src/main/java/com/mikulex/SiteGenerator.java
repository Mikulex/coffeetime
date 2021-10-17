package com.mikulex;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class SiteGenerator {
    private SiteConfig config;
    private MarkdownParser markdownParser;
    private Path root;
    private Path postsFolder;
    private Path pagesFolder;
    private Path siteFolder;
    private Configuration templateConfig;
    private List<Post> postList;

    public SiteGenerator() throws IOException {
        config = new SiteConfig();
        root = Paths.get(System.getProperty("user.dir"));
        postsFolder = root.resolve("_posts");
        pagesFolder = root.resolve("_pages");
        siteFolder = root.resolve("_site");
        templateConfig = new Configuration(Configuration.VERSION_2_3_29);
        templateConfig.setDirectoryForTemplateLoading(root.resolve("_layouts").toFile());
        markdownParser = new MarkdownParser();
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
        this.postList = collectPosts();
        System.out.println("Converting posts");
        for (Post post : this.postList) {
            createFile(post, postsFolder);
        }

    }

    private void generatePages() {
        List<Post> pages = collectPages();
        System.out.println("Converting pages");
        for (Post page : pages) {
            createFile(page, pagesFolder);
        }
    }

    private void createFile(Post post, Path target) {
        try {
            Map<String, Object> root = new HashMap<>();

            markdownParser.parse(post);

            Path relativeFile = target.toAbsolutePath().relativize(post.getFile());

            // Swap .md for .html
            String[] fileNameSplit = relativeFile.toString().split("\\.");
            fileNameSplit[fileNameSplit.length - 1] = ".html";
            String fileName = "";
            for (String part : fileNameSplit) {
                fileName = fileName.concat(part);
            }

            Path newFile = Paths.get("");
            if (target.endsWith("_posts")) {
                newFile = siteFolder.resolve(config.getSitePostFolderPath().resolve(fileName));
            } else {
                newFile = siteFolder.resolve(config.getSitePageFolderPath().resolve(fileName));
            }

            Files.createDirectories(newFile.getParent());

            // prepare map for template engine
            root.put("post", post);
            root.put("posts", this.postList);

            /* Get the template (uses cache internally) */
            Template template = templateConfig.getTemplate(post.getLayout().getFileName().toString());

            /* Merge data-model with template */
            Writer out = new FileWriter(newFile.toFile());
            template.process(root, out);
            out.close();
        } catch (Exception e) {
            System.err.println("Failed while creating post for " + post.getFile().getFileName().toAbsolutePath());
            System.err.println(e);
        }
    }

    /**
     * Find all markdown files in the _posts directory and its subdirectories,
     * create Post objects for them and return a List<Post>, sorted by date found in
     * the frontmatter.
     * 
     * @return a List containing Posts sorted by Date
     */
    private List<Post> collectPosts() {
        List<Path> postFiles = new ArrayList<>();
        List<Post> posts = new ArrayList<>();

        System.out.println("Collecting posts in " + postsFolder.getFileName());
        try {
            if (Files.notExists(postsFolder) || !Files.isDirectory(postsFolder)) {
                throw new FileNotFoundException("Missing directory: " + postsFolder.toAbsolutePath());
            }

            postFiles = Files.walk(postsFolder).filter(Files::isRegularFile)
                    .filter(file -> file.toString().toLowerCase().endsWith(".md"))
                    .collect(Collectors.toCollection(ArrayList::new));

        } catch (Exception e) {
            System.err.println("Failed to collect posts! Aborting");
            System.err.println(e);
            System.exit(1);
        }

        for (Path p : postFiles) {
            try {
                posts.add(new Post(p, ContentType.POST, config));
            } catch (Exception e) {
                System.err.println("Failed to create post for " + p.toAbsolutePath() + " ! Skipping file");
                System.err.println(e);
            }
        }
        System.out.println("Post count: " + posts.size());

        posts.sort(Comparator.comparing(Post::getDate));
        return posts;
    }

    private List<Post> collectPages() {
        List<Path> pageFiles = new ArrayList<>();
        List<Post> pages = new ArrayList<>();
        try {
            pageFiles = Files.list(siteFolder).filter(Files::isRegularFile)
                    .filter(file -> file.toString().toLowerCase().endsWith(".md"))
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (Exception e) {
            System.err.println(e);
        }

        for (Path p : pageFiles) {
            try {
                pages.add(new Post(p, ContentType.PAGE, config));

            } catch (Exception e) {
                System.err.println("Failed create page for " + p.toAbsolutePath());
                System.err.println(e);
            }
        }
        return pages;

    }

    public void build() {
        cleanSiteFolder();
        generatePosts();
        generatePages();
    }
}
