package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

public class Post {
    private String title;
    private String markdownRawContent;
    private Path file;
    private Path layout;
    private String content;
    private Map<String, Object> mapping;

    /**
     * Generate a new Post based on a markdown file. Title will be generated based
     * on the filename seperated by dashes '-' if none is given in the frontmatter.
     * 
     * @param p path to the markdown file
     */
    public Post(Path p) throws IOException, Exception {
        this.file = p;
        System.out.println("Creating post for " + this.file.getFileName());

        // split frontmatter from the actual markdown content
        BufferedReader reader = Files.newBufferedReader(p);
        String frontMatter = "";
        markdownRawContent = "";

        // detect frontmatter
        String line = reader.readLine();
        if (!line.equals("---")) {
            throw new Exception("No Frontmatter detected!");
        }

        frontMatter = frontMatter.concat(line + "\n");
        line = reader.readLine();
        while (!line.equals("---")) {
            frontMatter = frontMatter.concat(line + "\n");
            line = reader.readLine();
        }
        // skip last frontmatter line
        line = reader.readLine();

        // parse frontmatter
        Load load = new Load(LoadSettings.builder().build());
        mapping = (Map<String, Object>) load.loadFromString(frontMatter);

        // read rest of the file
        while (!Objects.isNull(line)) {
            markdownRawContent = markdownRawContent.concat(line + "\n");
            line = reader.readLine();
        }
        reader.close();

        this.title = generateTitle();

        // get layout based on frontmatter
        layout = Paths.get(System.getProperty("user.dir"), "_layouts");

        if (!Objects.isNull(mapping) && !Objects.isNull(mapping.get("layout"))) {
            layout = layout.resolve((String) mapping.get("layout") + ".html");
        } else {
            layout = layout.resolve("post.html");
        }

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
            for (int i = 0; i < fileNameParts.length - 1; i++)
                fileName = fileName.concat(fileNameParts[i]);

            String[] titleParts = fileName.split("-");

            for (String part : titleParts) {
                title = title.concat(part.toUpperCase(Locale.ROOT) + " ");
            }
        } else {
            title = (String) mapping.get("title");
        }
        return title;
    }
}