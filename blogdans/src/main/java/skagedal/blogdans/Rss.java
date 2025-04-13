package skagedal.blogdans;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Locale;

public class Rss {
    private static final DateTimeFormatter rfc822Formatter = new DateTimeFormatterBuilder()
        .appendPattern("EEE, dd MMM yyyy")
        .appendLiteral(' ')
        .appendPattern("HH:mm:ss")
        .appendLiteral(' ')
        .appendOffset("+HHMM", "+0000")
        .toFormatter(Locale.ENGLISH)
        .withResolverStyle(ResolverStyle.SMART);

    private Rss() {
    }

    public static String formatToRFC822(ZonedDateTime date) {
        return date.format(rfc822Formatter);
    }
}
