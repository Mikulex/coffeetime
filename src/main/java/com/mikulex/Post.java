package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;

public class Post {
    private String title;
    private String markdownRawContent;
    private Path file;
    YamlMapping mapping;

    /**
     * Generate a new Post based on a markdown file. Title will be generated based
     * on the filename seperated by dashes '-' if none is given in the frontmatter.
     * 
     * @param p path to the markdown file
     */
    public Post(Path p) throws IOException, Exception {
        System.out.println("Creating post for " + p.getFileName());
        this.file = p;

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
        mapping = Yaml.createYamlInput(frontMatter).readYamlMapping();

        while (!Objects.isNull(line)) {
            markdownRawContent = markdownRawContent.concat(line + "\n");
            line = reader.readLine();
        }
        reader.close();

        // Generate title if none was given in the frontmatter
        String title = "";
        if (Objects.isNull(mapping.string("title"))) {
            String[] fileNameParts = p.getFileName().toString().split("\\.");
            String fileName = "";
            for (int i = 0; i < fileNameParts.length - 1; i++)
                fileName = fileName.concat(fileNameParts[i]);

            String[] titleParts = fileName.split("-");

            for (String part : titleParts) {
                title = title.concat(part.toUpperCase(Locale.ROOT) + " ");
            }
        } else {
            title = mapping.string("title");
        }
        this.title = title;

    }

    public String getTitle() {
        return title;
    }

    public String getMarkdownRawContent() {
        return markdownRawContent;
    }

    public YamlMapping getMapping() {
        return mapping;
    }

    public Path getFile() {
        return file;
    }
}