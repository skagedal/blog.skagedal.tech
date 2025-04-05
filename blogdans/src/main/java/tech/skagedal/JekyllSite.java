package tech.skagedal;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;
import liqp.antlr.CharStreamWithLocation;
import liqp.antlr.NameResolver;
import liqp.parser.Flavor;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class JekyllSite {
    private static final Logger log = LoggerFactory.getLogger(JekyllSite.class);
    private final Path jekyllRoot;
    private final TemplateParser templateParser = new TemplateParser.Builder()
        .withFlavor(Flavor.JEKYLL)
        .withNameResolver(new NameResolver() {
            @Override
            public CharStreamWithLocation resolve(final String includeName) throws IOException {
                log.info("JekyllSite resolving " + includeName);
                return new CharStreamWithLocation(jekyllRoot.resolve("_includes").resolve(includeName));
            }
        })
        .build();

    public JekyllSite(final Path jekyllRoot) {
        this.jekyllRoot = jekyllRoot;
    }

    public Path indexPath() {
        return jekyllRoot.resolve("index.html");
    }

    public Path layoutsPath() {
        return jekyllRoot.resolve("_layouts");
    }

    public @Language("HTML") String renderHtml(final Path path) {
        final var content = readFile(path);
        final var frontMatter = FrontMatterSeparated.split(content);
        final var layoutPath = layoutsPath().resolve(frontMatter.frontMatter().layout() + ".html");
        final var template = readTemplate(layoutPath);
        try {
            return template.render(Map.of(
                "content", frontMatter.content()
            ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return e.getMessage();
        }
//        return """
//            <!DOCTYPE html>
//            <html lang="en">
//                <body>
//                    <h1>%s</h1>
//                    <code>
//                        <pre>
//            %s
//                        </pre>
//                    </code>
//                </body>
//            </html>
//            """.formatted(frontMatter.frontMatter().layout(), escapeHtml(frontMatter.content()));
    }

    private Template readTemplate(final Path path) {
        try {
            return templateParser.parse(path);
//            new TemplateContext(Map.of()).
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read template: " + path, e);
        }
    }

    private String escapeHtml(String content) {
        return content
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }

    private String readFile(final Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
