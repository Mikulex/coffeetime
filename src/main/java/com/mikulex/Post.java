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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSiteConfig(SiteConfig config) { this.siteConfig = config; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMarkdownRawContent(String markdownRawContent) {
        this.markdownRawContent = markdownRawContent;
    }

    public void setFile(Path file) {
        this.file = file;
    }

    public void setLayout(Path layout) {
        this.layout = layout;
    }

    public void setMapping(Map<String, Object> mapping) {
        this.mapping = mapping;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setRelativeLink(String relativeLink) {
        this.relativeLink = relativeLink;
    }

    public void setType(ContentType type) {
        this.type = type;
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

    public Map<String, Object> getVars() {
        return mapping;
    }

    public Path getFile() {
        return file;
    }

    public Date getDate() {
        return date;
    }

    public String getRelativeLink() {
        return relativeLink;
    }

    public ContentType getType() {
        return type;
    }


}