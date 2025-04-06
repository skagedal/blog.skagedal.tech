package tech.skagedal;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;
import liqp.antlr.CharStreamWithLocation;
import liqp.antlr.NameResolver;
import liqp.filters.Filter;
import liqp.parser.Flavor;
import org.intellij.lang.annotations.Language;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.skagedal.entry.EntryCollectors;
import tech.skagedal.entry.PossibleEntry;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        .withFilter(new Filter("date_to_xmlschema") {
            @Override
            public Object apply(Object value, TemplateContext context, Object... params) {
                return "foo";
            }
        })
        .build();

    public JekyllSite(final Path jekyllRoot) {
        this.jekyllRoot = jekyllRoot;
    }

    public Path indexPath() {
        return jekyllRoot.resolve("index.html");
    }

    public Path postPath(final String slug) {
        return jekyllRoot.resolve("_posts").resolve(slug + ".md");
    }

    public Path layoutsPath() {
        return jekyllRoot.resolve("_layouts");
    }

    public @Language("HTML") String renderHtml(final Path path) {
        final var siteContext = getSiteContext();
        final var content = readFile(path);
        return renderWithLayout(content, siteContext);
    }

    private String renderWithLayout(String content, Map<String, Object> siteContext) {
        final var frontMatter = FrontMatterSeparated.split(content);
        final var contentTemplate = readTemplate(frontMatter.content());

        // Render the content with the site context
        final var renderedContent = contentTemplate.render(siteContext);

        // Create context with rendered content
        final var contentContext = siteContext.containsKey("content") ? siteContext : Stream.concat(
            siteContext.entrySet().stream(),
            Map.<String, Object>of("content", renderedContent).entrySet().stream()
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // If no layout is specified, return the rendered content
        if (frontMatter.frontMatter().layout() == null) {
            return renderedContent;
        }

        // Otherwise, load the layout and recursively process it
        final var layoutPath = layoutsPath().resolve(frontMatter.frontMatter().layout() + ".html");
        try {
            final var layoutContent = readFile(layoutPath);
            return renderWithLayout(layoutContent, contentContext);
        } catch (Exception e) {
            log.error("Error rendering layout {}: {}", layoutPath, e.getMessage());
            return "Error rendering layout: " + e.getMessage();
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
            return processPostFiles(postFiles)
                .sorted((post1, post2) -> {
                    String date1 = (String) post1.get("date");
                    String date2 = (String) post2.get("date");
                    // Sort in descending order (newer dates first)
                    return date2.compareTo(date1);
                })
                .toList();
        } catch (IOException e) {
            log.error("Failed to read posts from {}", postsDirectory, e);
            throw new UncheckedIOException("Failed to read posts", e);
        }
    }

    private Stream<Map<String, Object>> processPostFiles(final Stream<Path> postFiles) {
        return postFiles
            .filter(Files::isRegularFile)
            .filter(path -> {
                String filename = path.getFileName().toString();
                return filename.matches("\\d{4}-\\d{2}-\\d{2}-.*\\.(md|markdown|html)");
            })
            .map(this::processPostFile)
            .filter(Objects::nonNull);
    }

    @Nullable
    private Map<String, Object> processPostFile(Path postFile) {
        try {
            final var content = readFile(postFile);
            final var frontMatterSeparated = FrontMatterSeparated.split(content);

            final var filename = postFile.getFileName().toString();
            // Extract date from filename (assuming Jekyll's format: YYYY-MM-DD-title.md)
            final var dateFromFilename = filename.substring(0, 10);
            final var slugFromFilename = filename.substring(0, filename.lastIndexOf('.'));
            final var url = "/posts/" + slugFromFilename;

            final var frontMatter = frontMatterSeparated.frontMatter();

            return Stream.concat(
                frontMatter.asPossibleEntries(),
                Stream.of(
                    new PossibleEntry("content", frontMatterSeparated.content()),
                    new PossibleEntry("date", frontMatter.date() == null ? dateFromFilename : null),
                    new PossibleEntry("slug", slugFromFilename),
                    new PossibleEntry("url", url)
                )
            ).collect(EntryCollectors.nonNullEntriesToMap());
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
