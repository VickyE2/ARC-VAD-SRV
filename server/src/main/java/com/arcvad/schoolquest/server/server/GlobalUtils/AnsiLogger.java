package com.arcvad.schoolquest.server.server.GlobalUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings("preview")
public class AnsiLogger {

    // ANSI color codes
    private static final String RESET = "\u001B[0m ";
    private static final String BLACK = "\u001B[30m ";
    private static final String RED = "\u001B[31m ";
    private static final String GREEN = "\u001B[32m ";
    private static final String YELLOW = "\u001B[33m ";
    private static final String BLUE = "\u001B[34m ";
    private static final String PURPLE = "\u001B[35m ";
    private static final String CYAN = "\u001B[36m ";
    private static final String WHITE = "\u001B[37m ";

    // Bright colors
    private static final String BRIGHT_BLACK = "\u001B[90m ";
    private static final String BRIGHT_RED = "\u001B[91m ";
    private static final String BRIGHT_GREEN = "\u001B[92m ";
    private static final String BRIGHT_YELLOW = "\u001B[93m ";
    private static final String BRIGHT_BLUE = "\u001B[94m ";
    private static final String BRIGHT_PURPLE = "\u001B[95m ";
    private static final String BRIGHT_CYAN = "\u001B[96m ";
    private static final String BRIGHT_WHITE = "\u001B[97m ";

    //styles
    public static final String BOLD = "\033[1m";
    public static final String ITALIC = "\033[3m";
    public static final String BOLD_ITALIC = "\033[1m\033[3m";
    public static final String UNDERLINE = "\033[4m";
    public static final String STRIKETHROUGH = "\033[9m" ;

    // Map to associate color names with their ANSI codes
    private static final Map<String, String> COLOR_MAP = new HashMap<>();
    static {
        COLOR_MAP.put("black", BLACK);
        COLOR_MAP.put("red", RED);
        COLOR_MAP.put("green", GREEN);
        COLOR_MAP.put("yellow", YELLOW);
        COLOR_MAP.put("blue", BLUE);
        COLOR_MAP.put("purple", PURPLE);
        COLOR_MAP.put("cyan", CYAN);
        COLOR_MAP.put("white", WHITE);
        COLOR_MAP.put("bright_black", BRIGHT_BLACK);
        COLOR_MAP.put("bright_red", BRIGHT_RED);
        COLOR_MAP.put("bright_green", BRIGHT_GREEN);
        COLOR_MAP.put("bright_yellow", BRIGHT_YELLOW);
        COLOR_MAP.put("bright_blue", BRIGHT_BLUE);
        COLOR_MAP.put("bright_purple", BRIGHT_PURPLE);
        COLOR_MAP.put("bright_cyan", BRIGHT_CYAN);
        COLOR_MAP.put("bright_white", BRIGHT_WHITE);
        COLOR_MAP.put("bold", BOLD);
        COLOR_MAP.put("bold_italic", BOLD_ITALIC);
        COLOR_MAP.put("italic", ITALIC);
        COLOR_MAP.put("underline", UNDERLINE);
        COLOR_MAP.put("strikethrough", STRIKETHROUGH);
    }

    // Instance variables for saving logs
    private boolean saveLogs;
    private String logFilePath;
    private String sessionId;

    /**
     * Constructor to initialize the logger and optionally save logs to a file.
     *
     * @param saveLogs Whether to save logs to a file.
     * @param logFilePath The path of the log file to save logs to.
     */
    public AnsiLogger(boolean saveLogs, String logFilePath) {
        this.saveLogs = saveLogs;
        this.logFilePath = logFilePath;
        this.sessionId = generateSessionId(); // Generate a unique session ID
    }

