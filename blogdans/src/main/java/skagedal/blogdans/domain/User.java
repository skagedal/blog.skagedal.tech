package skagedal.blogdans.domain;

import io.javalin.http.Context;

public sealed interface User {
    record Anonymous() implements User {
        @Override
        public String loggable() {
            return "anonymous";
        }
    }
    record Authenticated(String email) implements User {
        @Override
        public String loggable() {
            return email;
        }
    }

    static User fromContext(Context context) {
        final var header = context.header("x-email");
        if (header == null || header.isBlank()) {
            return new Anonymous();
        } else {
            return new Authenticated(header);
        }
    }

    public String loggable();
}
