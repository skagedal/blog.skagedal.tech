package skagedal.blogdans.cli;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

public class Cli {
    private final CommandSpec importCommand = CommandSpec.create()
        .mixinStandardHelpOptions(true)
        .name("import");
    private final CommandSpec serveCommand = CommandSpec.create()
        .mixinStandardHelpOptions(true)
        .name("serve");


    private final CommandSpec spec = CommandSpec.create()
        .mixinStandardHelpOptions(true)
        .addSubcommand(serveCommand.name(), serveCommand)
        .addSubcommand(importCommand.name(), importCommand);

    private final CommandLine commandLine = new CommandLine(spec);

    public Command parse(final String[] args) {
        final var parsed = commandLine.parseArgs(args);
        if (parsed.subcommand() instanceof CommandLine.ParseResult command) {
            if (command.isUsageHelpRequested())
                return new Command.Help(command.commandSpec());

            if (command.commandSpec() == importCommand)
                return new Command.Import();
            if (command.commandSpec() == serveCommand)
                return new Command.Serve();

            throw new IllegalStateException("Unhandled subcommand.");
        } else {
            return new Command.Help(spec);
        }
    }

    public void printHelp(final Command.Help help) {
        new CommandLine(help.spec()).usage(System.out);
    }

    private static CommandSpec subcommand() {
        return CommandSpec.create()
            .mixinStandardHelpOptions(true);
    }
}
