package com.arcvad.schoolquest.server.server.DataFormat.XML;

import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.Family;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.MaterialRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.PlayerFamily;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.*;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomClothes;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoes;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopClothes;
import com.arcvad.schoolquest.server.server.DataFormat.XML.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.GlobalUtils.Entiies.TransactionUser;
import com.arcvad.schoolquest.server.server.GlobalUtils.EnumRandomizer;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import com.arcvad.schoolquest.server.server.Playerutils.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

@SuppressWarnings("preview")
public class XmlConfigManager {
    private final JAXBContext jaxbContext;
    private final Lock fileLock = new ReentrantLock(); // Lock to synchronize file access

    @SafeVarargs
    public XmlConfigManager(Class<? extends BaseTemplate>... classesToBeBound) throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance(classesToBeBound);
    }

    // Save data asynchronously and handle merging if the file already exists
    public void saveToXMLAsync(Object object, String filePath) {
        new Thread(() -> {
            try {
                saveToXML(object, filePath);
            } catch (JAXBException e) {
                logger.error("ARC-XML", StringTemplate.STR."Failed to save object to XML\{e}");
            }
        }).start();
    }

    // Save data to XML with thread-safe file access
    private void saveToXML(Object object, String filePath) throws JAXBException {
        fileLock.lock();  // Ensure that only one thread is writing to the file at a time
        try {
            File file = new File(filePath);

            if (file.exists()) {
                try {
                    // Load existing object
                    Object existingObject = loadFromXML(filePath, object.getClass());

                    // Check if the object implements Mergeable and merge if so
                    if (existingObject instanceof Mergeable) {
                        ((Mergeable) existingObject).mergeWith(object);
                        logger.info("ARC-XML", StringTemplate.STR."green[Merged object into existing data for file: \{filePath}]");
                        object = existingObject;  // Update reference to save the merged version
                    }
                } catch (JAXBException e) {
                    logger.error("ARC-XML", StringTemplate.STR."Failed to load existing object for merging. Saving new object as is.\{e}");
                }
            }

            File parentDir = file.getParentFile();
            parentDir.mkdirs();

            // Save the (merged or new) object to XML
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(object, file);
            logger.info("ARC-XML", StringTemplate.STR."Object saved to XML at: \{filePath}");

        } finally {
            fileLock.unlock();  // Unlock the file after the operation is complete
        }
    }

    // Load XML into an object with thread-safe file access
    public <T> T loadFromXML(String filePath, Class<T> clazz) throws JAXBException {
        fileLock.lock();  // Ensure that only one thread is reading from the file at a time
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logger.error("ARC-XML","XML file not found: " + file.getAbsolutePath());
                return null;
            }

            JAXBContext context = JAXBContext.newInstance(clazz);
            return (T) context.createUnmarshaller().unmarshal(file);
        } catch (Exception e) {
            e.printStackTrace(); // Log stack trace for debugging
            return null;
        } finally {
            fileLock.unlock();  // Unlock the file after the operation is complete
        }
    }


    public void createUserAsync(String username, String password, String email,
                                String firstname, String lastname, Genders gender,
                                Consumer<Boolean> callback) {
        logger.warning("ARC-XML", "Creation of users async has many issues and is not currently supported.");
        new Thread(() -> {
            boolean success = false;
            try {
                success = createUser(username, password, email, firstname, lastname, gender);
            } catch (Exception e) {
                logger.severe("ARC-XML", StringTemplate.STR."Error creating user asynchronously \{e}");
            }
            callback.accept(success);  // Notify callback with the result (true or false)
        }).start();
    }
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

                logger.info("ARC-XML", "Making the default user...");
                User defaultUser = createUserObject(username, password, email, firstname, lastname, gender);
                registrar.getUsers().add(defaultUser);
                registrar.setCreatedDefault(true);

                Player defaultPlayer = createPlayerObject(gender, defaultUser);
                saveToXML(registrar, "./ServerData/XML/registeredUsers.xml");
                saveToXML(defaultPlayer, "./ServerData/XML/Users/" + username + ".xml");

                return true;
            }

            // For non-default users
            User newUser = createUserObject(username, password, email, firstname, lastname, gender);
            registrar.getUsers().add(newUser);

            Player newPlayer = createPlayerObject(gender, newUser);
            saveToXML(registrar, "./ServerData/XML/registeredUsers.xml");
            saveToXML(newPlayer, "./ServerData/XML/Users/" + username + ".xml");

            return true;

        } catch (JAXBException | InterruptedException | ExecutionException e) {
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

                logger.info("ARC-XML", "Making the default user...");
                User defaultUser = createUserObject(username, password, email, firstname, lastname, gender);
                registrar.getUsers().add(defaultUser);
                registrar.setCreatedDefault(true);

                Player defaultPlayer = createPlayerObject(gender, defaultUser);
                saveToXML(registrar, "./ServerData/XML/registeredUsers.xml");
                saveToXML(defaultPlayer, "./ServerData/XML/Users/" + username + ".xml");

                return true;
            }

            // For non-default users
            User newUser = createUserObject(username, password, email, firstname, lastname, gender);
            registrar.getUsers().add(newUser);

            Player newPlayer = createPlayerObject(gender, newUser);
            saveToXML(registrar, "./ServerData/XML/registeredUsers.xml");
            saveToXML(newPlayer, "./ServerData/XML/Users/" + username + ".xml");

            return true;

        } catch (JAXBException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    private PlayerRegistrar getOrCreateRegistrar() throws JAXBException {
        File registrarFile = new File("./ServerData/XML/registeredUsers.xml");
        if (registrarFile.exists()) {
            logger.info("ARC-XML","Using provided registrar found at def location");
            return loadFromXML(registrarFile.getPath(), PlayerRegistrar.class);
        }
        logger.info("ARC-XML", "No registrar found. Creating new registrar");
        PlayerRegistrar newRegistrar = new PlayerRegistrar();
        newRegistrar.setUsers(new ArrayList<>());
        return newRegistrar;
    }

    private FamilyRegistrar getOrCreateFamilyRegistrar() throws JAXBException {
        File registrarFile = new File("./ServerData/XML/registeredFamilies.xml");
        if (registrarFile.exists()) {
            logger.info("ARC-XML", "Using provided family registrar found at def location");
            return loadFromXML(registrarFile.getPath(), FamilyRegistrar.class);
        }
        logger.info("ARC-XML", "No registrar found. Creating new registrar");
        FamilyRegistrar newRegistrar = new FamilyRegistrar();
        newRegistrar.setFamilies(new ArrayList<>());
        return newRegistrar;
    }

    private MaterialRegistrar getOrCreateMaterialRegistrar() throws JAXBException {
        File registrarFile = new File("./ServerData/XML/registeredMaterials.xml");
        if (registrarFile.exists()) {
            logger.info("ARC-XML", "Using provided material registrar found at def location");
            MaterialRegistrar registrar = loadFromXML(registrarFile.getPath(), MaterialRegistrar.class);
            if (registrar.getAccessoryList() == null){
                registrar.setAccessoryList(new ArrayList<>());
            }
            if (registrar.getBottomClothList() == null){
                registrar.setBottomClothList(new ArrayList<>());
            }
            if (registrar.getShoesList() == null){
                registrar.setShoesList(new ArrayList<>());
            }
            if (registrar.getTopClothList() == null){
                registrar.setTopClothList(new ArrayList<>());
            }
            return registrar;
        }
        logger.info("ARC-XML", "No registrar found. Creating new registrar");
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

    private boolean isDefaultUser(String username) {
        return "test1".equals(username);
    }

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

    private Player createPlayerObject(Genders gender, User user) throws JAXBException, InterruptedException, ExecutionException {
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
        CompletableFuture<Family> familyFuture = getFamilyAsync(familyRegistrar, familyName);

        // Wait for the asynchronous task to complete and retrieve the family object
        Family family = familyFuture.get();  // This blocks until the family is retrieved

        MinimalUser mUser = new MinimalUser(user);
        family.addFamilyMember(mUser);

        familyRegistrar.getFamilies().add(family);

        PlayerFamily playerFamily = new PlayerFamily(family);
        playerFamily.setFamilyPosition(family.getFamilyMembers().size());
        player.setFamily(playerFamily);

        // Save to XML
        XmlConfigManager manager = new XmlConfigManager(Family.class, MaterialRegistrar.class, FamilyRegistrar.class, Player.class, User.class, PlayerRegistrar.class, TopCloth.class, BottomCloth.class, Shoes.class, Shoe.class, Accessory.class);
        manager.saveToXML(familyRegistrar, "./ServerData/XML/registeredFamilies.xml");
        manager.saveToXML(materialRegistrar, "./ServerData/XML/registeredMaterials.xml");

        return player;
    }


    private static Family getFamily(FamilyRegistrar familyRegistrar, FamilyNames familyName) {
        List<Family> families = familyRegistrar.getFamilies();

        // Check for special family names
        boolean isSpecial = familyName.equals(FamilyNames.POPOOLA) ||
            familyName.equals(FamilyNames.OSITUNGA) ||
            familyName.equals(FamilyNames.ADEDO) ||
            familyName.equals(FamilyNames.ADEJOH);

        if (families == null) {
            families = new ArrayList<>();
            familyRegistrar.setFamilies(families); // Initialize families list if null
        }

        // Check if the family already exists
        Family family = null;
        for (Family existingFamily : families) {
            if (existingFamily.getFamilyName().equals(familyName.getFamilyName())) {
                family = existingFamily;
                break;
            }
        }

        // Create a new family if it doesn't exist
        if (family == null) {
            family = new Family(familyName);
            family.setFamilyMembers(new ArrayList<>()); // Initialize family members list

            // Set family wealth and size
            if (isSpecial) {
                family.setFamilyWealth(Wealth.GOD_GIVEN_WEALTH);
                family.setFamilySize(100);
            } else {
                family.setFamilyWealth(Wealth.getRandomWealthByWeight());
                family.setFamilySize(FamilyNames.SizeRandomizer()); // Set a default size for non-special families
            }

            families.add(family); // Add the new family to the registrar
        }

        return family;

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

    // Helper methods remain the same as before
    private TopClothes createTopClothes(Genders gender, String variant) {
        String g;
        if (gender == Genders.MALE){
            g = "m";
        }else{
            g = "f";
        }

        Material material = variant.equals("def") ? Material.POLYESTER : Material.WOOL;
        Rarity rarity = Rarity.COMMON;
        String key = variant.equals("def") ? "t_def_"+g : "t_alt_"+g;
        TopCloth shoe = new TopCloth(rarity, material, key);

        return new TopClothes(shoe);
    }
    private BottomClothes createBottomClothes(Genders gender, String variant) {
        String g;
        if (gender == Genders.MALE){
            g = "m";
        }else{
            g = "f";
        }

        Material material = variant.equals("def") ? Material.CHINOS : Material.JEANS;
        Rarity rarity = Rarity.COMMON;
        String key = variant.equals("def") ? "c_def_"+g : "c_alt_"+g;
        BottomCloth shoe = new BottomCloth(rarity, material, key);

        return new BottomClothes(shoe);
    }
    private Shoes createShoes(String variant) {
        Material material = variant.equals("def") ? Material.LEATHER : Material.CANVAS;
        Rarity rarity = Rarity.COMMON;
        String key = variant.equals("def") ? "s_def_u" : "s_alt_u";
        Shoe shoe = new Shoe(rarity, material, key);

        return new Shoes(shoe);
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
        String key = variant.equals("def") ? "t_def_"+g : "t_alt_"+g;

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
        String key = variant.equals("def") ? "c_def_"+g : "c_alt_"+g;

        return new BottomCloth(rarity, material, key);
    }
    private Shoe createShoe(String variant) {
        Material material = variant.equals("def") ? Material.LEATHER : Material.CANVAS;
        Rarity rarity = Rarity.COMMON;
        String key = variant.equals("def") ? "s_def_u" : "s_alt_u";

        return new Shoe(rarity, material, key);
    }
}
