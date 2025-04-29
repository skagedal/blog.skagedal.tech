package skagedal.blogdans.handlers;


import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skagedal.blogdans.infra.Context;
import skagedal.blogdans.infra.Handler;

@NullMarked
public class BeforeRequestHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(BeforeRequestHandler.class);

    @Override
    public void handle(final Context context) {
        log.info(">>> Handling request");
    }
}
