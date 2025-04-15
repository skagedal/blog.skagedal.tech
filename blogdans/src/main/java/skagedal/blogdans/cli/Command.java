package skagedal.blogdans.cli;

import picocli.CommandLine.Model.CommandSpec;

public sealed interface Command {
    record Serve() implements Command {
    }

    record Import() implements Command {
    }

    record Help(CommandSpec spec) implements Command {
    }
}
