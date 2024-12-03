package com.arcvad.schoolquest.server.server.Commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model;
import picocli.CommandLine.Spec;

import java.util.concurrent.Callable;

import static picocli.CommandLine.usage;

@Command(
    name = "",
    description = "Server commands",
    mixinStandardHelpOptions = true,
    subcommands = {ConfigCommand.class, HelpCommand.class, ConvertCommand.class}
)
public class CommandManager implements Callable<Integer> {

    @Override
    public Integer call() {
        System.out.println("Use --help for usage information.");
        return 0;
    }
}

@Command(
    name = "help",
    description = "Displays help information about commands.",
    mixinStandardHelpOptions = true
)
class HelpCommand implements Callable<Integer> {

    @Spec
    Model.CommandSpec spec;

    @Override
    public Integer call() {
        usage(spec.root().commandLine(), System.out);
        return 0;
    }
}
