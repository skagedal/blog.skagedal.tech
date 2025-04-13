package skagedal.blogdans.util;

import java.nio.file.Path;

public class Paths {
    public static Path home() {
        return Path.of(System.getProperty("user.home"));
    }
}
