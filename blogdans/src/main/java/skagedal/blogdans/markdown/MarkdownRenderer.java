package skagedal.blogdans.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.util.List;

public class MarkdownRenderer {
    final private DataHolder options = new MutableDataSet().set(
        Parser.EXTENSIONS, List.of(
            SyntaxHighlightingExtension.create()
        ));
    final Parser parser = Parser.builder(options).build();
    final HtmlRenderer renderer = HtmlRenderer.builder(options).build();

    public String render(String markdown) {
        final var document = parser.parse(markdown);
        return renderer.render(document);
    }
}
