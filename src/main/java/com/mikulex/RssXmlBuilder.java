package com.mikulex;

import org.apache.commons.text.StringEscapeUtils;

public class RssXmlBuilder {
    private StringBuilder xmlContent;

    public RssXmlBuilder(){
        reset();
    }

    public void reset(){
        xmlContent = new StringBuilder();
        xmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        xmlContent.append("<feed xmlns=\"http://www.w3.org/2005/Atom\">\n");
    }

    public RssXmlBuilder addPageInfo(String title, String link, String subtitle, String updated, String author){
        xmlContent.append(getTag("title", title));
        xmlContent.append(getLinkTag(link));
        xmlContent.append(getTag("updated", updated));
        xmlContent.append(getTag("subtitle", subtitle));
        xmlContent.append(getTag("id", link));
        xmlContent.append(getAuthorTag(author));
        return this;
    }

    public RssXmlBuilder addEntry(String title, String link, String content, String updated){
        xmlContent.append("<entry>\n");
        xmlContent.append(getTag("title", title));
        xmlContent.append(getLinkTag(link));
        xmlContent.append(getTag("updated", updated));
        xmlContent.append(getContentTag(content));
        xmlContent.append(getTag("id", link));
        xmlContent.append("</entry>\n");
        return this;
    }

    public String createXmlContent(){
        xmlContent.append("</feed>");
        String result = xmlContent.toString();
        reset();
        return result;
    }

    private String getTag(String tagName, String content){
        return String.format("<%s>%s</%s>\n", tagName, content, tagName);
    }

    private String getLinkTag(String link){
        return String.format("<link href=\"%s\"></link>\n", link);
    }

    private String getContentTag(String content){
        return String.format("<content type=\"html\">%s</content>\n", StringEscapeUtils.escapeXml10(content));
    }

    private String getAuthorTag(String author){
        return String.format("<author><name>%s</name></author>", author);
    }
}
