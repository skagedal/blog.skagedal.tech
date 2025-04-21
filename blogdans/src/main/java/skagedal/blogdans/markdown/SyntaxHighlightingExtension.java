package skagedal.blogdans.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import org.jetbrains.annotations.NotNull;

public class SyntaxHighlightingExtension implements HtmlRenderer.HtmlRendererExtension {
    @Override
    public void rendererOptions(@NotNull final MutableDataHolder mutableDataHolder) {

    }

    @Override
    public void extend(final HtmlRenderer.@NotNull Builder builder, @NotNull final String s) {

    }

    public static SyntaxHighlightingExtension create() {
        return new SyntaxHighlightingExtension();
    }
}
