package skagedal.blogdans.domain;

import org.jspecify.annotations.NullMarked;

import java.net.URI;

@NullMarked
public record Site(
    URI baseUri
) {
}
