package com.codewaves.codehighlight.languages;

import com.codewaves.codehighlight.core.Language;
import com.codewaves.codehighlight.core.Mode;

/**
 * @author Sayi
 * 
 */
public class MarkdownLanguage implements LanguageBuilder {
  @Override
  public Language build() {
      final Mode INLINE_HTML = new Mode()
            .begin("<\\/?[A-Za-z_]")
            .end(">")
            .subLanguage("xml")
            .relevance(0);
      final Mode HORIZONTAL_RULE = new Mode()
              .begin("^[-\\*]{3,}")
              .end("$");
      
      final Mode CODE = new Mode()
              .className("code")
              .variants(new Mode[] {
                  new Mode().begin("(`{3,})[^`](.|\\n)*?\\1`*[ ]*"),
                  new Mode().begin("(~{3,})[^~](.|\\n)*?\\1~*[ ]*"),
                  new Mode().begin("```").end("```+[ ]*$"),
                  new Mode().begin("~~~").end("~~~+[ ]*$"),
                  new Mode().begin("`.+?`"),
                  new Mode().begin("^( {4}|\\t)").end("$"),
                  new Mode().begin("(?=^( {4}|\\t))")
                      .contains(new Mode[] {
                              new Mode().begin("^( {4}|\\t)").end("(\\n)$"),
                      }).relevance(0),
              });
      
      final Mode LIST = new Mode()
            .className("bullet")
            .begin("^[ \t]*([*+-]|(\\d+\\.))(?=\\s+)")
            .end("\\s+")
            .excludeEnd();
      
      final Mode LINK_REFERENCE = new Mode()
              .begin("^\\[[^\\n]+\\]:")
              .returnBegin()
              .contains(new Mode[] {
                    new Mode().className("symbol").begin("\\[").end("\\]").excludeBegin().excludeEnd(),
                    new Mode().className("link").begin(":\\s*").end("$").excludeBegin()
              });
      final String URL_SCHEME = "[A-Za-z][A-Za-z0-9+.-]*";
      final Mode LINK = new Mode()
              .variants(new Mode[] {
                      new Mode().begin("\\[.+?\\]\\[.*?\\]").relevance(0),
                      new Mode().begin("\\[.+?\\]\\(((data|javascript|mailto):|(?:http|ftp)s?:\\/\\/).*?\\)").relevance(2),
                      new Mode().begin("\\[.+?\\]\\(" + URL_SCHEME + ":\\/\\/.*?\\)").relevance(2),
                      new Mode().begin("\\[.+?\\]\\([./?&#].*?\\)").relevance(1),
                      new Mode().begin("\\[.+?\\]\\(.*?\\)").relevance(0),
              })
              .returnBegin()
              .contains(new Mode[] {
                      new Mode().className("string").relevance(0).begin("\\[").end("\\]").excludeBegin().returnEnd(),
                      new Mode().className("link").relevance(0).begin("\\]\\(").end("\\)").excludeBegin().returnEnd(),
                      new Mode().className("symbol").relevance(0).begin("\\]\\[").end("\\]").excludeBegin().returnEnd(),
              });
      final Mode BOLD = new Mode()
              .className("strong")
              .contains(new Mode[] {})
              .returnBegin()
              .variants(new Mode[] {
                    new Mode().begin("_{2}").end("_{2}"),
                    new Mode().begin("\\*{2}").end("\\*{2}")
              });
      final Mode ITALIC = new Mode()
              .className("emphasis")
              .contains(new Mode[] {})
              .returnBegin()
              .variants(new Mode[] {
                      new Mode().begin("\\*(?!\\*)").end("\\*"),
                      new Mode().begin("_(?!_)").end("_").relevance(0)
              });
      final Mode BOLDCOPY = new Mode()
              .className("strong")
              .contains(new Mode[] {})
              .returnBegin()
              .variants(new Mode[] {
                      new Mode().begin("_{2}").end("_{2}"),
                      new Mode().begin("\\*{2}").end("\\*{2}")
              });
      final Mode ITALICCOPY = new Mode()
              .className("emphasis")
              .contains(new Mode[] {})
              .returnBegin()
              .variants(new Mode[] {
                      new Mode().begin("\\*(?!\\*)").end("\\*"),
                      new Mode().begin("_(?!_)").end("_").relevance(0)
              });
      BOLD.contains(new Mode[] {ITALICCOPY,INLINE_HTML, LINK});
      ITALIC.contains(new Mode[] {BOLDCOPY,INLINE_HTML, LINK});
      Mode[] CONTAINABLE = new Mode[] { INLINE_HTML, LINK, ITALICCOPY, BOLDCOPY };
      final Mode HEADER = new Mode()
              .className("section")
              .variants(new Mode[] {
                      new Mode().begin("^#{1,6}")
                      .contains(CONTAINABLE),
                      new Mode().begin("(?=^.+?\\n[=-]{2,}$)").contains(new Mode[] {
                              new Mode().begin("^[=-]*$"),
                              new Mode().begin("^").end("\\n").contains(CONTAINABLE),
                      })
              });
      final Mode BLOCKQUOTE = new Mode()
              .className("quote")
              .begin("^>\\s+")
              .contains(CONTAINABLE)
              .end("\\s+");
      return (Language) new Language()
            .aliases(new String[] {"md", "mkdown", "mkd"})
            .contains(new Mode[] {
                     HEADER,
                     INLINE_HTML,
                    LIST,
                    BOLD,
                    ITALIC,
                    BLOCKQUOTE,
                    CODE,
                    HORIZONTAL_RULE,
                    LINK,
                    LINK_REFERENCE
            });
   }
}
