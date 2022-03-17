package com.mikulex;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlFileGenerator {
    private MarkdownParser markdownParser;
    private Path siteFolder;
    private SiteConfig config;
    private Configuration templateConfig;

    public HtmlFileGenerator(MarkdownParser markdownParser, Path siteFolder, SiteConfig config, Configuration templateConfig){
        this.markdownParser = markdownParser;
        this.siteFolder = siteFolder;
        this.config = config;
        this.templateConfig = templateConfig;
    }

    public void createFile(Post post, Path target, List<Post> postList) {
        try {
            Map<String, Object> templateRoot = new HashMap<>();

            markdownParser.parse(post);

            Path relativeFile = target.toAbsolutePath().relativize(post.getFile());

            // Swap .md for .html
            String[] fileNameSplit = relativeFile.toString().split("\\.");
            fileNameSplit[fileNameSplit.length - 1] = ".html";
            String relativeHtmlFileName = "";
            for (String part : fileNameSplit) {
                relativeHtmlFileName = relativeHtmlFileName.concat(part);
            }

            // set file path according to content type
            Path newFile = Paths.get("");
            if (post.getType().equals(ContentType.POST)) {
                newFile = siteFolder.resolve(config.getSitePostFolderPath().resolve(relativeHtmlFileName));
                templateRoot.put("post", post);
            } else {
                newFile = siteFolder.resolve(config.getSitePageFolderPath().resolve(relativeHtmlFileName));
                templateRoot.put("page", post);
            }

            templateRoot.put("posts", postList);
            templateRoot.put("site", config.getConfig());

            Files.createDirectories(newFile.getParent());

            /* Get the template (uses cache internally) */
            Template template = templateConfig.getTemplate(post.getLayout().getFileName().toString());

            /* Merge data-model with template */
            Writer out = new FileWriter(newFile.toFile());
            template.process(templateRoot, out);
            out.close();
        } catch (Exception e) {
            System.err.println("Failed while creating post for " + post.getFile().getFileName().toAbsolutePath());
            System.err.println(e);
        }
    }
}
