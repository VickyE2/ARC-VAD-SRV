package com.arcvad.schoolquest.server.server.Managers;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.jackson.FieldValueSeparatorStyle;
import org.spongepowered.configurate.jackson.JacksonConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.loader.HeaderMode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

public class JsonConfigManager {
    public ConfigurationLoader<? extends ConfigurationNode> loader;
    public ConfigurationNode rootNode;
    public ConfigurationOptions options;

    public JsonConfigManager() {
    }

    // Create or load the config file
    public void createConfig(String path) {
        File configFile = new File(path);

        // Ensure the parent directories for the file exist
        File parentDir = configFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs(); // Create all necessary parent directories
        }

        options = JacksonConfigurationLoader.builder().headerMode(HeaderMode.PRESET).defaultOptions();

        loader =
            JacksonConfigurationLoader.builder()
                .path(configFile.toPath())
                .indent(2)
                .fieldValueSeparatorStyle(FieldValueSeparatorStyle.SPACE_BOTH_SIDES)
                .build();

        if (!configFile.exists()) {
            try {
                // Create the file if it does not exist
                configFile.createNewFile();
                try (FileWriter writer = new FileWriter(configFile)) {
                    writer.write("{}"); // Start with an empty JSON object
                }
                System.out.println("Created new json file: " + path);
            } catch (IOException e) {
                System.out.println("Failed to create new json file: " + e.getMessage());
            }
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

    // Save the configuration to disk
    public synchronized void saveConfig() {
        try {
            // Save your configuration
            loader.save(rootNode);
        } catch (ConfigurateException e) {
            System.out.println("Could not save config asynchronously: " + e.getMessage());
            if (e.getCause() instanceof AccessDeniedException) {
                System.out.println("Access denied while saving config. Check file permissions.");
            }
        }
    }

    // Check if a path exists in the configuration
    public synchronized boolean doesPathExist(String path) {
        return !rootNode.node((Object[]) path.split("\\.")).virtual();
    }

    // Get a generic config value
    public synchronized Object getConfigValue(String path) {
        try {
            return rootNode.node((Object[]) path.split("\\.")).get(Object.class);
        } catch (Exception e) {
            System.out.println("Failed to get config value at path: " + path);
            e.printStackTrace();
            return null;
        }
    }

    // Get boolean config value
    public synchronized boolean getBooleanValue(String path) {
        return rootNode.node((Object[]) path.split("\\.")).getBoolean();
    }

    // Get double config value
    public synchronized double getDoubleValue(String path) {
        return rootNode.node((Object[]) path.split("\\.")).getDouble();
    }

    public synchronized int getIntegerValue(String path) {
        return rootNode.node((Object[]) path.split("\\.")).getInt();
    }

    // Get string config value
    public synchronized String getStringValue(String path) {
        return rootNode.node((Object[]) path.split("\\.")).getString();
    }

    // Set a config value, ensuring parent nodes exist
    public synchronized void setConfigValue(String Key, Object value) {
        try {
            ConfigurationNode parentNode = rootNode.node((Object[]) Key.split("\\."));

            // Set the value and comment
            parentNode.set(value);
        } catch (Exception e) {
            System.out.println("Failed to add config: " + Key + " with value: " + value);
        }
        saveConfig(); // Save changes to the config file
    }
}


/*
  // Asynchronous methods

  // Create or load the config file asynchronously
  public CompletableFuture<Void> createConfigAsync(String path) {
    return CompletableFuture.runAsync(
            () -> {
              File configFile = new File(plugin.getDataFolder(), path);

              // Ensure the parent directories for the file exist
              File parentDir = configFile.getParentFile();
              if (!parentDir.exists()) {
                parentDir.mkdirs(); // Create all necessary parent directories
              }

              options =
                  JacksonConfigurationLoader.builder()
                      .headerMode(HeaderMode.PRESET)
                      .defaultOptions();

              loader =
                  JacksonConfigurationLoader.builder()
                      .path(configFile.toPath())
                      .indent(2)
                      .fieldValueSeparatorStyle(FieldValueSeparatorStyle.SPACE_BOTH_SIDES)
                      .build();

              if (!configFile.exists()) {
                try {
                  configFile.createNewFile();
                  try (FileWriter writer = new FileWriter(configFile)) {
                    writer.write("{}"); // Start with an empty JSON object
                  }
                  System.out.println("Created new json file: " + path);
                } catch (IOException e) {
                  System.out.println("Failed to create new json file: " + e.getMessage());
                }
              }
            })
        .thenCompose(
            v -> loadConfigValuesAsync()); // Chain loading of the config after file creation
  }

  // Load configuration values asynchronously
  public CompletableFuture<Void> loadConfigValuesAsync() {
    return CompletableFuture.runAsync(
        () -> {
          try {
            rootNode = loader.load(options); // Load the config directly
            System.out.println("Config loaded successfully.");
          } catch (Exception e) {
            System.out.println("Failed to load configurations: " + e);
          }
        });
  }

  // Save the configuration asynchronously
  public CompletableFuture<Void> saveConfigAsync() {
    return CompletableFuture.runAsync(
        () -> {
          try {
            // Save your configuration
            loader.save(rootNode);
          } catch (ConfigurateException e) {
            System.out.println("Could not save config asynchronously: " + e.getMessage());
            if (e.getCause() instanceof AccessDeniedException) {
              plugin
                  .getLogger()
                  .severe("Access denied while saving config. Check file permissions.");
            }
          }
        });
  }

  // Set a config value asynchronously, ensuring parent nodes exist
  public CompletableFuture<Void> setConfigValueAsync(String key, Object value) {
    return CompletableFuture.runAsync(
            () -> {
              try {
                ConfigurationNode parentNode = rootNode.node((Object[]) key.split("\\."));
                parentNode.set(value); // Set the value
              } catch (Exception e) {
                plugin
                    .getLogger()
                    .warning("Failed to add config: " + key + " with value: " + value);
                e.printStackTrace();
              }
            })
        .thenCompose(v -> saveConfigAsync()); // Chain saving of the config after setting the value
  }

  // Get a generic config value asynchronously
  public CompletableFuture<Object> getConfigValueAsync(String path) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            return rootNode.node((Object[]) path.split("\\.")).get(Object.class);
          } catch (Exception e) {
            System.out.println("Failed to get config value at path: " + path);
            e.printStackTrace();
            return null;
          }
        });
  }

  // Get boolean config value asynchronously
  public CompletableFuture<Boolean> getBooleanValueAsync(String path) {
    return CompletableFuture.supplyAsync(
        () -> rootNode.node((Object[]) path.split("\\.")).getBoolean());
  }

  // Get double config value asynchronously
  public CompletableFuture<Double> getDoubleValueAsync(String path) {
    return CompletableFuture.supplyAsync(
        () -> rootNode.node((Object[]) path.split("\\.")).getDouble());
  }

  // Get integer config value asynchronously
  public CompletableFuture<Integer> getIntegerValueAsync(String path) {
    return CompletableFuture.supplyAsync(
        () -> rootNode.node((Object[]) path.split("\\.")).getInt());
  }

  // Get string config value asynchronously
  public CompletableFuture<String> getStringValueAsync(String path) {
    return CompletableFuture.supplyAsync(
        () -> rootNode.node((Object[]) path.split("\\.")).getString());
  }
}

 */
