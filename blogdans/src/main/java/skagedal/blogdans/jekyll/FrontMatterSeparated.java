package skagedal.blogdans.jekyll;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.util.regex.Pattern;

public record FrontMatterSeparated(FrontMatter frontMatter, String content) {
    private static final Pattern pattern = Pattern.compile("^---\\s*$\\n?", Pattern.MULTILINE);
    private static final YAMLMapper yamlMapper = new YAMLMapper();

    public static FrontMatterSeparated split(String text) {
        final var array = pattern.split(text, 3);
        return switch (array.length) {
            case 1 -> new FrontMatterSeparated(FrontMatter.empty(), text);
            case 2 -> new FrontMatterSeparated(parseFrontMatterYaml(array[0]), array[1]);
            case 3 -> new FrontMatterSeparated(parseFrontMatterYaml(array[1]), array[2]);
            default -> throw new IllegalStateException("Unexpected number of --- splits in file: " + array.length);
        };
    }

    private static FrontMatter parseFrontMatterYaml(final String yaml) {
        try {
            return yamlMapper.readValue(yaml, FrontMatter.class);
        } catch (JsonProcessingException e) {
            throw new JekyllParseException("Could not parse front matter as YAML", e);
        }
    }
}
