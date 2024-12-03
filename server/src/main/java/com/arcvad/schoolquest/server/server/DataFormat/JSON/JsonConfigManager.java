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
import com.arcvad.schoolquest.server.server.GlobalUtils.Entiies.TransactionUser;
import com.arcvad.schoolquest.server.server.GlobalUtils.EnumRandomizer;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import com.arcvad.schoolquest.server.server.Playerutils.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
    public boolean createUser(TransactionUser user) {
        try {
            PlayerRegistrar registrar = getOrCreateRegistrar();

            String username = user.getUsername();
            String password= user.getPassword();
            String firstname = user.getFirstname();
            String lastname = user.getLastname();
            String email = user.getEmail();
            Genders gender = user.getGender();

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

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }
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

        } catch (IOException | InterruptedException | ExecutionException e) {
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
        File registrarFile = new File("./ServerData/JSON/registeredFamilies.json");
        if (registrarFile.exists()) {
            logger.info("ARC-JSON", "Using provided family registrar found at def location");
            return loadFromJSON(registrarFile.getPath(), FamilyRegistrar.class);
        }
        logger.info("ARC-JSON", "No registrar found. Creating new registrar");
        FamilyRegistrar newRegistrar = new FamilyRegistrar();
        newRegistrar.setFamilies(new ArrayList<>());
        return newRegistrar;
    }
    private MaterialRegistrar getOrCreateMaterialRegistrar() throws IOException {
        File registrarFile = new File("./ServerData/JSON/registeredMaterials.json");
        if (registrarFile.exists()) {
            logger.info("ARC-JSON", "Using provided material registrar found at def location");
            return loadFromJSON(registrarFile.getPath(), MaterialRegistrar.class);
        }
        logger.info("ARC-JSON", "No registrar found. Creating new registrar");
        MaterialRegistrar newRegistrar = new MaterialRegistrar();
        newRegistrar.setAccessoryList(new ArrayList<>());
        newRegistrar.setBottomClothList(new ArrayList<>());
        newRegistrar.setShoesList(new ArrayList<>());
        newRegistrar.setTopClothList(new ArrayList<>());

        newRegistrar.getTopClothList().add(createTopCloth(Genders.MALE, "def"));
        newRegistrar.getTopClothList().add(createTopCloth(Genders.FEMALE, "def"));
        newRegistrar.getTopClothList().add(createTopCloth(Genders.MALE, "alt"));
        newRegistrar.getTopClothList().add(createTopCloth(Genders.FEMALE, "alt"));


        newRegistrar.getBottomClothList().add(createBottomCloth(Genders.MALE, "def"));
        newRegistrar.getBottomClothList().add(createBottomCloth(Genders.FEMALE, "def"));
        newRegistrar.getBottomClothList().add(createBottomCloth(Genders.MALE, "alt"));
        newRegistrar.getBottomClothList().add(createBottomCloth(Genders.FEMALE, "alt"));

        newRegistrar.getShoesList().add(createShoe("def"));
        newRegistrar.getShoesList().add(createShoe("alt"));

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
    private Player createPlayerObject(Genders gender, User user) throws IOException, InterruptedException, ExecutionException {
        Player player = new Player();

        MaterialRegistrar materialRegistrar = getOrCreateMaterialRegistrar();

        // Assign default clothing to the player
        player.setUpperWear(new TopClothes(
            materialRegistrar.getTopClothList().stream()
                .filter(topCloth -> topCloth.getKey().equals(STR."t_def_\{gender.equals(Genders.MALE) ? "m" : "f"}"))
                .findFirst()
                .get()
            )
        );
        player.setLowerWear(new BottomClothes(
                materialRegistrar.getBottomClothList().stream()
                    .filter(bottomCloth -> bottomCloth.getKey().equals(STR."c_def_\{gender.equals(Genders.MALE) ? "m" : "f"}"))
                    .findFirst()
                    .get()
            )
        );
        player.setFootwear(new Shoes(
                materialRegistrar.getShoesList().stream()
                    .filter(shoe -> shoe.getKey().equals("s_def_u"))
                    .findFirst()
                    .get()
            )
        );

        // Populate the collections with the shared instances
        player.setCollectedUpperWear(new ArrayList<>());
        player.setCollectedLowerWear(new ArrayList<>());
        player.setCollectedFootwear(new ArrayList<>());

        player.getCollectedUpperWear().add(
            materialRegistrar.getTopClothList().stream()
                .filter(topCloth -> topCloth.getKey().equals(STR."t_def_\{gender.equals(Genders.MALE) ? "m" : "f"}"))
                .findFirst()
                .get()
        );
        player.getCollectedUpperWear().add(
            materialRegistrar.getTopClothList().stream()
                .filter(topCloth -> topCloth.getKey().equals(STR."t_alt_\{gender.equals(Genders.MALE) ? "m" : "f"}"))
                .findFirst()
                .get()
        );

        player.getCollectedLowerWear().add(
            materialRegistrar.getBottomClothList().stream()
                .filter(bottomCloth -> bottomCloth.getKey().equals(STR."c_def_\{gender.equals(Genders.MALE) ? "m" : "f"}"))
                .findFirst()
                .get()
        );
        player.getCollectedLowerWear().add(
            materialRegistrar.getBottomClothList().stream()
                .filter(bottomCloth -> bottomCloth.getKey().equals(STR."c_alt_\{gender.equals(Genders.MALE) ? "m" : "f"}"))
                .findFirst()
                .get()
        );

        player.getCollectedFootwear().add(
            materialRegistrar.getShoesList().stream()
                .filter(shoe -> shoe.getKey().equals("s_def_u"))
                .findFirst()
                .get()
        );
        player.getCollectedFootwear().add(
            materialRegistrar.getShoesList().stream()
                .filter(shoe -> shoe.getKey().equals("s_alt_u"))
                .findFirst()
                .get()
        );

        // Set other attributes as needed
        player.setIrisColor(new Color(0, 0, 0, 100));
        player.setHairShade(new Color(0, 0, 0, 100));
        player.setEyeLashHue(new Color(0, 0, 0, 100));
        player.setComplexion(new Color(0, 0, 0, 100));
        player.setEyeLashDesign(Styles.EyelashStyles.DEFAULT);
        player.setHairDesign(Styles.HairStyles.DEFAULT);
        player.setAdornments(new ArrayList<>());
        player.setCollectedAdornments(new ArrayList<>());

        FamilyNames familyName = EnumRandomizer.getRandomEnum(FamilyNames.class);

        FamilyRegistrar familyRegistrar = getOrCreateFamilyRegistrar();
        Family family = getFamily(familyRegistrar, familyName);

        MinimalUser mUser = new MinimalUser(user);
        family.addFamilyMember(mUser);

        PlayerFamily playerFamily = new PlayerFamily(family);
        playerFamily.setFamilyPosition(family.getFamilyMembers().size());
        player.setFamily(playerFamily);

        saveToJSON(familyRegistrar, "./ServerData/JSON/registeredFamilies.json");
        saveToJSON(materialRegistrar, "./ServerData/JSON/registeredMaterials.json");

        return player;
    }

    private static Family getFamily(FamilyRegistrar familyRegistrar, FamilyNames familyName) {
        List<Family> families = familyRegistrar.getFamilies();

        return families.stream()
                .filter(family -> family.getFamilyName().equalsIgnoreCase(familyName.toString()))
                .findAny()
                .orElseGet(() ->
                    {
                        Family newFamily =
                            new Family(
                                familyName
                            );
                        newFamily.setFamilySize();
                        newFamily.setFamilyWealth(Wealth.getRandomWealthByWeight());
                        newFamily.setFamilyMembers(new ArrayList<>());

                        familyRegistrar.getFamilies().add(newFamily);
                        return newFamily;
                    }
                );
    }
    public static CompletableFuture<Family> getFamilyAsync(FamilyRegistrar familyRegistrar, FamilyNames familyName) {
        return CompletableFuture.supplyAsync(() -> getFamily(familyRegistrar, familyName))
            .thenApplyAsync(family -> {
                boolean isSpecial = familyName.equals(FamilyNames.POPOOLA) ||
                    familyName.equals(FamilyNames.OSITUNGA) ||
                    familyName.equals(FamilyNames.ADEDO) ||
                    familyName.equals(FamilyNames.ADEJOH);

                if (isSpecial) {
                    // Make sure these values are set before proceeding
                    family.setFamilyWealth(Wealth.GOD_GIVEN_WEALTH);
                    family.setFamilySize(100);
                }

                return family;
            });
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
