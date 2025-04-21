package com.codewaves.codehighlight.languages;

import com.codewaves.codehighlight.core.Keyword;
import com.codewaves.codehighlight.core.Language;
import com.codewaves.codehighlight.core.Mode;
import com.codewaves.codehighlight.core.Regex;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Sayi
 */
public class SqlLanguage implements LanguageBuilder {
   private static String KEYWORDS_LITERAL = "true false unknown null";
   private static String KEYWORDS_BUILTIN = "current_catalog current_date current_default_transform_group "
           + "current_path current_role current_schema current_transform_group_for_type current_user session_user "
           + "system_time system_user current_time localtime current_timestamp localtimestamp";
   private static String TYPES = "bigint binary blob boolean char character clob date dec decfloat decimal float int "
           + "integer interval nchar nclob national numeric real row smallint time timestamp varchar varying varbinary";

   private static String FUNCTIONS = "abs acos array_agg asin atan avg cast ceil ceiling coalesce corr cos cosh count"
           + " covar_pop covar_samp cume_dist dense_rank deref element exp extract first_value floor json_array "
           + "json_arrayagg json_exists json_object json_objectagg json_query json_table json_table_primitive json_value "
           + "lag last_value lead listagg ln log log10 lower max min mod nth_value ntile nullif percent_rank percentile_cont "
           + "percentile_disc position position_regex power rank regr_avgx regr_avgy regr_count regr_intercept regr_r2 "
           + "regr_slope regr_sxx regr_sxy regr_syy row_number sin sinh sqrt stddev_pop stddev_samp substring substring_regex "
           + "sum tan tanh translate translate_regex treat trim trim_array unnest upper value_of var_pop var_samp width_bucket";
   private static String[] COMBOS = new String[] { "create table", "insert into", "primary key", "foreign key",
           "not null", "alter table", "add constraint", "grouping sets", "on overflow", "character set",
           "respect nulls", "ignore nulls", "nulls first", "nulls last", "depth first", "breadth first" };
   private static String[] MULTI_WORD_TYPES = new String[] { "double precision", "large object", "with timezone",
           "without timezone" };
   private static String NON_RESERVED_WORDS = "add asc collation desc final first last view";
   // https://jakewheat.github.io/sql-overview/sql-2016-foundation-grammar.html#reserved-word
  private static String RESERVED_WORDS =  "abs acos all allocate alter and any are array array_agg array_max_cardinality "
          + "as asensitive asin asymmetric at atan atomic authorization avg begin begin_frame begin_partition between "
          + "bigint binary blob boolean both by call called cardinality cascaded case cast ceil ceiling char char_length "
          + "character character_length check classifier clob close coalesce collate collect column commit condition connect "
          + "constraint contains convert copy corr corresponding cos cosh count covar_pop covar_samp create cross cube "
          + "cume_dist current current_catalog current_date current_default_transform_group current_path current_role "
          + "current_row current_schema current_time current_timestamp current_path current_role current_transform_group_for_type "
          + "current_user cursor cycle date day deallocate dec decimal decfloat declare default define delete dense_rank deref "
          + "describe deterministic disconnect distinct double drop dynamic each element else empty end end_frame end_partition "
          + "end-exec equals escape every except exec execute exists exp external extract false fetch filter first_value float "
          + "floor for foreign frame_row free from full function fusion get global grant group grouping groups having hold hour "
          + "identity in indicator initial inner inout insensitive insert int integer intersect intersection interval into is join "
          + "json_array json_arrayagg json_exists json_object json_objectagg json_query json_table json_table_primitive json_value "
          + "lag language large last_value lateral lead leading left like like_regex listagg ln local localtime localtimestamp log "
          + "log10 lower match match_number match_recognize matches max member merge method min minute mod modifies module month "
          + "multiset national natural nchar nclob new no none normalize not nth_value ntile null nullif numeric octet_length "
          + "occurrences_regex of offset old omit on one only open or order out outer over overlaps overlay parameter partition "
          + "pattern per percent percent_rank percentile_cont percentile_disc period portion position position_regex power precedes "
          + "precision prepare primary procedure ptf range rank reads real recursive ref references referencing regr_avgx regr_avgy "
          + "regr_count regr_intercept regr_r2 regr_slope regr_sxx regr_sxy regr_syy release result return returns revoke right "
          + "rollback rollup row row_number rows running savepoint scope scroll search second seek select sensitive session_user set "
          + "show similar sin sinh skip smallint some specific specifictype sql sqlexception sqlstate sqlwarning sqrt start static "
          + "stddev_pop stddev_samp submultiset subset substring substring_regex succeeds sum symmetric system system_time system_user "
          + "table tablesample tan tanh then time timestamp timezone_hour timezone_minute to trailing translate translate_regex "
          + "translation treat trigger trim trim_array true truncate uescape union unique unknown unnest update upper user using "
          + "value values value_of var_pop var_samp varbinary varchar varying versioning when whenever where width_bucket window "
          + "with within without year";

