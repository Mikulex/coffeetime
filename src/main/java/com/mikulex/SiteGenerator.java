package com.mikulex;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class SiteGenerator {
    private SiteConfig config;
    private MarkdownParser markdownParser;
    private Path projectFolder;
    private Path postsFolder;
    private Path pagesFolder;
    private Path siteFolder;
    private Path assetsFolder;
    private Configuration templateConfig;
    private List<Post> postList;
    private PostGenerator postGenerator;
    private HtmlFileGenerator htmlFileGenerator;

    public SiteGenerator() throws IOException {
        config = new SiteConfig();
        projectFolder = Paths.get(System.getProperty("user.dir"));
        postsFolder = projectFolder.resolve("_posts");
        pagesFolder = projectFolder.resolve("_pages");
        siteFolder = projectFolder.resolve("_site");
        assetsFolder = projectFolder.resolve("assets");
        templateConfig = new Configuration(Configuration.VERSION_2_3_29);
        templateConfig.setDirectoryForTemplateLoading(projectFolder.resolve("_layouts").toFile());
        markdownParser = new MarkdownParser();
        postGenerator = new PostGenerator();
        htmlFileGenerator = new HtmlFileGenerator(markdownParser, siteFolder, config, templateConfig);
    }

    public void build() {
        cleanSiteFolder();
        generatePosts();
        generatePages();
        generateIndex();
        copyAssets();
    }

    private void cleanSiteFolder() {
        System.out.println("Clean site folder");
        try {
            Files.walkFileTree(siteFolder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                    if (e == null) {
                        if (!Files.isSameFile(dir, siteFolder)) {
                            Files.delete(dir);
                        }
                        return FileVisitResult.CONTINUE;
                    } else {
                        // directory iteration failed
                        throw e;
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Failed while trying to clean _site directory");
            System.err.println(e);
        }

    }

    /**
     * Generates an index file in the website root if no page was generated for it.
     */
    private void generateIndex() {
        Path index = siteFolder.resolve("index.html");

        if (Files.notExists(index)) {
            System.out.println("Generating index.html");
            Map<String, Object> root = new HashMap<>();
            root.put("posts", postList);
            root.put("site", config.getConfig());
            try {
                Template template = templateConfig.getTemplate("index.html");
                Writer out = new FileWriter(index.toFile());
                template.process(root, out);
                out.close();
            } catch (Exception e) {
                System.err.println("Failed while generating index");
                System.err.println(e);
            }
        } else {
            System.out.println("index.html found");
        }

    }

    private void generatePosts() {
        this.postList = collectPosts();
        System.out.println("Converting posts");
        for (Post post : this.postList) {
            htmlFileGenerator.createFile(post, postsFolder, this.postList);
        }

    }

    private void generatePages() {
        List<Post> pages = collectPages();
        System.out.println("Converting pages");
        for (Post page : pages) {
            htmlFileGenerator.createFile(page, pagesFolder, this.postList);
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

            postFiles = Files.walk(postsFolder)
                    .filter(Files::isRegularFile)
                    .filter(file -> file.toString().toLowerCase().endsWith(".md"))
                    .collect(Collectors.toCollection(ArrayList::new));

        } catch (Exception e) {
            System.err.println("Failed to collect posts! Aborting");
            System.err.println(e);
            System.exit(1);
        }

        for (Path p : postFiles) {
            try {
                Post post = postGenerator.generatePostData(p, ContentType.POST, config);
                posts.add(post);
            } catch (Exception e) {
                System.err.println("Failed to create post for " + p.toAbsolutePath() + " ! Skipping file");
                System.err.println(e);
            }
        }
        System.out.println("Post count: " + posts.size());

        posts.sort(Comparator.comparing(Post::getDate));
        return posts;
    }

    /**
     * Look for Pages
     * 
     * @return
     */
    private List<Post> collectPages() {
        List<Path> pageFiles = new ArrayList<>();
        List<Post> pages = new ArrayList<>();

        try {
            pageFiles = Files.list(pagesFolder).filter(Files::isRegularFile)
                    .filter(file -> file.toString().toLowerCase().endsWith(".md"))
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (Exception e) {
            System.err.println(e);
        }

        for (Path p : pageFiles) {
            try {
                Post page = postGenerator.generatePostData(p, ContentType.PAGE, config);
                pages.add(page);

            } catch (Exception e) {
                System.err.println("Failed create page for " + p.toAbsolutePath());
                System.err.println(e);
            }
        }
        return pages;

    }

    private void copyAssets() {
        Path siteAssets = siteFolder.resolve("assets");
        try {
            Files.walkFileTree(assetsFolder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetdir = siteAssets.resolve(assetsFolder.relativize(dir));
                    Files.copy(dir, targetdir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, siteAssets.resolve(assetsFolder.relativize(file)));
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e) {
            System.err.println("Failed while trying to copying assets");
            System.err.println(e);
        }

    }
    
}
