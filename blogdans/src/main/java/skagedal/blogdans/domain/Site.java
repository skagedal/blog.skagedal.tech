package skagedal.blogdans.domain;

import org.intellij.lang.annotations.Language;
import org.jspecify.annotations.NullMarked;

import java.net.URI;

@NullMarked
public record Site(
    URI baseUri,
    @Language("html") String header,
    @Language("html") String footer
) {
    private static final String GITHUB_ICON_SVG = """
    <svg viewBox="0 0 16 16"><path fill="#828282" d="M7.999,0.431c-4.285,0-7.76,3.474-7.76,7.761 c0,3.428,2.223,6.337,5.307,7.363c0.388,0.071,0.53-0.168,0.53-0.374c0-0.184-0.007-0.672-0.01-1.32 c-2.159,0.469-2.614-1.04-2.614-1.04c-0.353-0.896-0.862-1.135-0.862-1.135c-0.705-0.481,0.053-0.472,0.053-0.472 c0.779,0.055,1.189,0.8,1.189,0.8c0.692,1.186,1.816,0.843,2.258,0.645c0.071-0.502,0.271-0.843,0.493-1.037 C4.86,11.425,3.049,10.76,3.049,7.786c0-0.847,0.302-1.54,0.799-2.082C3.768,5.507,3.501,4.718,3.924,3.65 c0,0,0.652-0.209,2.134,0.796C6.677,4.273,7.34,4.187,8,4.184c0.659,0.003,1.323,0.089,1.943,0.261 c1.482-1.004,2.132-0.796,2.132-0.796c0.423,1.068,0.157,1.857,0.077,2.054c0.497,0.542,0.798,1.235,0.798,2.082 c0,2.981-1.814,3.637-3.543,3.829c0.279,0.24,0.527,0.713,0.527,1.437c0,1.037-0.01,1.874-0.01,2.129 c0,0.208,0.14,0.449,0.534,0.373c3.081-1.028,5.302-3.935,5.302-7.362C15.76,3.906,12.285,0.431,7.999,0.431z"/></svg>
    """;

    private static final String BLUESKY_ICON_SVG = """
        <svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 24 24">
          <path fill="currentColor" d="M12 10.8c-1.087-2.114-4.046-6.053-6.798-7.995C2.566.944 1.561 1.266.902 1.565C.139 1.908 0 3.08 0 3.768c0 .69.378 5.65.624 6.479c.815 2.736 3.713 3.66 6.383 3.364c.136-.02.275-.039.415-.056c-.138.022-.276.04-.415.056c-3.912.58-7.387 2.005-2.83 7.078c5.013 5.19 6.87-1.113 7.823-4.308c.953 3.195 2.05 9.271 7.733 4.308c4.267-4.308 1.172-6.498-2.74-7.078a8.741 8.741 0 0 1-.415-.056c.14.017.279.036.415.056c2.67.297 5.568-.628 6.383-3.364c.246-.828.624-5.79.624-6.478c0-.69-.139-1.861-.902-2.206c-.659-.298-1.664-.62-4.3 1.24C16.046 4.748 13.087 8.687 12 10.8"/>
        </svg>
        """;;


    public static Site simple() {
        return new Site(URI.create("https://example.com/"), "<header></header>", "<footer></footer>");
    }

    public static Site skagedalTech() {
        return new Site(
            URI.create("https://blog.skagedal.tech/"),
            """
                <header class="site-header">
                  <div class="wrapper">
                    <a class="site-title" href="https://blog.skagedal.tech/">skagedal.tech</a>
                    <nav class="site-nav">
                      <a href="#" class="menu-icon">
                        <svg viewBox="0 0 18 15">
                          <path fill="#424242" d="M18,1.484c0,0.82-0.665,1.484-1.484,1.484H1.484C0.665,2.969,0,2.304,0,1.484l0,0C0,0.665,0.665,0,1.484,0 h15.031C17.335,0,18,0.665,18,1.484L18,1.484z"/>
                          <path fill="#424242" d="M18,7.516C18,8.335,17.335,9,16.516,9H1.484C0.665,9,0,8.335,0,7.516l0,0c0-0.82,0.665-1.484,1.484-1.484 h15.031C17.335,6.031,18,6.696,18,7.516L18,7.516z"/>
                          <path fill="#424242" d="M18,13.516C18,14.335,17.335,15,16.516,15H1.484C0.665,15,0,14.335,0,13.516l0,0 c0-0.82,0.665-1.484,1.484-1.484h15.031C17.335,12.031,18,12.696,18,13.516L18,13.516z"/>
                        </svg>
                      </a>
                      <div class="trigger">
                        <a class="page-link" href="/about/">About</a>
                      </div>
                    </nav>
                  </div>
                </header>
                """,
            """
                <footer class="site-footer">
                  <div class="wrapper">
                    <h2 class="footer-heading">skagedal.tech</h2>
                    <div class="footer-col-wrapper">
                      <div class="footer-col footer-col-1">
                        <ul class="contact-list">
                          <li>Simon Kågedal Reimer</li>
                          <li><a href="mailto:skagedal@gmail.com">skagedal@gmail.com</a></li>
                        </ul>
                      </div>
                
                      <div class="footer-col footer-col-2">
                        <ul class="social-media-list">
                          <li>
                            <a href="https://github.com/skagedal">
                              <span class="icon icon--github">%s</span>
                               <span class="username">skagedal</span>
                             </a>
                          </li>
                          <li>
                            <a href="https://bsky.app/profile/skagedal.tech">
                                <span class="icon icon--bluesky">%s</span>
                                <span class="username">skagedal.tech</span>
                            </a>
                          </li>
                        </ul>
                      </div>
                
                      <div class="footer-col footer-col-3">
                        <p>Thoughts on programming and other things.</p>
                      </div>
                    </div>
                  </div>
                </footer>
                """.formatted(GITHUB_ICON_SVG, BLUESKY_ICON_SVG)
        );
    }
}
