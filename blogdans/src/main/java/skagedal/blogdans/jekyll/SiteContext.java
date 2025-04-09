package skagedal.blogdans.jekyll;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public record SiteContext(
    List<Map<String, Object>> posts,
    List<Map<String, Object>> pages
) {
    public Map<String, Object> asMap() {
        return Map.of(
            "site", Map.ofEntries(
                Map.entry("title", "skagedal.tech"),
                Map.entry("baseUrl", "https://skagedal.tech"),
                Map.entry("posts", posts()),
                Map.entry("pages", pages()),
                Map.entry("time", ZonedDateTime.now()),
                Map.entry("email", "skagedal@gmail.com"),
                Map.entry("description", "Thoughts on programming, music and other things. Feel free to e-mail me comments!"),
                Map.entry("baseurl", ""),
                Map.entry("url", "https://blog.skagedal.tech"),
                Map.entry("twitter_username", "skagedal"),
                Map.entry("github_username", "skagedal")
            )
        );
    }
}
