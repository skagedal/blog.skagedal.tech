export function ExampleFooter() {
  return (
    <footer className="site-footer">
      <div className="wrapper">
        <h2 className="footer-heading">skagedal.tech</h2>
        <div className="footer-col-wrapper">
          <div className="footer-col footer-col-1">
            <ul className="contact-list">
              <li>Simon Kågedal Reimer</li>
              <li>
                <a href="mailto:skagedal@gmail.com">skagedal@gmail.com</a>
              </li>
            </ul>
          </div>
          <div className="footer-col footer-col-2">
            <ul className="social-media-list">
              <li>
                <a href="https://github.com/skagedal">
                  <span className="icon icon--github">
                    <svg viewBox="0 0 16 16">
                      <path
                        fill="#828282"
                        d="M7.999,0.431c-4.285,0-7.76,3.474-7.76,7.761 c0,3.428,2.223,6.337,5.307,7.363c0.388,0.071,0.53-0.168,0.53-0.374c0-0.184-0.007-0.672-0.01-1.32 c-2.159,0.469-2.614-1.04-2.614-1.04c-0.353-0.896-0.862-1.135-0.862-1.135c-0.705-0.481,0.053-0.472,0.053-0.472 c0.779,0.055,1.189,0.8,1.189,0.8c0.692,1.186,1.816,0.843,2.258,0.645c0.071-0.502,0.271-0.843,0.493-1.037 C4.86,11.425,3.049,10.76,3.049,7.786c0-0.847,0.302-1.54,0.799-2.082C3.768,5.507,3.501,4.718,3.924,3.65 c0,0,0.652-0.209,2.134,0.796C6.677,4.273,7.34,4.187,8,4.184c0.659,0.003,1.323,0.089,1.943,0.261 c1.482-1.004,2.132-0.796,2.132-0.796c0.423,1.068,0.157,1.857,0.077,2.054c0.497,0.542,0.798,1.235,0.798,2.082 c0,2.981-1.814,3.637-3.543,3.829c0.279,0.24,0.527,0.713,0.527,1.437c0,1.037-0.01,1.874-0.01,2.129 c0,0.208,0.14,0.449,0.534,0.373c3.081-1.028,5.302-3.935,5.302-7.362C15.76,3.906,12.285,0.431,7.999,0.431z"
                      />
                    </svg>
                  </span>
                  <span className="username">skagedal</span>
                </a>
              </li>
              <li>
                <a href="https://bsky.app/profile/skagedal.tech">
                  <span className="icon icon--bluesky">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="200"
                      height="200"
                      viewBox="0 0 24 24"
                    >
                      <path
                        fill="currentColor"
                        d="M12 10.8c-1.087-2.114-4.046-6.053-6.798-7.995C2.566.944 1.561 1.266.902 1.565C.139 1.908 0 3.08 0 3.768c0 .69.378 5.65.624 6.479c.815 2.736 3.713 3.66 6.383 3.364c.136-.02.275-.039.415-.056c-.138.022-.276.04-.415.056c-3.912.58-7.387 2.005-2.83 7.078c5.013 5.19 6.87-1.113 7.823-4.308c.953 3.195 2.05 9.271 7.733 4.308c4.267-4.308 1.172-6.498-2.74-7.078a8.741 8.741 0 0 1-.415-.056c.14.017.279.036.415.056c2.67.297 5.568-.628 6.383-3.364c.246-.828.624-5.79.624-6.478c0-.69-.139-1.861-.902-2.206c-.659-.298-1.664-.62-4.3 1.24C16.046 4.748 13.087 8.687 12 10.8"
                      />
                    </svg>
                  </span>
                  <span className="username">skagedal.tech</span>
                </a>
              </li>
            </ul>
          </div>
          <div className="footer-col footer-col-3">
            <p>Thoughts on programming and other things.</p>
          </div>
        </div>
        <div className="user-info">
          <a href="/oauth2/sign_in">
            <span style={{ color: "#DCDCDC" }}>log in</span>
          </a>
        </div>
      </div>
    </footer>
  );
}

export function ExampleBlogPost() {
  return (
    <div className="page-content">
      <div className="wrapper">
        <article className="post">
          <header className="post-header">
            <h1 className="post-title">Week 14, 2025: Links and things</h1>
            <p className="post-meta">
              <time>2025-04-07</time>
            </p>
          </header>
          <div className="post-content">
            <p>
              Following{" "}
              <a href="/posts/2025-03-30-week-13-links-and-things">
                Week 13, 2025: Links and things
              </a>
              , here comes Week 14, 25: Links and things! Although it is already
              the monday of Week 15.
            </p>
            <p>Let’s kick off with some AI stuff:</p>
            <ul>
              <li>
                <a href="https://ai-2027.com/">AI 2027</a>
                is a nauseating read: “We predict that the impact of superhuman
                AI over the next decade will be enormous, exceeding that of the
                Industrial Revolution.”{" "}
                <a href="https://www.astralcodexten.com/p/introducing-ai-2027">
                  Astral Codex Ten: Introducing AI 2027
                </a>
                introduces the project here, and I hope to also listen to – or
                maybe even watch – this{" "}
                <a href="https://youtu.be/htOvH12T7mU">podcast episode</a>. Also
                see{" "}
                <a href="https://haggstrom.blogspot.com/2025/04/recommending-ai-2027-report-by.html">
                  this post by Olle Häggström
                </a>
                .
              </li>
            </ul>
            <p>
              Finally, I bought a very cool domain name:{" "}
              <a href="https://ournewinsectoverlords.com">
                ournewinsectoverlords.com
              </a>
              . I’m thinking about what to do with it. Possibly just moving this
              blog there.
            </p>
          </div>
        </article>
      </div>
    </div>
  );
}
