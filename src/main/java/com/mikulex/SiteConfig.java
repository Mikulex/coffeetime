package com.mikulex;

import java.nio.file.Paths;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.InputStream;

public class SiteConfig {
    private String baseUrl;
    private String title;
    private Map<String, Object> config;

    public SiteConfig() throws IOException {
        Path dir = Paths.get(System.getProperty("user.dir"));
        Path file = Paths.get(dir.toString(), "config.yml");
        InputStream stream = Files.newInputStream(file);
        YamlParser parser = new YamlParser();

        this.config = parser.parseFile(stream);
        this.baseUrl = (String) config.get("baseUrl");
        this.title = (String) config.get("title");
        stream.close();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getTitle() {
        return title;
    }

    public Map<String, Object> getConfig() {
        return config;
    }
}