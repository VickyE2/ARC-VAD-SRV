package com.arcvad.schoolquest.server.server.Commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import static com.arcvad.schoolquest.server.server.GlobalUtils.Config.saveConfigValue;

@Command(name="config",
        version="beta-1.0",
        description = "Used to handle config values from the command line",
        mixinStandardHelpOptions = true,
        subcommands = {EnableConfig.class}
)
public class ConfigCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        return 0;
    }
}

@Command(
    name = "enable_log_saving",
    description = "Set weather or not the logger saves logs in files"
)
class EnableConfig implements Callable<Integer> {

    @Parameters(index = "0", description = "Set true or false to enable o disable")
    private boolean enable_logger;

    @Override
    public Integer call() {
        boolean saved;
        if (enable_logger) {
            saved = saveConfigValue("enable_logger_saving", true);
            if (saved) {
                System.out.println("Logger saving has been enabled");
                return 0;
            } else {
                System.out.println("Failed to apply changes. Please check logs");
                return 1;
            }
        } else {
            saved = saveConfigValue("enable_logger_saving", false);
            if (saved) {
                System.out.println("Logger saving has been disabled");
                return 0;
            } else {
                System.out.println("Failed to apply changes. Please check logs");
                return 1;
            }
        }
    }
}