    // Generate a unique session ID based on the current timestamp and a random UUID
    private String generateSessionId() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String uniqueId = UUID.randomUUID().toString().substring(0, 8); // Get first 8 characters of UUID
        return timestamp + "_" + uniqueId; // Combine timestamp and UUID for uniqueness
    }

    // Regex pattern to match color markers like red[...] or purple[...]
    private static final Pattern COLOR_PATTERN = Pattern.compile("([a-zA-Z_]+)\\[([^\\]]+)\\]");

    /**
     * Formats the log message with color and timestamp.
     *
     * @param tag The tag to prepend to the log message.
     * @param level The level of the log (e.g., INFO, ERROR, DEBUG).
     * @param message The message to log.
     * @return The formatted log message.
     */
    private String formatLogMessage(String tag, String level, String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return String.format("[%s] [%s] %s : %s", timestamp, tag != null ? tag : "LOGGER-EXAMPLE-TAG", level, message);
    }

    /**
     * Formats the input message by replacing color markers with ANSI escape sequences.
     *
     * @param message The original message containing color markers.
     * @return The message with color formatting applied.
     */
    private String formatMessageWithColors(String message) {
        while (true) {
            Matcher matcher = COLOR_PATTERN.matcher(message);

            if (!matcher.find()) {
                // Break the loop if no matches are found
                break;
            }

            String color = matcher.group(1).toLowerCase(); // Extract color/style name
            String text = matcher.group(2);               // Extract text within the brackets

            // Recursively process the inner text
            String processedText = formatMessageWithColors(text);

            // Look up the ANSI code for the color/style, or default to RESET if unknown
            String colorCode = COLOR_MAP.getOrDefault(color, RESET);

            // Replace the matched segment with the processed content
            message = matcher.replaceFirst(Matcher.quoteReplacement(colorCode + processedText + RESET));
        }

        return message;
    }

    public static String removeAnsiCodes(String input) {
        return input.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    /**
     * Writes a log message to a file, creating the file if it does not exist.
     *
     * @param logMessage The formatted log message to write.
     */
    private void writeLogToFile(String logMessage) {
        try {
            // Use the session ID in the log file name (e.g., log-2024-11-25_16-25-10_abc12345.log)
            File logFile = new File(logFilePath + "log-" + sessionId + ".log");

            // Check if the log file exists, create if it doesn't
            if (!logFile.exists()) {
                File logPath = new File(logFilePath);
                logPath.mkdirs();
                logFile.createNewFile();
            }

            // Write the log message to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                logMessage = removeAnsiCodes(logMessage); // Remove ANSI codes before writing
                writer.write(logMessage);
                writer.newLine(); // Add a newline after each log entry
            }
        } catch (IOException e) {
            System.err.println("Failed to write log to file: " + e.getMessage());
        }
    }
    /**
     * Logs a message with "INFO" level.
     */
    public void info(String tag, String message) {
        log(tag, "INFO", message);
    }

    public void toast(String message) {
        String formattedMessage = formatMessageWithColors(message);
        System.out.println(new String(formattedMessage.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
    }

    /**
     * Logs a message with "WARNING" level.
     */
    public void warning(String tag, String message) {
        log(tag, "WARNING", StringTemplate.STR."yellow[\{message}]");
    }

    /**
     * Logs a message with "ERROR" level.
     */
    public void error(String tag, String message) {
        log(tag, "ERROR", StringTemplate.STR."bright_red[\{message}]");
    }

    /**
     * Logs a message with "DEBUG" level.
     */
    public void debug(String tag, String message) {
        log(tag, "DEBUG", message);
    }

    /**
     * Logs a message with "SUCCESS" level.
     */
    public void success(String tag, String message) {
        log(tag, "SUCCESS", StringTemplate.STR."green[\{message}]");
    }

    /**
     * Logs a message with "SEVERE" level.
     */
    public void severe(String tag, String message) {
        log(tag, "SEVERE", StringTemplate.STR."red[\{message}]");
    }

    /**
     * Logs a message with the specified level.
     *
     * @param tag The tag to prepend to the log message.
     * @param level The log level (e.g., INFO, ERROR, DEBUG).
     * @param message The message to log.
     */
    private void log(String tag, String level, String message) {
        // Format the log message with timestamp, level, and tag
        String logMessage = formatLogMessage(tag, level, message);

        // Format the message with ANSI colors
        String formattedMessage = formatMessageWithColors(logMessage);

        // Output to console
        System.out.println(new String(formattedMessage.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));

        // If saveLogs is true, write the log to a file
        if (saveLogs && !logFilePath.isEmpty()) {
            writeLogToFile(formattedMessage);
        }
    }

    /**
     * Adds a new color to the color map with the corresponding ANSI code.
     *
     * @param colorName The name of the new color (e.g., "orange").
     * @param ansiCode  The ANSI escape sequence for the color (e.g., "\u001B[38;5;214m").
     */
    public void addCustomColor(String colorName, String ansiCode) {
        COLOR_MAP.put(colorName.toLowerCase(), ansiCode);
    }
}
