package skagedal.blogdans.handlers;

import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import skagedal.blogdans.infra.Context;
import skagedal.blogdans.infra.Handler;
import skagedal.blogdans.infra.Logging;

import java.time.Duration;
import java.time.Instant;

@NullMarked
public class AfterRequestHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(AfterRequestHandler.class);

    @Override
    public void handle(final Context context) {
        final var duration = Duration.between(context.startTime(), Instant.now());
        try (final var ignored = MDC.putCloseable(Logging.DURATION_MS, String.valueOf(duration.toMillis()))) {
            log.info("<<< Handled request");
        }
    }
}
