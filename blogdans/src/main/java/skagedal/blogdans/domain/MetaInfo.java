package skagedal.blogdans.domain;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.URI;

@NullMarked
public record MetaInfo(
    URI canonicalUri,
    @Nullable String description,
    String title) {
}
