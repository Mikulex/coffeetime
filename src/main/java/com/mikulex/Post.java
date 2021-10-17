package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class Post {
    private String title;
    private String markdownRawContent;
    private Path file;
    private Path layout;
    private String content;
    private Map<String, Object> mapping;
    private Date date;
    private String relativeLink;
    private ContentType type;
    private SiteConfig siteConfig;

    /**
     * Generate a new Post based on a markdown file. Title will be generated based
     * on the filename seperated by dashes '-' if none is given in the frontmatter.
     * 
     * @param p path to the markdown file
     */
    public Post(Path p, ContentType type, SiteConfig siteConfig) throws IOException, Exception {
        System.out.println("Creating post for " + p.getFileName());

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        BufferedReader reader = Files.newBufferedReader(p);
        YamlParser yamlParser = new YamlParser();

        markdownRawContent = "";
        this.siteConfig = siteConfig;
        this.file = p;
        this.type = type;
        this.mapping = yamlParser.parseFrontMatter(reader);
        this.relativeLink = this.generateRelativeLink();
        // skip last "---" from the frontmatter
        String line = reader.readLine();

        // read rest of the file
        while (!Objects.isNull(line)) {
            this.markdownRawContent = markdownRawContent.concat(line + "\n");
            line = reader.readLine();
        }
        reader.close();

        this.title = generateTitle();
        this.date = df.parse((String) mapping.get("date"));

        // get layout based on frontmatter
        this.layout = Paths.get(System.getProperty("user.dir"), "_layouts");

        if (!Objects.isNull(mapping) && mapping.containsKey("layout")) {
            this.layout = layout.resolve((String) mapping.get("layout") + ".html");
        } else if (type.equals(ContentType.POST)) {
            this.layout = layout.resolve("post.html");
        } else {
            this.layout = layout.resolve("page.html");
        }
    }

    private String generateRelativeLink() {
        switch (type) {
            case PAGE:
                return siteConfig.getSitePageFolderString() + file.getFileName();
            case POST:
                return siteConfig.getSitePostFolderString() + file.getFileName();
        }
        return "";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Path getLayout() {
        return layout;
    }

    public String getTitle() {
        return title;
    }

    public String getMarkdownRawContent() {
        return markdownRawContent;
    }

    public Map<String, Object> getMapping() {
        return mapping;
    }

    public Path getFile() {
        return file;
    }

    public Date getDate() {
        return date;
    }

    /**
     * Generate title based on frontmatter. It looks for the variable "title" in the
     * frontmatter or chooses to split the filename at the char '-' and uses that as
     * its title.
     * 
     * @return a String with either the title set in the frontmatter or one based on
     *         the filename.
     */
    private String generateTitle() {
        String title = "";
        if (Objects.isNull(mapping) || Objects.isNull(mapping.get("title"))) {
            String[] fileNameParts = this.file.getFileName().toString().split("\\.");
            String fileName = "";

            // cut off file extension
            for (int i = 0; i < fileNameParts.length - 1; i++)
                fileName = fileName.concat(fileNameParts[i]);

            String[] titleParts = fileName.split("-");

            // capitalize first letters
            for (String part : titleParts) {
                part = part.substring(0, 1).toUpperCase() + part.substring(1);
            }
            title = String.join(" ", titleParts);
        } else {
            title = (String) mapping.get("title");
        }
        return title;
    }

    public String getRelativeLink() {
        return relativeLink;
    }

    public ContentType getType() {
        return type;
    }

}