package skagedal.blogdans.infra;

import org.jspecify.annotations.NullMarked;
import org.slf4j.MDC;

@NullMarked
public interface Handler extends io.javalin.http.Handler {
    default void handle(final io.javalin.http.Context javalinContext) {
        final var context = Context.from(javalinContext);
        try {
            MDC.put(Logging.USER, context.user().loggable());
            MDC.put(Logging.PATH, javalinContext.path());
            handle(context);
        } finally {
            MDC.remove(Logging.USER);
            MDC.remove(Logging.PATH);
        }
    }

    void handle(final Context context);
}
