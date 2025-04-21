package skagedal.blogdans.markdown;

import com.codewaves.codehighlight.core.Highlighter;
import com.codewaves.codehighlight.core.StyleRenderer;
import com.codewaves.codehighlight.core.StyleRendererFactory;
import com.codewaves.codehighlight.renderer.HtmlRenderer;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.html.renderer.CoreNodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.misc.CharPredicate;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class SyntaxHighlightingRenderer implements NodeRenderer {

    public class HighlightRendererFactory implements StyleRendererFactory<CharSequence> {
        public StyleRenderer<CharSequence> create(String languageName) {
            return new HtmlRenderer("hljs-");
        }
    }

    public SyntaxHighlightingRenderer() {
    }

    private static final Set<String> LANGUAGES = Set.of("java");

    @Override
    public @Nullable Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        HashSet<NodeRenderingHandler<?>> set = new HashSet<>();
        set.add(new NodeRenderingHandler<>(FencedCodeBlock.class, (node, context, html) -> {
            // test the node to see if it needs overriding
            BasedSequence info = node.getInfo();

            if(info.isIn(Highlighter.getSupportedLanguages())) {
                // standard fenced code rendering from CoreNodeRenderer with addition of converting class attribute of code tag to uppercase, customize it according to need
                html.line();
                html.srcPosWithTrailingEOL(node.getChars()).withAttr().tag("pre").openPre();

                BasedSequence language = node.getInfoDelimitedByAny(CharPredicate.SPACE_TAB);
                html.attr("class", (context.getHtmlOptions().languageClassPrefix + language.unescape()));

                html.srcPosWithEOL(node.getContentChars()).withAttr(CoreNodeRenderer.CODE_CONTENT).tag("code");
                final Highlighter<CharSequence> highlighter = new Highlighter<>(new HighlightRendererFactory());
                final Highlighter.HighlightResult<CharSequence> result = highlighter.highlightAuto(node.getContentChars().toString(), null);
                final CharSequence styledCode = result.getResult();
                html.raw(styledCode);
                html.tag("/code");
                html.tag("/pre").closePre();
                html.lineIf(context.getHtmlOptions().htmlBlockCloseTagEol);
            } else {
                context.delegateRender();
            }
        }));

        return set;
    }
}
