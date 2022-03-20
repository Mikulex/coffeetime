package com.mikulex;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RssFeedGenerator {
    private RssXmlBuilder builder;
    private SiteConfig siteConfig;
    private String date;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no
    private Path siteFolder = Paths.get(System.getProperty("user.dir"), "_site");


    public RssFeedGenerator(SiteConfig siteConfig){
        this.siteConfig = siteConfig;
        this.builder = new RssXmlBuilder();
        this.date = df.format(new Date());
    }
    public void generate(List<Post> postList){
        builder.reset();
        String description = (String) siteConfig.getConfig().getOrDefault("rssDescription", "");

        builder.addPageInfo(
                siteConfig.getTitle(),
                siteConfig.getBaseUrl(),
                description,
                date,
                (String) siteConfig.getConfig().get("rssAuthor"));

        postList.forEach(p -> builder.addEntry(p.getTitle(), siteConfig.getBaseUrl()+p.getRelativeLink(), p.getContent(), df.format(p.getDate())));

        String content = builder.createXmlContent();
        try(FileWriter writer = new FileWriter(siteFolder.resolve("mikulex-atom.xml").toString())){
            writer.write(content);
        } catch (IOException e){
            System.err.println(e);
        }
    }
}
