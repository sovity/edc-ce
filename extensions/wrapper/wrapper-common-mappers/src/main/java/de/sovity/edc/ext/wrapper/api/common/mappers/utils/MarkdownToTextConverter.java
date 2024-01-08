package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jsoup.Jsoup;

public class MarkdownToTextConverter {
    String extractText(String markdown) {
        var options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Node document = parser.parse(markdown);
        String html = renderer.render(document);
        var plainText = Jsoup.parse(html).text();
        return Jsoup.parse(plainText).text().substring(0, Math.min(300, plainText.length()));
    }
}
