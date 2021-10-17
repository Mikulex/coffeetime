package com.mikulex;

import java.nio.file.Paths;
import java.util.Map;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.InputStream;

public class SiteConfig {
    private String baseUrl;
    private String title;
    private Map<String, Object> config;
    private String sitePostFolder;
    private String sitePageFolder;

    public SiteConfig() throws IOException {
        Path dir = Paths.get(System.getProperty("user.dir"));
        Path file = Paths.get(dir.toString(), "config.yml");
        InputStream stream = Files.newInputStream(file);
        YamlParser parser = new YamlParser();

        this.config = parser.parseFile(stream);
        this.baseUrl = (String) config.get("baseUrl");
        this.title = (String) config.get("title");
        this.sitePageFolder = (String) config.get("pages");
        this.sitePostFolder = (String) config.get("posts");
        stream.close();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getSitePageFolderString() {
        return sitePageFolder;
    }

    public String getSitePostFolderString() {
        return sitePostFolder;
    }

    public Path getSitePageFolderPath() {
        String[] parts = sitePageFolder.split("/");
        Path result = Paths.get("");
        for (String part : parts) {
            result = result.resolve(part);
        }
        return result;
    }

    public Path getSitePostFolderPath() {
        String[] parts = sitePostFolder.split("/");
        Path result = Paths.get("");
        for (String part : parts) {
            result = result.resolve(part);
        }
        return result;
    }

    public Map<String, Object> getConfig() {
        return config;
    }
}