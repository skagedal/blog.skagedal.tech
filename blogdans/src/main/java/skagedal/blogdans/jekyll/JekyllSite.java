package skagedal.blogdans.jekyll;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;
import liqp.antlr.CharStreamWithLocation;
import liqp.antlr.NameResolver;
import liqp.filters.Filter;
import liqp.parser.Flavor;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skagedal.blogdans.ContentFile;
import skagedal.blogdans.ContentType;
import skagedal.blogdans.Rss;
import skagedal.blogdans.Xml;
import skagedal.blogdans.domain.Post;
import skagedal.blogdans.domain.Slug;
import skagedal.blogdans.entry.EntryCollectors;
import skagedal.blogdans.entry.PossibleEntry;
import skagedal.blogdans.markdown.MarkdownRenderer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JekyllSite {
    private static final Logger log = LoggerFactory.getLogger(JekyllSite.class);
    private final Path jekyllRoot;
    private final Path renderedPostsRoot;
    private final AtomicReference<SiteContext> cachedSiteContext = new AtomicReference<>();
    private final MarkdownRenderer markdownRenderer = new MarkdownRenderer();

    private final TemplateParser templateParser = new TemplateParser.Builder()
        .withFlavor(Flavor.JEKYLL)
        .withNameResolver(new NameResolver() {
            @Override
            public CharStreamWithLocation resolve(final String includeName) throws IOException {
                return new CharStreamWithLocation(jekyllRoot.resolve("_includes").resolve(includeName));
            }
        })
        .withFilter(new Filter("markdownify") {
            @Override
            public Object apply(Object value, TemplateContext context, Object... params) {
                final var text = asString(value, context);
                return markdownToHtml(text);
            }
        })
        .withFilter(new Filter("date_to_xmlschema") {
            @Override
            public Object apply(Object value, TemplateContext context, Object... params) {
                final var date = asRubyDate(value, context);
                return date.format(DateTimeFormatter.RFC_1123_DATE_TIME);
            }
        })
        .withFilter(new Filter("xml_escape") {
            @Override
            public Object apply(Object value, TemplateContext context, Object... params) {
                final var text = asString(value, context);
                return Xml.escape(text);
            }
        })
        .withFilter(new Filter("date_to_rfc822") {
            @Override
            public Object apply(Object value, TemplateContext context, Object... params) {
                if (value instanceof String dateString) {
                    try {
                        final var date = LocalDate.parse(dateString);
                        return Rss.formatToRFC822(date.atStartOfDay(ZoneId.of("Europe/Stockholm")));
                    } catch (Exception e) {
                        log.error("error parsing date: {}", value, e);
                        return "";
                    }
                }
                try {
                    final var date = asRubyDate(value, context);
                    return Rss.formatToRFC822(date);
                } catch (Exception e) {
                    log.error("error parsing date: {}", value, e);
                    return "";
                }
            }
        })
        .build();

    public JekyllSite(final Path jekyllRoot, final Path renderedPostsRoot) {
        this.jekyllRoot = jekyllRoot;
        this.renderedPostsRoot = renderedPostsRoot;
    }

    public Path indexPath() {
        return jekyllRoot.resolve("index.html");
    }

    public Post readPost(final Slug slug) {
        final var postPath = postPath(slug);
        final var contentFile = readFile(postPath);
        final var frontMatterSeparated = FrontMatterSeparated.split(contentFile.content());
        final var title = frontMatterSeparated.frontMatter().title();
        final var renderedPost = readFile(renderedPostsRoot.resolve(slug + ".html"));
        return new Post(
            slug,
            title != null ? title : slug.title(),
            frontMatterSeparated.content(),
            renderedPost.content()
        );
    }

    public Path postPath(final Slug slug) {
        return jekyllRoot.resolve("_posts").resolve(slug + ".md");
    }

    public String dateFromSlug(final String slug) {
        return slug.substring(0, 10);
    }

    public Path layoutsPath() {
        return jekyllRoot.resolve("_layouts");
    }

    public String render(final Path path) {
        return render(path, null);
    }

    public String render(final Path path, final @Nullable String defaultDate) {
        final var siteContext = getSiteContext();
        final var content = readFile(path);
        return renderWithLayout(content, siteContext, defaultDate);
    }

    private String renderWithLayout(final ContentFile contentFile, final Map<String, Object> siteContext, final @Nullable String defaultDate) {
        final var frontMatter = FrontMatterSeparated.split(contentFile.content());
        final var contentTemplate = readTemplate(frontMatter.content());

        // Render the content with the site context
        final var renderedContent = getRenderedContent(siteContext, contentFile.contentType(), contentTemplate);

        Object page = Stream.of(
            new PossibleEntry("title", frontMatter.frontMatter().title()),
            new PossibleEntry("date", Optional.ofNullable(frontMatter.frontMatter().date()).orElse(defaultDate))
        ).collect(EntryCollectors.nonNullEntriesToMap());

        // Create context with rendered content
        final var contentContext = siteContext.containsKey("content") ? siteContext : Stream.concat(
            siteContext.entrySet().stream(),
            Map.<String, Object>of(
                "content", renderedContent,
                "page", page
            ).entrySet().stream()
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // If no layout is specified, return the rendered content
        if (frontMatter.frontMatter().layout() == null) {
            return renderedContent;
        }

        // Otherwise, load the layout and recursively process it
        final var layoutPath = layoutsPath().resolve(frontMatter.frontMatter().layout() + ".html");
        try {
            final var layoutContent = readFile(layoutPath);
            return renderWithLayout(layoutContent, contentContext, defaultDate);
        } catch (Exception e) {
            log.error("Error rendering layout {}: {}", layoutPath, e.getMessage());
            return "Error rendering layout: " + e.getMessage();
        }
    }

    private String getRenderedContent(final Map<String, Object> siteContext, final ContentType contentType, final Template contentTemplate) {
        final var templatedInlined = contentTemplate.render(siteContext);
        return switch (contentType) {
            case HTML, XML -> templatedInlined;
            case MARKDOWN -> markdownToHtml(templatedInlined);
            case TEXT -> "<code><pre>" + templatedInlined + "</pre></code>";
        };
    }

    private Map<String, Object> getSiteContext() {
        return cachedSiteContext.updateAndGet((_) -> new SiteContext(posts(), pages())).asMap();
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

    private List<Map<String, Object>> pages() {
        final var aboutPath = getAboutPath();
        return List.of(
            Map.of(
                "title", "About",
                "url", "/about/",
                "content", readFile(aboutPath).content()
            )
        );
    }

    public Path getAboutPath() {
        return jekyllRoot.resolve("about.md");
    }

    public Path getFeedPath() {
        return jekyllRoot.resolve("feed.xml");
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
            final var contentFile = readFile(postFile);
            final var frontMatterSeparated = FrontMatterSeparated.split(contentFile.content());

            final var filename = postFile.getFileName().toString();
            // Assuming Jekyll's format: YYYY-MM-DD-title.md
            final var dateFromFilename = filename.substring(0, 10);
            final var slugFromFilename = filename.substring(0, filename.lastIndexOf('.'));
            final var url = "/posts/" + slugFromFilename;

            final var frontMatter = frontMatterSeparated.frontMatter();

            return Stream.concat(
                frontMatter.asPossibleEntries(),
                Stream.of(
                    new PossibleEntry("content", markdownToHtml(frontMatterSeparated.content())),
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

    private ContentFile readFile(final Path path) {
        final var contentType = switch (getFilenameExtension(path)) {
            case "html" -> ContentType.HTML;
            case "md" -> ContentType.MARKDOWN;
            case "xml" -> ContentType.XML;
            default -> ContentType.TEXT;
        };
        try {
            return new ContentFile(Files.readString(path), contentType);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String getFilenameExtension(final Path path) {
        final var fileName = path.getFileName().toString();
        if (fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    private String markdownToHtml(String markdown) {
        return markdownRenderer.render(markdown);
    }
}
