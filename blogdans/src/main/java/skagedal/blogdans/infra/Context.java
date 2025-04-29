package skagedal.blogdans.infra;

import skagedal.blogdans.domain.User;

import java.time.Instant;

public record Context(
    io.javalin.http.Context javalin,
    User user,
    Instant startTime
) {
    public static Context from(io.javalin.http.Context context) {
        if (context.attribute(Attributes.CONTEXT) instanceof Context requestContext) {
            return requestContext;
        }
        final var requestContext = new Context(
            context,
            User.fromContext(context),
            Instant.now()
        );
        context.attribute(Attributes.CONTEXT, requestContext);
        return requestContext;
    }
}