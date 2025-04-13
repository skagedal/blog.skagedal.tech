package skagedal.blogdans.config;

import skagedal.blogdans.util.Paths;

import java.io.IOException;
import java.nio.file.Files;

public class Secrets {
    private Secrets() {}

    public static String readSecret(final String secretName) {
        try {
            return Files.readString(Paths.home().resolve(".secrets").resolve(secretName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
