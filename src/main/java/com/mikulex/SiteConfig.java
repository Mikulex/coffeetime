package com.mikulex;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.File;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;

public class SiteConfig {
    private String baseUrl;
    private String name;
    private String title;

    public SiteConfig() {
        Path dir = Paths.get(System.getProperty("user.dir"));
        Path file = Paths.get(dir.toString(), "config.yml");
        try {
            YamlMapping config = Yaml.createYamlInput(new File(file.toString())).readYamlMapping();
            this.baseUrl = config.string("baseUrl");
            this.title = config.string("title");
        } catch (Exception e) {
            System.err.println("Failed to read config.yml\n" + e.getMessage());
        }

    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }
}