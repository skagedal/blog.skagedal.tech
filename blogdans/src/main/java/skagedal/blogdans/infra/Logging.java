package skagedal.blogdans.infra;

import io.javalin.http.Context;
import org.slf4j.MDC;

import java.util.function.Supplier;

public class Logging {
    private Logging() {}

    public static final String USER = "user";
    public static final String PATH = "path";
    public static final String DURATION_MS = "duration_ms";
}