  private static String KEYWORDS;
  private static String REDUCE_KEYWORD;
  static {
      List<String> funcs = Arrays.asList(FUNCTIONS.split(" "));
      String[] keywords = Stream.concat(Arrays.stream(RESERVED_WORDS.split(" ")), Arrays.stream(NON_RESERVED_WORDS.split(" ")))
              .filter(e -> !funcs.contains(e)).toArray(String[]::new);
      KEYWORDS = String.join(" ", keywords);
      REDUCE_KEYWORD = String.join(" ", Arrays.stream(keywords).map(e -> {
          if (e.matches("\\|\\d+$")) {
              return e;
          } else if (e.length() < 3) {
              return e + "|0";
          }
          return e;
      }).toArray(String[]::new));
  }

  @Override
  public Language build() {
      final Mode VAR = new Mode()
            .className("variable")
            .begin("@[a-z0-9]+");
      
      final Mode OPERATOR = new Mode()
              .className("variable")
              .begin("[-+*/=%^~]|&&?|\\|\\|?|!=?|<(?:=>?|<|>)?|>[>=]?")
              .relevance(0);
      
      final Mode STRING = new Mode()
            .className("operator")
            .variants(new Mode[] {
                  new Mode().begin("'").end("'").contains(new Mode[] {
                          new Mode().begin("''")
                  })
            });
      final Mode QUOTED_IDENTIFIER = new Mode()
              .begin("\"")
              .end("\"")
              .contains(new Mode[] {
                      new Mode().begin("\"\"")
              });
      final Mode FUNCTION_CALL = new Mode()
              .begin("")
              .keywords(new Keyword[] {
                      new Keyword("built_in", FUNCTIONS)
                })
              .relevance(0);

      return (Language) new Language()
             .caseInsensitive()
            .illegal("[{}]|<\\/")
            .lexemes("\\b[\\w\\.]+")
            .keywords(new Keyword[] {
                  new Keyword("keyword", REDUCE_KEYWORD),
                  new Keyword("literal", KEYWORDS_LITERAL),
                  new Keyword("built_in", KEYWORDS_BUILTIN),
                  new Keyword("type", TYPES)
            })
            .contains(new Mode[] {
                  new Mode().begin(Regex.either(COMBOS))
                              .lexemes("[\\w\\.]+")
                              .keywords(new Keyword[] {
                                      new Keyword("keyword",
                                              Stream.concat(Arrays.stream(KEYWORDS.split(" ")), Arrays.stream(COMBOS))
                                                      .toArray(String[]::new)),
                                      new Keyword("literal", KEYWORDS_LITERAL),
                                      new Keyword("type", TYPES) }),
                  new Mode()
                        .className("type")
                        .begin(Regex.either(MULTI_WORD_TYPES)),
                  FUNCTION_CALL,
                  VAR,
                  STRING,
                  QUOTED_IDENTIFIER,
                  Mode.C_NUMBER_MODE,
                  Mode.C_BLOCK_COMMENT_MODE,
                  Mode.COMMENT("--", "$", null),
                  OPERATOR
            });
   }
}
