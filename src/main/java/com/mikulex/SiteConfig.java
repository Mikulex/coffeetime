package com.mikulex;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.File;
import java.io.IOException;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;

public class SiteConfig {
    private String baseUrl;
    private String title;
    private YamlMapping config;

    public SiteConfig() throws IOException {
        Path dir = Paths.get(System.getProperty("user.dir"));
        Path file = Paths.get(dir.toString(), "config.yml");

        YamlMapping config = Yaml.createYamlInput(new File(file.toString())).readYamlMapping();
        this.config = config;
        this.baseUrl = config.string("baseUrl");
        this.title = config.string("title");

    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getTitle() {
        return title;
    }

    public YamlMapping getConfig() {
        return config;
    }
}