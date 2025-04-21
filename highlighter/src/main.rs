use std::{
    fs,
    io::{self, Write as _},
};
use tree_sitter_highlight::{HighlightConfiguration, Highlighter, HtmlRenderer};

fn main() {
    let stdout = io::stdout();
    let mut stdout = stdout.lock();

    let mut highlighter = Highlighter::new();

    let highlight_names = [
        "attribute",
        "comment",
        "constant",
        "constant.builtin",
        "constructor",
        "embedded",
        "function",
        "function.builtin",
        "keyword",
        "module",
        "number",
        "operator",
        "property",
        "property.builtin",
        "punctuation",
        "punctuation.bracket",
        "punctuation.delimiter",
        "punctuation.special",
        "string",
        "string.special",
        "tag",
        "type",
        "type.builtin",
        "variable",
        "variable.builtin",
        "variable.parameter",
    ];

    let java_language = tree_sitter_java::LANGUAGE;
    let mut java_config = HighlightConfiguration::new(
        java_language.into(),
        "java",
        tree_sitter_java::HIGHLIGHTS_QUERY,
        "",
        "",
    )
    .unwrap();
    java_config.configure(&highlight_names);


    // let mut source: Vec<u8> = Vec::new();
    // let result = io::stdin().read_to_end(&mut source).unwrap();
    let source = fs::read("hello.java").unwrap();

    let highlights = highlighter
        .highlight(&java_config, &source, None, |_| None)
        .unwrap();

    let mut renderer = HtmlRenderer::new();
    renderer.render(highlights, &source, &move |_highlight, output| {
        output.extend(b"class='foo'");
        // let mut parts = theme.highlight_names[highlight.0].split('.').peekable();
        // while let Some(part) = parts.next() {
        //     output.extend(part.as_bytes());
        //     if parts.peek().is_some() {
        //         output.extend(b" ");
        //     }
        // }
        // output.extend(b"'");
    }).unwrap();

    for (i, line) in renderer.lines().enumerate() {
        writeln!(
            &mut stdout,
            "<tr><td class=line-number>{}</td><td class=line>{line}</td></tr>",
            i + 1,
        ).unwrap();
    }

    // for event in highlights {
    //     match event.unwrap() {
    //         HighlightEvent::Source {start, end} => {
    //             eprintln!("source: {start}-{end}");
    //         },
    //         HighlightEvent::HighlightStart(s) => {
    //             eprintln!("highlight style started: {s:?}");
    //         },
    //         HighlightEvent::HighlightEnd => {
    //             eprintln!("highlight style ended");
    //         },
    //     }
    // }
}
