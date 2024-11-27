package com.arcvad.schoolquest.server.server.GlobalUtils;

import com.arcvad.schoolquest.server.server.ARCServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

public class Config {
    // Load the configuration file (if it doesn't exist, it will be created)
    public static File loadConfig() {
        // The folder where you want to save the file
        String folderPath = "ServerData/configs";
        String filePath = folderPath + "/server_meta.json";

        InputStream inputStream = ARCServer.class.getResourceAsStream("/configs/server_meta.json");

        try {
            // Ensure the folder exists
            Path path = Paths.get(folderPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);  // Create the folder if it doesn't exist
            }

            // Check if the file exists in the specified folder, and if not, copy it from the input stream
            File file = new File(filePath);
            if (!file.exists()) {
                try (OutputStream outputStream = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }

            return file;

        } catch (IOException e) {
            e.printStackTrace();
            logger.error("ARC-CONFIG", "Failed to get config file...");
            return null;
        }
    }

    // Get a configuration value by key
    public static Object getConfigValue(String key) {
        File file = loadConfig();
        var gson = new Gson();
        try (FileReader reader = new FileReader(file)) {
            Map data = gson.fromJson(reader, Map.class);
            Object value = data.getOrDefault(key, "none");

            return value;
        } catch (IOException e) {
            logger.error("ARC-CONFIG", "Failed to get value from config file...");
            return "";
        }
    }

    // Save a configuration value by key
    public static boolean saveConfigValue(String key, Object value) {
        File file = loadConfig();
        var gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileReader reader = new FileReader(file)) {
            // Read the existing config
            Map data = gson.fromJson(reader, Map.class);

            // Update the value in the map
            data.put(key, value);

            // Write the updated data back to the file
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(data, writer);
            }

            return true;

        } catch (IOException e) {
            logger.error("ARC-CONFIG", "Failed to save value to config file...");
            return false;
        }
    }
}
