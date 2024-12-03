package com.arcvad.schoolquest.server.server.Commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import static com.arcvad.schoolquest.server.server.DataFormat.Converter.DataConverter.*;
import static com.arcvad.schoolquest.server.server.GlobalUtils.Config.getConfigValue;
import static com.arcvad.schoolquest.server.server.GlobalUtils.Config.saveConfigValue;
import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

@SuppressWarnings("preview")
@Command(name="convert",
        version="beta-1.0",
        description = "User sensibly. used to change from SQLITE, XML, or JSON data format",
        mixinStandardHelpOptions = true
    )
    public class ConvertCommand implements Callable<Integer> {

    @Option(
        names = {"-d", "--delay"},
        description = "Give an integer in seconds on how long to wait before the backup commences"
    )
    private long delay = 0;

    @Option(
        names = {"-b", "--backup"},
        description = "With default value of true, it says weather or not to backup the previous data format after conversion"
    )
    private boolean backup = true;

    @Option(
        names = {"-f", "--flush"},
        description = "With default value of false, it says weather or not to delete all data of previous data format after conversion"
    )
    private boolean flush = false;

    @Parameters(
        description = "Write a non-case sensitive valid data format. <XML, JSON, SQLITE>"
    )
    public String dataFormat;

    @Override
    public Integer call() throws Exception {
        if (delay > 0) {
            if (delay < 10) {
                System.out.println("The delay must not be less than 10 seconds. Aborting conversion.");
                return 1;
            }

            // Inform the user about the delay and wait for the specified time
            System.out.println(String.format("Delaying conversion for %d seconds...", delay));
            Thread.sleep(delay * 1000); // Convert seconds to milliseconds
        }

        String currentFormat = getConfigValue("server_data_format").toString();

        if (!dataFormat.equalsIgnoreCase("XML") &&
            !dataFormat.equalsIgnoreCase("JSON") &&
            !dataFormat.equalsIgnoreCase("SQLITE")) {

            System.out.println(StringTemplate.STR."Data format: \{dataFormat} is not a valid data format <XML, JSON, SQLITE>");
            return 1;
        }

        if (currentFormat.equalsIgnoreCase(dataFormat)) {
            System.out.println(StringTemplate.STR."Cannot convert server data format as it is already \{dataFormat}");
            return 0;
        }

        boolean conversionResult = false;

        switch (dataFormat.toUpperCase()) {
            case "JSON":
                if (currentFormat.equalsIgnoreCase("XML")) {
                    conversionResult = convertXmlToJson(backup, flush);
                } else if (currentFormat.equalsIgnoreCase("SQLITE")) {
                    // Add a method to convert from SQLITE to JSON if needed
                    System.out.println("Conversion from SQLITE to JSON is not yet implemented.");
                    return 1;
                }
                break;

            case "SQLITE":
                if (currentFormat.equalsIgnoreCase("XML")) {
                    conversionResult = convertXmlToSql(backup, flush);
                } else if (currentFormat.equalsIgnoreCase("JSON")) {
                    conversionResult = convertJsonToSql(backup, flush);
                }
                break;

            case "XML":
                if (currentFormat.equalsIgnoreCase("JSON")) {
                    conversionResult = convertJsonToXml(backup, flush);
                } else if (currentFormat.equalsIgnoreCase("SQLITE")) {
                    conversionResult = convertSqlToXml(backup, flush);
                }
                break;

            default:
                System.out.println("Unsupported conversion type.");
                return 1;
        }

        if (conversionResult) {
            logger.success(
                "ARC-CONVERT",
                String.format(
                    "Data was successfully converted to %s format%s%s%s.",
                    dataFormat,
                    (backup && flush ? ", backed up" : ""),
                    (backup && !flush ? " and backed up" : ""),
                    (flush ? " and flushed" : "")
                )
            );
            saveConfigValue("server_data_format", dataFormat.toUpperCase());
            saveConfigValue("former_data_format", currentFormat.toUpperCase());
            return 0;
        } else {
            logger.severe("ARC-CONVERT", String.format("Failed to convert to %s format. Please check logs...", dataFormat));
            return 1;
        }
    }

}


