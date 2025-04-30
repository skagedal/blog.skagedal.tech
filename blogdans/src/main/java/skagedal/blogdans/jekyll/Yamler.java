package skagedal.blogdans.jekyll;

import org.jetbrains.annotations.NotNull;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/// Quick processor of YAML front matter
public class Yamler {
    private Load yamlLoader = new Load(LoadSettings.builder().build());

    public FrontMatter load(String input) {
        final var frontMatter = yamlLoader.loadAllFromString(input).iterator().next();
        return fromYamlObject(frontMatter);
    }

    public FrontMatter load(Path path) {
        try (final var reader = Files.newBufferedReader(path)) {
            final var frontMatter = yamlLoader.loadAllFromReader(reader).iterator().next();
            return fromYamlObject(frontMatter);
        } catch (IOException e) {
            throw new JekyllParseException("Could not load front matter", e);
        }
    }

    private static @NotNull FrontMatter fromYamlObject(final Object frontMatter) {
        if (frontMatter instanceof Map<?, ?> map) {
            return new FrontMatter(
                (String) map.get("layout"),
                (String) map.get("title"),
                (String) map.get("summary"),
                (String) map.get("date"),
                (String) map.get("permalink")
            );
        } else {
            throw new IllegalArgumentException("Invalid front matter: " + frontMatter);
        }
    }
}
