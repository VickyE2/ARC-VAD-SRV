package com.arcvad.schoolquest.server.server.DataFormat.JSON;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.Family;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.MaterialRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.PlayerFamily;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.*;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomClothes;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoe;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoes;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopClothes;
import com.arcvad.schoolquest.server.server.GlobalUtils.EnumRandomizer;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import com.arcvad.schoolquest.server.server.Playerutils.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

//TODO: Fix manager merging of materials and families as it still makes duplicates.
//TODO: Add command to add materials to materials registrar and to create users through console.
//TODO: Fully complete server data converter...

public class JsonConfigManager {
    private final ObjectMapper objectMapper;
    private final Lock fileLock = new ReentrantLock(); // Lock to synchronize file access

    public JsonConfigManager() {
        this.objectMapper = new ObjectMapper();
    }

    // Save data asynchronously to JSON
    public void saveToJSONAsync(Object object, String filePath) {
        new Thread(() -> {
            try {
                saveToJSON(object, filePath);
            } catch (IOException e) {
                logger.error("ARC-JSON","Failed to save object to JSON" + e);
            }
        }).start();
    }

    // Save data to JSON with thread-safe file access
    private void saveToJSON(Object object, String filePath) throws IOException {
        fileLock.lock();  // Ensure that only one thread is writing to the file at a time
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            parentDir.mkdirs();

            if (file.exists()) {
                try {
                    // Load existing object
                    Object existingObject = loadFromJSON(filePath, object.getClass());

                    // Check if the object implements Mergeable and merge if so
                    if (existingObject instanceof Mergeable) {
                        ((Mergeable) existingObject).mergeWith(object);
                        logger.info("ARC-JSON", StringTemplate.STR."green[Merged object into existing data for file: \{filePath}]");
                        object = existingObject;  // Update reference to save the merged version
                    }
                } catch (IOException e) {
                    logger.error("ARC-JSON", StringTemplate.STR."Failed to load existing object for merging. Saving new object as is.\{e}");
                }
            }

            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);  // Disable empty beans check
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, object);
            logger.info("ARC-JSON","Object saved to JSON at: " + filePath);
        } finally {
            fileLock.unlock();  // Unlock the file after the operation is complete
        }
    }

    // Load JSON into an object with thread-safe file access
    public <T> T loadFromJSON(String filePath, Class<T> clazz) throws IOException {
        fileLock.lock();  // Ensure that only one thread is reading from the file at a time
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            return objectMapper.readValue(file, clazz);
        } finally {
            fileLock.unlock();  // Unlock the file after the operation is complete
        }
    }

    // Asynchronous user creation
    public void createUserAsync(String username, String password, String email,
                                String firstname, String lastname, Genders gender,
                                Consumer<Boolean> callback) {
        new Thread(() -> {
            boolean success = false;
            try {
                success = createUser(username, password, email, firstname, lastname, gender);
            } catch (Exception e) {
                logger.severe("ARC-JSON","Error creating user asynchronously: " + e.getMessage());
            }
            callback.accept(success);  // Notify callback with the result (true or false)
        }).start();
    }

    // Synchronous user creation
    public boolean createUser(String username, String password, String email,
                              String firstname, String lastname, Genders gender) {
        try {
            PlayerRegistrar registrar = getOrCreateRegistrar();

            if (isDefaultUser(username)) {
                // Check if the default user has already been created
                if (registrar.isCreatedDefault()) {
                    return false;  // Default user already exists, so skip creation
                }

                logger.info("ARC-JSON","Creating default user...");
                User defaultUser = createUserObject(username, password, email, firstname, lastname, gender);
                registrar.getUsers().add(defaultUser);
                registrar.setCreatedDefault(true);

                Player defaultPlayer = createPlayerObject(gender, defaultUser);
                saveToJSON(registrar, "./ServerData/JSON/registeredUsers.json");
                saveToJSON(defaultPlayer, "./ServerData/JSON/Users/" + username + ".json");

                return true;
            }

            // For non-default users
            User newUser = createUserObject(username, password, email, firstname, lastname, gender);
            registrar.getUsers().add(newUser);

            Player newPlayer = createPlayerObject(gender, newUser);
            saveToJSON(registrar, "./ServerData/JSON/registeredUsers.json");
            saveToJSON(newPlayer, "./ServerData/JSON/Users/" + username + ".json");

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to get or create a user registrar from a JSON file
    private PlayerRegistrar getOrCreateRegistrar() throws IOException {
        File registrarFile = new File("./ServerData/JSON/registeredUsers.json");
        if (registrarFile.exists()) {
            logger.info("ARC-JSON","Using provided registrar found at default location");
            return loadFromJSON(registrarFile.getPath(), PlayerRegistrar.class);
        }
        logger.info("ARC-JSON","No registrar found. Creating new registrar.");
        PlayerRegistrar newRegistrar = new PlayerRegistrar();
        newRegistrar.setUsers(new ArrayList<>());
        return newRegistrar;
    }

    private FamilyRegistrar getOrCreateFamilyRegistrar() throws IOException {
        File registrarFile = new File("./Families/JSON/registeredFamilies.xml");
        if (registrarFile.exists()) {
            logger.info("ARC-JSON", "Using provided family registrar found at def location");
            return loadFromJSON(registrarFile.getPath(), FamilyRegistrar.class);
        }
        logger.info("ARC-JSON", "No registrar found. Creating new registrar");
        FamilyRegistrar newRegistrar = new FamilyRegistrar();
        newRegistrar.setFamilies(new ArrayList<>());
        return newRegistrar;
    }

    // Method to check if the username is a default user
    private boolean isDefaultUser(String username) {
        return "test1".equals(username);
    }

    // Method to create a new User object
    private User createUserObject(String username, String password, String email,
                                  String firstname, String lastname, Genders gender) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setLastname(lastname);
        user.setFirstname(firstname);
        user.setGender(gender);
        return user;
    }

    // Method to create a new Player object
    private Player createPlayerObject(Genders gender, User user) throws IOException {
        Player player = new Player();

        // Create the main clothing items
        TopClothes topCloth = createTopClothes(gender, "def");
        BottomClothes bottomCloth = createBottomClothes(gender, "def");
        Shoes shoes = createShoes("def");

        // Assign default clothing to the player
        player.setUpperWear(topCloth);
        player.setLowerWear(bottomCloth);
        player.setFootwear(shoes);

        // Populate the collections with the shared instances
        player.setCollectedUpperWear(new ArrayList<>());
        player.setCollectedLowerWear(new ArrayList<>());
        player.setCollectedFootwear(new ArrayList<>());

        player.getCollectedUpperWear().add(createTopCloth(gender, "def"));
        player.getCollectedUpperWear().add(createTopCloth(gender, "alt"));

        player.getCollectedLowerWear().add(createBottomCloth(gender, "def"));
        player.getCollectedLowerWear().add(createBottomCloth(gender, "alt"));

        player.getCollectedFootwear().add(createShoe("def"));
        player.getCollectedFootwear().add(createShoe("alt"));

        player.setIrisColor(new Color(0, 0, 0, 100));
        player.setHairShade(new Color(0, 0, 0, 100));
        player.setEyeLashHue(new Color(0, 0, 0, 100));
        player.setComplexion(new Color(0, 0, 0, 100));
        player.setEyeLashDesign(Styles.EyelashStyles.DEFAULT);
        player.setHairDesign(Styles.HairStyles.DEFAULT);
        player.setAdornments(new ArrayList<>());
        player.setCollectedAdornments(new ArrayList<>());

        MaterialRegistrar materialRegistrar = new MaterialRegistrar();

        materialRegistrar.setAccessoryList(new ArrayList<>());
        materialRegistrar.setBottomClothList(new ArrayList<>());
        materialRegistrar.setShoesList(new ArrayList<>());
        materialRegistrar.setTopClothList(new ArrayList<>());

        materialRegistrar.getTopClothList().add(createTopCloth(gender, "def"));
        materialRegistrar.getTopClothList().add(createTopCloth(gender, "alt"));

        materialRegistrar.getBottomClothList().add(createBottomCloth(gender, "def"));
        materialRegistrar.getBottomClothList().add(createBottomCloth(gender, "alt"));

        materialRegistrar.getShoesList().add(createShoes("def"));
        materialRegistrar.getShoesList().add(createShoes("alt"));

        FamilyNames familyName = EnumRandomizer.getRandomEnum(FamilyNames.class);

        FamilyRegistrar familyRegistrar = getOrCreateFamilyRegistrar();
        Family family = getFamily(familyRegistrar, familyName);

        MinimalUser mUser = new MinimalUser(user);
        family.getFamilyMembers().add(mUser);

        familyRegistrar.getFamilies().add(family);

        PlayerFamily playerFamily = new PlayerFamily(family);
        playerFamily.setFamilyPosition(family.getFamilyMembers().size());
        player.setFamily(playerFamily);

        JsonConfigManager manager = new JsonConfigManager();
        manager.saveToJSON(familyRegistrar, "./ServerData/JSON/registeredFamilies.json");
        manager.saveToJSON(materialRegistrar, "./ServerData/JSON/registeredMaterials.json");

        return player;
    }

    private static Family getFamily(FamilyRegistrar familyRegistrar, FamilyNames familyName) {
        List<Family> families = familyRegistrar.getFamilies();
        Family family = null;
        Wealth familyWealth = Wealth.getRandomWealthByWeight();

        boolean isContainedFamily = false;
        for (Family family2 : families){
            if (family2.getFamilyNames().equals(familyName)){
                isContainedFamily = true;
                family = family2;
                break;
            }
        }
        if (!isContainedFamily){
            family = new Family(familyName);
            family.setFamilyMembers(new ArrayList<>());
            family.setFamilySize();
            family.setFamilyWealth(familyWealth);
        }
        return family;
    }

    // Helper methods to create clothing and other player items
    private TopClothes createTopClothes(Genders gender, String variant) {
        String g = (gender == Genders.MALE) ? "m" : "f";
        Material material = variant.equals("def") ? Material.POLYESTER : Material.WOOL;
        Rarity rarity = Rarity.COMMON;
        String key = variant.equals("def") ? "t_def_" + g : "t_alt_" + g;
        return new TopClothes(new TopCloth(rarity, material, key));
    }

    private BottomClothes createBottomClothes(Genders gender, String variant) {
        String g = (gender == Genders.MALE) ? "m" : "f";
        Material material = variant.equals("def") ? Material.CHINOS : Material.JEANS;
        Rarity rarity = Rarity.COMMON;
        String key = variant.equals("def") ? "c_def_" + g : "c_alt_" + g;
        return new BottomClothes(new BottomCloth(rarity, material, key));
    }

    private Shoes createShoes(String variant) {
        Material material = variant.equals("def") ? Material.LEATHER : Material.CANVAS;
        Rarity rarity = Rarity.COMMON;
        String key = variant.equals("def") ? "s_def_u" : "s_alt_u";
        return new Shoes(new Shoe(rarity, material, key));
    }

    private TopCloth createTopCloth(Genders gender, String variant) {
        String g;
        if (gender == Genders.MALE){
            g = "m";
        }else{
            g = "f";
        }

        Material material = variant.equals("def") ? Material.POLYESTER : Material.WOOL;
        Rarity rarity = Rarity.COMMON;
        String key = variant.equals("def") ? "s_def_"+g : "s_alt_"+g;

        return new TopCloth(rarity, material, key);
    }
    private BottomCloth createBottomCloth(Genders gender, String variant) {
        String g;
        if (gender == Genders.MALE){
            g = "m";
        }else{
            g = "f";
        }

        Material material = variant.equals("def") ? Material.CHINOS : Material.JEANS;
        Rarity rarity = Rarity.COMMON;
        String key = variant.equals("def") ? "s_def_"+g : "s_alt_"+g;

        return new BottomCloth(rarity, material, key);
    }
    private Shoe createShoe(String variant) {
        Material material = variant.equals("def") ? Material.LEATHER : Material.CANVAS;
        Rarity rarity = Rarity.COMMON;
        String key = variant.equals("def") ? "s_def_u" : "s_alt_u";

        return new Shoe(rarity, material, key);
    }
}
