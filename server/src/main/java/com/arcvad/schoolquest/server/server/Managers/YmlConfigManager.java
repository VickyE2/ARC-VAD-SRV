package com.arcvad.schoolquest.server.server.Managers;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class YmlConfigManager {

    public ConfigurationLoader<CommentedConfigurationNode> loader;
    public CommentedConfigurationNode rootNode;
    public ConfigurationOptions options;

    // Constructor 1: With a specified path
    public YmlConfigManager(String path) {
        createConfig(path);
    }

    // Constructor 2: Without a path
    public YmlConfigManager() {
    }

    // Create or load the config file
    public void createConfig(String path) {
        File configFile = new File(path);

        options =
            YamlConfigurationLoader.builder().indent(2).nodeStyle(NodeStyle.BLOCK).defaultOptions();

        loader =
            YamlConfigurationLoader.builder()
                .path(configFile.toPath())
                .indent(2)
                .nodeStyle(NodeStyle.BLOCK)
                .build();

        if (!configFile.exists()) {
            try {
                // Create the file if it does not exist
                configFile.createNewFile();
                System.out.println("Created new config file: " + path);
                loadConfigValues();
            } catch (IOException e) {
                System.out.println("Failed to create new config file: " + e.getMessage());
            }
        } else {
            loadConfigValues();
        }
    }

    public void createPathedConfig(String pat) {
        File configFile = new File(pat);

        options =
            YamlConfigurationLoader.builder().indent(2).nodeStyle(NodeStyle.BLOCK).defaultOptions();

        loader =
            YamlConfigurationLoader.builder()
                .path(configFile.toPath())
                .indent(2)
                .nodeStyle(NodeStyle.BLOCK)
                .build();

        if (!configFile.exists()) {
            try {
                // Create the file if it does not exist
                configFile.createNewFile();
                System.out.println("Created new config file: " + pat);
                loadConfigValues();
            } catch (IOException e) {
                System.out.println("Failed to create new config file: " + e.getMessage());
            }
        } else {
            loadConfigValues();
        }
    }

    public void loadConfig(String path, String file) {
        File configFile = new File(path, file);

        options = YamlConfigurationLoader.builder().defaultOptions();

        loader = YamlConfigurationLoader.builder().path(configFile.toPath()).build();

        if (!configFile.exists()) {
            System.out.println("File " + file + "does not exist.");
        } else {
            loadConfigValues();
        }
    }

    // Load configuration values
    public void loadConfigValues() {
        try {
            rootNode = loader.load(options); // Load the config directly
            System.out.println("Config loaded successfully.");
        } catch (Exception e) {
            System.out.println("Failed to load configurations from config.yml " + e);
            e.getCause();
        }
    }

    public void loadConfigFromZip(Path zipFilePath, String configFileName) throws IOException {
        try (ZipFile zipFile = new ZipFile(zipFilePath.toFile())) {
            ZipEntry entry = zipFile.getEntry(configFileName);

            if (entry != null && !entry.isDirectory()) {
                try (InputStream inputStream = zipFile.getInputStream(entry);
                     InputStreamReader reader = new InputStreamReader(inputStream)) {

                    // Create a YamlConfigurationLoader using the InputStream
                    YamlConfigurationLoader loader =
                        YamlConfigurationLoader.builder()
                            .source(() -> new BufferedReader(reader))
                            .nodeStyle(NodeStyle.BLOCK)
                            .build();

                    // Load the YAML config directly from the input stream
                    rootNode = loader.load(ConfigurationOptions.defaults());

                    // Log success
                    System.out.println(
                        configFileName
                            + " Has been successfully loaded from zip-file: "
                            + zipFilePath.getFileName());
                }
            } else {
                System.out.println(
                    "Could not find the required '"
                        + configFileName
                        + "' config file inskeye zip-file: "
                        + zipFilePath.getFileName());
            }
        } catch (Exception e) {
            System.out.println(
                "Failed to load configurations from zip file: "
                    + zipFilePath.getFileName()
                    + " reason: "
                    + e.getMessage());
        }
    }

    // Save the configuration to disk
    public void saveConfig() {
        try {
            loader.save(rootNode);
        } catch (IOException e) {
            System.out.println("Could not save config.yml!");
            e.printStackTrace();
        }
    }

    // Check if a path exists in the configuration
    public boolean doesPathExist(String path) {
        return !rootNode.node((Object[]) path.split("\\.")).virtual();
    }

    // Get a generic config value
    public Object getConfigValue(String path) {
        try {
            return rootNode.node((Object[]) path.split("\\.")).get(Object.class);
        } catch (Exception e) {
            System.out.println("Failed to get config value at path: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public void setListConfigValue(String key, List<String> values) {
        try {
            CommentedConfigurationNode node = rootNode.node((Object[]) key.split("\\."));
            node.set(values); // Set the list of values
            saveConfig(); // Save changes to the config file
            System.out.println("Config value set for " + key + ": " + values);
        } catch (Exception e) {
            System.out.println("Failed to set config value for " + key + ": " + e.getMessage());
        }
    }

    // Method to retrieve a list of strings from a specified key
    public List<String> getListConfigValue(String key) {
        List<String> values = new ArrayList<>();
        try {
            ConfigurationNode node = rootNode.node((Object[]) key.split("\\."));
            for (ConfigurationNode childNode : node.childrenList()) {
                String value = childNode.getString();
                if (value != null) {
                    values.add(value);
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to get list config value for " + key + ": " + e.getMessage());
        }
        return values;
    }

    // Get boolean config value
    public boolean getBooleanValue(String path) {
        return rootNode.node((Object[]) path.split("\\.")).getBoolean();
    }

    // Get double config value
    public double getDoubleValue(String path) {
        return rootNode.node((Object[]) path.split("\\.")).getDouble();
    }

    public int getIntegerValue(String path) {
        return rootNode.node((Object[]) path.split("\\.")).getInt();
    }

    // Get string config value
    public String getStringValue(String path) {
        return rootNode.node((Object[]) path.split("\\.")).getString();
    }

    // Set a config value, ensuring parent nodes exist
    public void setConfigValue(String parentKey, String childKey, Object value, String comment) {
        try {
            // Split the parentKey and childKey to handle nested structures
            CommentedConfigurationNode parentNode = rootNode.node((Object[]) parentKey.split("\\."));
            CommentedConfigurationNode childNode = parentNode.node((Object[]) childKey.split("\\."));

            // Set the value and comment
            childNode.set(value).comment(comment);
        } catch (Exception e) {
            System.out.println("Failed to add config: " + parentKey + "." + childKey + " with value: " + value);
        }
        saveConfig(); // Save changes to the config file
    }

    // Set a braced config value (for parent nodes)
    public void setBracedConfigValue(String key, Object value, String comment) {
        try {
            rootNode.node(key).set(value).comment(comment);
        } catch (Exception e) {
            System.out.println("Failed to add config: " + key);
        }
        saveConfig(); // Save changes to the config file
    }
}
