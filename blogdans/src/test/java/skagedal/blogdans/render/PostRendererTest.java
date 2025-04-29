package skagedal.blogdans.render;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import skagedal.blogdans.domain.Post;
import skagedal.blogdans.domain.Site;
import skagedal.blogdans.domain.Slug;
import skagedal.blogdans.domain.User;

@ExtendWith({
    SnapshotExtension.class
})
class PostRendererTest {

    private final PostRenderer postRenderer = new PostRenderer(Site.simple());

    private Expect expect;

    @Disabled("i should do something smarter than this")
    @Test
    void render() {
        final var post = Post.builder(Slug.of("2025-04-07-first-post"))
            .title("First post")
            .build();
        final var user = new User.Anonymous();
        final var renderedPage = postRenderer.render(post, user);

        expect.toMatchSnapshot(renderedPage);
    }

    @Disabled("i should do something smarter than this")
    @Test
    void withDescription() {
        final var post = Post.builder(Slug.of("2025-04-07-first-post"))
            .title("First post")
            .excerpt("Excerpt from First post")
            .build();
        final var user = new User.Anonymous();
        final var renderedPage = postRenderer.render(post, user);

        expect.toMatchSnapshot(renderedPage);
    }

}
