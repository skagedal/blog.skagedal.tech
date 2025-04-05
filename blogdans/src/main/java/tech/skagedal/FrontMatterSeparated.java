package tech.skagedal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.util.regex.Pattern;

public record FrontMatterSeparated(FrontMatter frontMatter, String content) {
    private static final Pattern pattern = Pattern.compile("^---\\s*$\\n?", Pattern.MULTILINE);
    private static final YAMLMapper yamlMapper = new YAMLMapper();

    public static FrontMatterSeparated split(String text) {
        final var array = pattern.split(text);
        return new FrontMatterSeparated(parseFrontMatterYaml(array), array[2]);
    }

    private static FrontMatter parseFrontMatterYaml(final String[] array) {
        try {
            return yamlMapper.readValue(array[1], FrontMatter.class);
        } catch (JsonProcessingException e) {
            throw new JekyllParseException("Could not parse front matter as YAML", e);
        }
    }
}
