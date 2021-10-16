package com.mikulex;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

public class MarkdownParser {

    public void parse(Post post) {
        Parser mdParser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        Node document = mdParser.parse(post.getMarkdownRawContent());
        String html = renderer.render(document);
        post.setContent(html);
    }
}