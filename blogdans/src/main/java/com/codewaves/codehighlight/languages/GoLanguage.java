package com.codewaves.codehighlight.languages;

import com.codewaves.codehighlight.core.Keyword;
import com.codewaves.codehighlight.core.Language;
import com.codewaves.codehighlight.core.Mode;

/**
 * @author Sayi
 */
public class GoLanguage implements LanguageBuilder {

    final String keyword = "break default func interface select case map struct chan else goto package switch "
            + "const fallthrough if range type continue for import return var go defer "
            + "bool byte complex64 complex128 float32 float64 int8 int16 int32 int64 string uint8 "
            + "uint16 uint32 uint64 int uint uintptr rune";
    final String literal = "true false iota nil";
    final String built_in = "append cap close complex copy imag len make new panic print println real recover delete";

  @Override
  public Language build() {
      return (Language) new Language()
            .aliases(new String[] {"golang"})
            .illegal("</")
            .keywords(new Keyword[] {
                  new Keyword("keyword", keyword),
                  new Keyword("literal", literal),
                  new Keyword("built_in", built_in)
            })
            .contains(new Mode[] {
                 Mode.C_LINE_COMMENT_MODE,
                 Mode.C_BLOCK_COMMENT_MODE,
                 new Mode().className("string")
                 .variants(new Mode[] {
                         Mode.QUOTE_STRING_MODE,
                         Mode.APOS_STRING_MODE,
                         new Mode().begin("`").end("`")
                 }),
                 new Mode().className("number")
                 .variants(new Mode[] {
                         new Mode().begin(Mode.C_NUMBER_RE + "[if]").relevance(1),
                         Mode.C_NUMBER_MODE
                 }),
                 new Mode().begin(":="),
                 new Mode().className("function")
                 .beginKeywords(new Keyword[] {new Keyword("_", "func")})
                 .end("\\s*(\\{|$)")
                 .excludeEnd()
                 .contains(new Mode[] {
                         Mode.TITLE_MODE,
                         new Mode().className("params").begin("\\(").end("\\)")
                                              .keywords(new Keyword[] { new Keyword("keyword", keyword),
                                                      new Keyword("literal", literal),
                                                      new Keyword("built_in", built_in) })
                                              .illegal("[\"']")
                 })
            });
   }
}
