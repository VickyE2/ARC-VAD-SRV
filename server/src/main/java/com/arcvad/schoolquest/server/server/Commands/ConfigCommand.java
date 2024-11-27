package com.arcvad.schoolquest.server.server.Commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.Objects;
import java.util.concurrent.Callable;

import static com.arcvad.schoolquest.server.server.GlobalUtils.Config.saveConfigValue;

@Command(name="config",
        version="beta-1.0",
        description = "Used to handle config values from the command line",
        mixinStandardHelpOptions = true,
        subcommands = {EnableConfig.class, ServerDataFormat.class}
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
            } else {
                System.out.println("Failed to apply changes. Please check logs");
            }
        } else {
            saved = saveConfigValue("enable_logger_saving", false);
            if (saved) {
                System.out.println("Logger saving has been disabled");
            } else {
                System.out.println("Failed to apply changes. Please check logs");
            }
        }
        return 0;
    }
}

@Command(
    name = "server_data_format",
    description = "Set weather or not the logger saves logs in files",
    version = "BETA-001"
)
class ServerDataFormat implements Callable<Integer> {

    @Parameters(index = "0", description = "Choose a server data format. How data is saved either XML, JSON, or DB")
    private String format;

    @Override
    public Integer call() {
        boolean saved;
        if (Objects.equals(format, "JSON") || format.equals("XML") || format.equals("DB")) {
            saved = saveConfigValue("server_data_format", format);
            if (saved) {
                System.out.printf("Server data format has been changed to '%s'", format);
            } else {
                System.out.println("Failed to apply changes. Please check logs");
            }
        } else {
            System.out.printf("'%s' is not a valid data format <XML, JSON, DB>", format);
        }
        return 0;
    }
}
