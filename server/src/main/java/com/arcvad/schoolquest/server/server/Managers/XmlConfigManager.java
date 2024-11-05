package com.arcvad.schoolquest.server.server.Managers;

import org.spongepowered.configurate.AttributedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.AtomicFiles;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.xml.XmlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class XmlConfigManager {

    public XmlConfigurationLoader loader;
    public AttributedConfigurationNode rootNode;
    public ConfigurationOptions options;

    public XmlConfigManager() {
    }

    // Create or load the config file
    public void createConfig(String path) {
        File configFile = new File(path);

        // Ensure the parent directories for the file exist
        File parentDir = configFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs(); // Create all necessary parent directories
        }

        options =
            XmlConfigurationLoader.builder().headerMode(HeaderMode.PRESET).indent(4).defaultOptions();

        loader =
            XmlConfigurationLoader.builder()
                .writesExplicitType(false)
                .path(configFile.toPath())
                .indent(4)
                .sink(AtomicFiles.atomicWriterFactory(configFile.toPath(), UTF_8))
                .build();

        if (!configFile.exists()) {
            try {
                // Create the file if it does not exist
                configFile.createNewFile();
                Files.write(
                    configFile.toPath(),
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><config></config>"
                        .getBytes(StandardCharsets.UTF_8));
                System.out.println("Created new xml file: " + path);
                loadConfigValues();
            } catch (IOException e) {
                System.out.println("Failed to create new xml file: " + e.getMessage());
            }
        } else {
            loadConfigValues();
        }
    }

    public void createConfig(String path, String defualtKey) {
        File configFile = new File(path);

        // Ensure the parent directories for the file exist
        File parentDir = configFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs(); // Create all necessary parent directories
        }

        options =
            XmlConfigurationLoader.builder()
                .headerMode(HeaderMode.PRESET)
                .indent(4)
                .defaultTagName(defualtKey)
                .defaultOptions();

        loader =
            XmlConfigurationLoader.builder()
                .defaultTagName(defualtKey)
                .writesExplicitType(false)
                .path(configFile.toPath())
                .indent(4)
                .sink(AtomicFiles.atomicWriterFactory(configFile.toPath(), UTF_8))
                .build();

        if (!configFile.exists()) {
            try {
                // Create the file if it does not exist
                configFile.createNewFile();
                Files.write(
                    configFile.toPath(),
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes(StandardCharsets.UTF_8));
                System.out.println("Created new xml file: " + path);
                loadConfigValues();
            } catch (IOException e) {
                System.out.println("Failed to create new xml file: " + e.getMessage());
            }
        } else {
            loadConfigValues();
        }
    }

    public void loadConfig(String path, String file) {
        File configFile = new File(path, file);

        options = XmlConfigurationLoader.builder().defaultOptions();

        loader = XmlConfigurationLoader.builder().path(configFile.toPath()).build();

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

    public List<AttributedConfigurationNode> getChildNodes(String path) {
        try {
            AttributedConfigurationNode node = rootNode.node((Object[]) path.split("\\."));
            return node.childrenList();
        } catch (Exception e) {
            System.out.println("Failed to get child nodes at path: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public boolean isListNode(String path) {
        try {
            AttributedConfigurationNode node = rootNode.node((Object[]) path.split("\\."));
            return node.isList();
        } catch (Exception e) {
            System.out.println("Failed to check if node is list at path: " + path);
            e.printStackTrace();
            return false;
        }
    }

    public boolean isMapNode(String path) {
        try {
            AttributedConfigurationNode node = rootNode.node((Object[]) path.split("\\."));
            return node.isMap();
        } catch (Exception e) {
            System.out.println("Failed to check if node is map at path: " + path);
            e.printStackTrace();
            return false;
        }
    }

    public String getTagName(String path) {
        try {
            AttributedConfigurationNode node = rootNode.node((Object[]) path.split("\\."));
            return node.tagName();
        } catch (Exception e) {
            System.out.println("Failed to get tag name at path: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, String> getAttributes(String path) {
        try {
            AttributedConfigurationNode node = rootNode.node((Object[]) path.split("\\."));
            if (node != null) {
                return node.attributes();
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Failed to get attributes at path: " + path);
            e.printStackTrace();
            return null;
        }
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
    public void setConfigValue(
        String Key, Object value, String comment, Map<String, String> attributes) {
        try {
            // Split the parentKey and childKey to handle nested structures
            AttributedConfigurationNode node = rootNode.node((Object[]) Key.split("\\."));
            if (attributes != null) {
                node.attributes(attributes);
            }
            if (comment != null) {
                node.comment(comment);
            }
            node.set(value);
        } catch (Exception e) {
            System.out.println("Failed to add config: " + Key + " with value: " + value);
        }
        saveConfig(); // Save changes to the config file
    }

    public void createDefaultUser(String username, String password) {
        createConfig("Clients/Users/registered_users.xml");
        if (username.equals("test1")) {
            if (!getBooleanValue("created_default")) {

                // Register
                setConfigValue("created_default", true, "has created default", null);
                Map<String, String> defaultAttributes = getDefaultRegisteredPlayer(username, password);
                setConfigValue("users.user", "", "Default user...", defaultAttributes);

                // Data register
                createConfig("Clients/Users/" + username + "/data.xml");

                setConfigValue("playerData.eyeLashStyle", "style_1", "", null);
                setConfigValue("playerData.eyeLashColor", "black", "", null);
                setConfigValue("playerData.hairStyle", "style_1", "", null);
                setConfigValue("playerData.hairColor", "black", "", null);
                setConfigValue("playerData.eyeColor", "black", "", null);
                setConfigValue("playerData.skinColor", "dark_brown", "", null);
                setConfigValue("playerData.topCloth", "c_def_1", "Default Top", null);
                setConfigValue("playerData.bottomCloth", "t_def_1", "Default Short", null);
                setConfigValue("playerData.shoe", "s_def_1", "Default Shoe", null);

                Map<String, String> defaultAccessories = new HashMap<>();
                defaultAccessories.put("key", "accessory_0");

                int i = 0;
                for (Map.Entry<String, String> entry : defaultAccessories.entrySet()) {
                    int index = i;

                    Map<String, String> singleEntryMap = new HashMap<>();
                    singleEntryMap.put(entry.getKey(), entry.getValue());

                    setConfigValue("playerData.accessories.accessory", "default_accessory_" + index, "", singleEntryMap);

                    i++;
                }


                //Top cloth
                List<Map<String, String>> defaultTopCloths = new ArrayList<>();

                // First topCloth
                Map<String, String> cloth1 = new HashMap<>();
                cloth1.put("rarity", "common");
                cloth1.put("id", "c_def_1");
                defaultTopCloths.add(cloth1);

                // Second topCloth
                Map<String, String> cloth2 = new HashMap<>();
                cloth2.put("rarity", "common");
                cloth2.put("id", "c_alt_1");
                defaultTopCloths.add(cloth2);

                int index = 0;
                for (Map<String, String> topClothAttributes : defaultTopCloths) {
                    // Set each attribute map as a single entry
                    setConfigValue("playerData.ownedItems.topClothes.topCloth", "", "Default Owned Top", topClothAttributes);
                    index++;
                }


                List<Map<String, String>> defaultBottomCloths = new ArrayList<>();

                // First topCloth
                Map<String, String> bottom1 = new HashMap<>();
                bottom1.put("rarity", "common");
                bottom1.put("id", "t_def_1");
                defaultBottomCloths.add(bottom1);

                // Second topCloth
                Map<String, String> bottom2 = new HashMap<>();
                bottom2.put("rarity", "common");
                bottom2.put("id", "t_alt_1");
                defaultBottomCloths.add(bottom2);

                index = 0;
                for (Map<String, String> topClothAttributes : defaultBottomCloths) {
                    // Set each attribute map as a single entry
                    setConfigValue("playerData.ownedItems.topClothes.topCloth", "", "Default Owned Top", topClothAttributes);
                    index++;
                }


                List<Map<String, String>> defaultShoes = new ArrayList<>();

                // First topCloth
                Map<String, String> shoe1 = new HashMap<>();
                shoe1.put("rarity", "common");
                shoe1.put("id", "s_def_1");
                defaultShoes.add(shoe1);

                // Second topCloth
                Map<String, String> shoe2 = new HashMap<>();
                shoe2.put("rarity", "common");
                shoe2.put("id", "s_alt_1");
                defaultShoes.add(shoe2);

                index = 0;
                for (Map<String, String> topClothAttributes : defaultShoes) {
                    // Set each attribute map as a single entry
                    setConfigValue("playerData.ownedItems.shoes.shoe", "", "Default Owned Shoe", topClothAttributes);
                    index++;
                }


                List<Map<String, String>> defaultAccesories = new ArrayList<>();

                // First topCloth
                Map<String, String> bangle = new HashMap<>();
                bangle.put("rarity", "common");
                bangle.put("id", "bangle");
                defaultAccesories.add(bangle);

                index = 0;
                for (Map<String, String> topClothAttributes : defaultAccesories) {
                    // Set each attribute map as a single entry
                    setConfigValue("playerData.ownedItems.accessories.accessory", "", "Default Owned Accessory", topClothAttributes);
                    index++;
                }
            }
        }else{
            // Register
            setConfigValue("created_default", true, "has created default", null);
            Map<String, String> defaultAttributes = getDefaultRegisteredPlayer(username, password);
            setConfigValue("users.user", "", "Default user...", defaultAttributes);

            // Data register
            createConfig("Clients/Users/" + username + "/data.xml");

            setConfigValue("playerData.eyeLashStyle", "style_1", "", null);
            setConfigValue("playerData.eyeLashColor", "black", "", null);
            setConfigValue("playerData.hairStyle", "style_1", "", null);
            setConfigValue("playerData.hairColor", "black", "", null);
            setConfigValue("playerData.eyeColor", "black", "", null);
            setConfigValue("playerData.skinColor", "dark_brown", "", null);
            setConfigValue("playerData.topCloth", "c_def_1", "Default Top", null);
            setConfigValue("playerData.bottomCloth", "t_def_1", "Default Short", null);
            setConfigValue("playerData.shoe", "s_def_1", "Default Shoe", null);

            Map<String, String> defaultAccessories = new HashMap<>();
            defaultAccessories.put("key", "accessory_0");

            int i = 0;
            for (Map.Entry<String, String> entry : defaultAccessories.entrySet()) {
                int index = i;

                Map<String, String> singleEntryMap = new HashMap<>();
                singleEntryMap.put(entry.getKey(), entry.getValue());

                setConfigValue("playerData.accessories.accessory", "default_accessory_" + index, "", singleEntryMap);

                i++;
            }


            //Top cloth
            List<Map<String, String>> defaultTopCloths = new ArrayList<>();

            // First topCloth
            Map<String, String> cloth1 = new HashMap<>();
            cloth1.put("rarity", "common");
            cloth1.put("id", "c_def_1");
            defaultTopCloths.add(cloth1);

            // Second topCloth
            Map<String, String> cloth2 = new HashMap<>();
            cloth2.put("rarity", "common");
            cloth2.put("id", "c_alt_1");
            defaultTopCloths.add(cloth2);

            int index = 0;
            for (Map<String, String> topClothAttributes : defaultTopCloths) {
                // Set each attribute map as a single entry
                setConfigValue("playerData.ownedItems.topClothes.topCloth", "", "Default Owned Top", topClothAttributes);
                index++;
            }


            List<Map<String, String>> defaultBottomCloths = new ArrayList<>();

            // First topCloth
            Map<String, String> bottom1 = new HashMap<>();
            bottom1.put("rarity", "common");
            bottom1.put("id", "t_def_1");
            defaultBottomCloths.add(bottom1);

            // Second topCloth
            Map<String, String> bottom2 = new HashMap<>();
            bottom2.put("rarity", "common");
            bottom2.put("id", "t_alt_1");
            defaultBottomCloths.add(bottom2);

            index = 0;
            for (Map<String, String> topClothAttributes : defaultBottomCloths) {
                // Set each attribute map as a single entry
                setConfigValue("playerData.ownedItems.topClothes.topCloth", "", "Default Owned Top", topClothAttributes);
                index++;
            }


            List<Map<String, String>> defaultShoes = new ArrayList<>();

            // First topCloth
            Map<String, String> shoe1 = new HashMap<>();
            shoe1.put("rarity", "common");
            shoe1.put("id", "s_def_1");
            defaultShoes.add(shoe1);

            // Second topCloth
            Map<String, String> shoe2 = new HashMap<>();
            shoe2.put("rarity", "common");
            shoe2.put("id", "s_alt_1");
            defaultShoes.add(shoe2);

            index = 0;
            for (Map<String, String> topClothAttributes : defaultShoes) {
                // Set each attribute map as a single entry
                setConfigValue("playerData.ownedItems.shoes.shoe", "", "Default Owned Shoe", topClothAttributes);
                index++;
            }


            List<Map<String, String>> defaultAccesories = new ArrayList<>();

            // First topCloth
            Map<String, String> bangle = new HashMap<>();
            bangle.put("rarity", "common");
            bangle.put("id", "bangle");
            defaultAccesories.add(bangle);

            index = 0;
            for (Map<String, String> topClothAttributes : defaultAccesories) {
                // Set each attribute map as a single entry
                setConfigValue("playerData.ownedItems.accessories.accessory", "", "Default Owned Accessory", topClothAttributes);
                index++;
            }
        }
    }

    private static Map<String, String> getDefaultRegisteredPlayer(String username, String password) {
        // "onlyifyouknewwhatitwas009!&"

        Map<String, String> defaultAttributes = new HashMap<>();
        defaultAttributes.put("username", username);
        defaultAttributes.put("password", password);
        defaultAttributes.put("email", "test_001@waitamin.com");
        defaultAttributes.put("firstname", "Robot");
        defaultAttributes.put("lastname", "Logic");
        return defaultAttributes;
    }
}
