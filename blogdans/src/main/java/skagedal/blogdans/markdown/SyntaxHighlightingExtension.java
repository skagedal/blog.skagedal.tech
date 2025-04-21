package skagedal.blogdans.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SyntaxHighlightingExtension implements HtmlRenderer.HtmlRendererExtension {
    @Override
    public void rendererOptions(final MutableDataHolder mutableDataHolder) {

    }

    @Override
    public void extend(final HtmlRenderer.@NotNull Builder builder, final String s) {
        builder.nodeRendererFactory(new RendererFactory());
    }

    public static SyntaxHighlightingExtension create() {
        return new SyntaxHighlightingExtension();
    }

    private static class RendererFactory implements NodeRendererFactory {
        @Override
        public NodeRenderer apply(final DataHolder options) {
            return new SyntaxHighlightingRenderer();
        }
    }
}
