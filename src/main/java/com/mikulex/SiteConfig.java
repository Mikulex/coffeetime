package com.mikulex;

import java.nio.file.Paths;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.InputStream;

import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

public class SiteConfig {
    private String baseUrl;
    private String title;
    private Map<String, Object> config;

    public SiteConfig() throws IOException {
        Path dir = Paths.get(System.getProperty("user.dir"));
        Path file = Paths.get(dir.toString(), "config.yml");
        InputStream stream = Files.newInputStream(file);

        Load load = new Load(LoadSettings.builder().build());
        this.config = (Map<String, Object>) load.loadFromInputStream(stream);
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