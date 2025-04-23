package skagedal.blogdans.domain;

import org.jspecify.annotations.NullMarked;

import java.time.LocalDate;
import java.util.regex.Pattern;

@NullMarked
public record Slug(LocalDate date, String title) {
    private static final Pattern PATTERN = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})-(.+)$");

    public static Slug of(String slug) {
        final var matcher = PATTERN.matcher(slug);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid slug format: " + slug);
        }
        final var date = LocalDate.of(
            Integer.parseInt(matcher.group(1)),
            Integer.parseInt(matcher.group(2)),
            Integer.parseInt(matcher.group(3))
        );
        final var title = matcher.group(4);
        return new Slug(date, title);
    }

    public String toString() {
        return String.format("%04d-%02d-%02d-%s", date.getYear(), date.getMonthValue(), date.getDayOfMonth(), title);
    }
}
