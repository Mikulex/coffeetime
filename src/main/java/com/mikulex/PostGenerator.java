package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat; // TODO: refactor to java.time package
import java.util.*;
import java.util.stream.Collectors;

public class PostGenerator {
    public Post generatePostData(Path p, ContentType type, SiteConfig config) {
        System.out.println("Creating post for " + p.getFileName());

        YamlParser yamlParser = new YamlParser();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        Map<String, Object> mapping = new HashMap<>();

        Post post = new Post();
        post.setSiteConfig(config);
        post.setFile(p);
        post.setType(type);
        post.setRelativeLink(generateRelativeLink(p, type, config));

        setMappingAndMarkdown(p, yamlParser, post);
        mapping = post.getVars();

        post.setTitle(generateTitle(mapping, p));
        post.setDate((Date) mapping.get("date"));

        // get layout based on frontmatter
        Path layoutPath = Paths.get(System.getProperty("user.dir"), "_layouts");

        if (!Objects.isNull(mapping) && mapping.containsKey("layout")) {
            layoutPath = layoutPath.resolve((String) mapping.get("layout") + ".html");
        } else if (type.equals(ContentType.POST)) {
            layoutPath = layoutPath.resolve("post.html");
        } else {
            layoutPath = layoutPath.resolve("page.html");
        }
        post.setLayout(layoutPath);
        return post;
    }

    private void setMappingAndMarkdown(Path p, YamlParser yamlParser, Post post) {
        Map<String, Object> mapping = new HashMap<>();
        String markdownRawContent = "";

        try (BufferedReader reader = Files.newBufferedReader(p)){
            mapping = yamlParser.parseFrontMatter(reader);

            // read rest of the file
            String line;
            while (!Objects.isNull(line = reader.readLine())) {
                markdownRawContent = markdownRawContent.concat(line + "\n");
            }

            post.setMapping(mapping);
            post.setMarkdownRawContent(markdownRawContent);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private String generateRelativeLink(Path p, ContentType type, SiteConfig siteConfig) {
        String[] fileNameSplit = p.getFileName().toString().split("\\.");
        fileNameSplit[fileNameSplit.length - 1] = ".html";
        String htmlFileName = "";
        for (String part : fileNameSplit) {
            htmlFileName = htmlFileName.concat(part);
        }
        switch (type) {
            case PAGE:
                return siteConfig.getSitePageFolderString() + htmlFileName;
            case POST:
                return siteConfig.getSitePostFolderString() + htmlFileName;
        }
        return "";
    }

    /**
     * Generate title based on frontmatter. It looks for the variable "title" in the
     * frontmatter or chooses to split the filename at the char '-' and uses that as
     * its title.
     *
     * @return a String with either the title set in the frontmatter or one based on
     *         the filename.
     */
    private String generateTitle(Map<String, Object> mapping, Path file) {
        String title;
        if (Objects.isNull(mapping) || Objects.isNull(mapping.get("title"))) {
            String[] fileNameParts = file.getFileName().toString().split("\\.");
            String fileName = "";

            // cut off file extension
            for (int i = 0; i < fileNameParts.length - 1; i++)
                fileName = fileName.concat(fileNameParts[i]);

            List<String> titleParts = List.of(fileName.split("-"));

            // capitalize first letters
            title = titleParts
                    .stream()
                    .map((part) -> part.substring(0, 1).toUpperCase() + part.substring(1))
                    .collect(Collectors.joining(" "));

        } else {
            title = (String) mapping.get("title");
        }
        return title;
    }
}
