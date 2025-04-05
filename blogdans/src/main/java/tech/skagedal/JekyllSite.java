package tech.skagedal;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;
import liqp.antlr.CharStreamWithLocation;
import liqp.antlr.NameResolver;
import liqp.filters.Filter;
import liqp.parser.Flavor;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public class JekyllSite {
    private static final Logger log = LoggerFactory.getLogger(JekyllSite.class);
    private final Path jekyllRoot;
    private final TemplateParser templateParser = new TemplateParser.Builder()
        .withFlavor(Flavor.JEKYLL)
        .withNameResolver(new NameResolver() {
            @Override
            public CharStreamWithLocation resolve(final String includeName) throws IOException {
                log.info("JekyllSite resolving {}", includeName);
                return new CharStreamWithLocation(jekyllRoot.resolve("_includes").resolve(includeName));
            }
        })
        .withFilter(new Filter("markdownify") {
            @Override
            public Object apply(Object value, TemplateContext context, Object... params) {
                String text = super.asString(value, context);
                return markdownToHtml(text);
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
        final var siteContext = getSiteContext();
        final var content = readFile(path);
        final var frontMatter = FrontMatterSeparated.split(content);
        final var layoutPath = layoutsPath().resolve(frontMatter.frontMatter().layout() + ".html");
        final var layoutTemplate = readTemplate(layoutPath);
        try {
            final var contentTemplate = readTemplate(frontMatter.content());
            final var renderedContent = contentTemplate.render(siteContext);
            final var joinedContext = Stream.concat(
                siteContext.entrySet().stream(),
                Map.<String, Object>of("content", renderedContent).entrySet().stream()
            ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            return layoutTemplate.render(joinedContext);
        } catch (Exception e) {
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

    private Map<String, Object> getSiteContext() {
        return Map.of(
            "site", Map.ofEntries(
                Map.entry("title", "skagedal.tech"),
                Map.entry("baseUrl", "https://example.com"),
                Map.entry("posts", posts()),
                Map.entry("email", "skagedal@gmail.com"),
                Map.entry("description", "Thoughts on programming, music and other things. Feel free to e-mail me comments!"),
                Map.entry("baseurl", ""),
                Map.entry("url", "https://blog.skagedal.tech"),
                Map.entry("twitter_username", "skagedal"),
                Map.entry("github_username", "skagedal")
            )
        );
    }

private List<Map<String, Object>> posts() {
    Path postsDirectory = jekyllRoot.resolve("_posts");
    if (!Files.exists(postsDirectory) || !Files.isDirectory(postsDirectory)) {
        log.warn("Posts directory not found: {}", postsDirectory);
        return List.of();
    }

    try (Stream<Path> postFiles = Files.list(postsDirectory)) {
        return postFiles
            .filter(Files::isRegularFile)
            .filter(path -> {
                String filename = path.getFileName().toString();
                return filename.matches("\\d{4}-\\d{2}-\\d{2}-.*\\.(md|markdown|html)");
            })
            .map(this::processPostFile)
            .filter(Objects::nonNull)
            .toList();
    } catch (IOException e) {
        log.error("Failed to read posts from {}: {}", postsDirectory, e.getMessage());
        throw new UncheckedIOException("Failed to read posts", e);
    }
}

@Nullable
private Map<String, Object> processPostFile(Path postFile) {
    try {
        String content = readFile(postFile);
        FrontMatterSeparated frontMatterSeparated = FrontMatterSeparated.split(content);

        String filename = postFile.getFileName().toString();
        // Extract date from filename (assuming Jekyll's format: YYYY-MM-DD-title.md)
        String dateFromFilename = filename.substring(0, 10);
        String slugFromFilename = filename.substring(11, filename.lastIndexOf('.'));

        // Create URL from slug
        String url = "/" + slugFromFilename;

        // Combine front matter with metadata from filename
        Map<String, Object> metadata = new HashMap<>(frontMatterSeparated.frontMatter().asMap());

        // Use date from filename if not specified in front matter
        if (!metadata.containsKey("date")) {
            metadata.put("date", dateFromFilename);
        }

        // Add URL if not present
        if (!metadata.containsKey("url")) {
            metadata.put("url", url);
        }

        return metadata;
    } catch (Exception e) {
        log.error("Failed to process post file {}: {}", postFile, e.getMessage());
        return null;
    }
}
    private Template readTemplate(final Path path) {
        try {
            return templateParser.parse(path);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read template: " + path, e);
        }
    }

    private Template readTemplate(final String contents) {
        return templateParser.parse(contents);
    }

    private String readFile(final Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // Helper method to convert markdown to HTML
    private String markdownToHtml(String markdown) {
        // Implement markdown processing here
        // This is a simple example, you should use a proper markdown library like flexmark
        // For now, return the input as-is to avoid compilation errors
        return markdown;
    }
}
