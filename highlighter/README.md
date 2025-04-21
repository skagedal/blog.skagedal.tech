An attempt at using tree-sitter for syntax highlighting: https://tree-sitter.github.io/tree-sitter/3-syntax-highlighting.html

There are a Java bindings for tree-sitter, but not for the syntax highlighting API specifically, so I thought I'd have a little rust cli app that calls that. Since `tree-sitter highlight` does weird things. But I'm failing at rust.
