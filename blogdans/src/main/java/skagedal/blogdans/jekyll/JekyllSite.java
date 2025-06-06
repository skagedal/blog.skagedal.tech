package skagedal.blogdans.jekyll;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;
import liqp.filters.Filter;
import liqp.parser.Flavor;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skagedal.blogdans.util.ContentFile;
import skagedal.blogdans.util.ContentType;
import skagedal.blogdans.util.Rss;
import skagedal.blogdans.util.Xml;
import skagedal.blogdans.domain.Post;
import skagedal.blogdans.domain.Slug;
import skagedal.blogdans.util.entry.EntryCollectors;
import skagedal.blogdans.util.entry.PossibleEntry;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class JekyllSite {
    private static final Logger log = LoggerFactory.getLogger(JekyllSite.class);
    private final Path contentRoot;
    private final Path renderedPostsRoot;
    private final AtomicReference<SiteContext> cachedSiteContext = new AtomicReference<>();
    private final MarkdownRenderer markdownRenderer = new MarkdownRenderer();
    private final Yamler yamler = new Yamler();

    private final TemplateParser templateParser = new TemplateParser.Builder()
        .withFlavor(Flavor.JEKYLL)
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

    public JekyllSite(final Path contentRoot, final Path renderedPostsRoot) {
        this.contentRoot = contentRoot;
        this.renderedPostsRoot = renderedPostsRoot;
    }

    public Post readPost(final Slug slug) {
        final var postPath = postPath(slug);
        final var contentFile = readFile(postPath);
        final var frontMatterSeparated = FrontMatterSeparated.split(contentFile.content());
        final var frontMatter = frontMatterSeparated.frontMatter();
        final var title = frontMatter.title();
        final var renderedPost = readFile(renderedPostsRoot.resolve(slug + ".html"));
        return new Post(
            slug,
            title != null ? title : slug.title(),
            frontMatter.summary(),
            renderedPost.content()
        );
    }

    public Path postPath(final Slug slug) {
        return contentRoot.resolve("posts").resolve(slug + ".md");
    }

    public String render(final Path path) {
        final var siteContext = getSiteContextAsMap();
        final var content = readFile(path);
        return renderWithLayout(content, siteContext);
    }

    private String renderWithLayout(final ContentFile contentFile, final Map<String, Object> siteContext) {
        final var frontMatter = FrontMatterSeparated.split(contentFile.content());
        final var contentTemplate = readTemplate(frontMatter.content());

        return getRenderedContent(siteContext, contentFile.contentType(), contentTemplate);
    }

    private String getRenderedContent(final Map<String, Object> siteContext, final ContentType contentType, final Template contentTemplate) {
        final var templatedInlined = contentTemplate.render(siteContext);
        return switch (contentType) {
            case HTML, XML -> templatedInlined;
            case MARKDOWN -> markdownToHtml(templatedInlined);
            case TEXT -> "<code><pre>" + templatedInlined + "</pre></code>";
        };
    }

    private Map<String, Object> getSiteContextAsMap() {
        return getSiteContext().asMap();
    }

    public SiteContext getSiteContext() {
        return cachedSiteContext.updateAndGet((_) -> new SiteContext(posts(), pages()));
    }

    private List<Map<String, Object>> posts() {
        Path postsDirectory = contentRoot.resolve("posts");
        if (!Files.exists(postsDirectory) || !Files.isDirectory(postsDirectory)) {
            log.warn("Posts directory not found: {}", postsDirectory);
            return List.of();
        }

        final var before = System.currentTimeMillis();
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
        } finally {
            final var after = System.currentTimeMillis();
            log.info("Processed posts in {} ms", after - before);
        }
    }

    private List<Map<String, Object>> pages() {
        return List.of();
    }

    public Path getFeedPath() {
        return contentRoot.resolve("feed.xml");
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
            final var filename = postFile.getFileName().toString();
            // Assuming Jekyll's format: YYYY-MM-DD-title.md
            final var dateFromFilename = filename.substring(0, 10);
            final var slugFromFilename = filename.substring(0, filename.lastIndexOf('.'));
            final var url = "/posts/" + slugFromFilename;

            final var frontMatter = yamler.load(postFile);

            return Stream.concat(
                frontMatter.asPossibleEntries(),
                Stream.of(
                    new PossibleEntry("content", "" /*markdownToHtml(frontMatterSeparated.content()) */),
                    new PossibleEntry("date", frontMatter.date() == null ? dateFromFilename : null),
                    new PossibleEntry("slug", slugFromFilename),
                    new PossibleEntry("url", url)
                )
            ).collect(EntryCollectors.nonNullEntriesToMap());
        } catch (Exception e) {
            log.error("Failed to process post file {}", postFile, e);
            return null;
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
