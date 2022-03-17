package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class YamlParser {
    Yaml yaml;

    public YamlParser() {
        yaml = new Yaml();
    }

    public Map<String, Object> parseFile(InputStream stream) {
        return (Map<String, Object>) yaml.load(stream);
    }

    /**
     * Reads frontmatter, seperated on start at end with "---", and returns the yaml
     * formatted into a HashMap. The reader stops right after the last "---"
     * 
     * @param reader the file reader for the markdown post
     * @return a hashmap containg yaml mappings from the frontmatter
     */
    public Map<String, Object> parseFrontMatter(BufferedReader reader) throws IOException, Exception {
        String line = reader.readLine();
        String frontMatter = "";

        // detect frontmatter
        if (!line.equals("---")) {
            throw new Exception("No Frontmatter detected!");
        }

        // read frontmatter until another "---" is detected
        frontMatter = frontMatter.concat(line + "\n");
        line = reader.readLine();
        while (!line.equals("---")) {
            frontMatter = frontMatter.concat(line + "\n");
            line = reader.readLine();
        }
        return (Map<String, Object>) yaml.load(frontMatter);
    }

}
