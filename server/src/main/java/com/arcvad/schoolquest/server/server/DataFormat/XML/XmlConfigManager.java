package com.arcvad.schoolquest.server.server.DataFormat.XML;

import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.Family;
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
import com.arcvad.schoolquest.server.server.GlobalUtils.EnumRandomizer;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import com.arcvad.schoolquest.server.server.Playerutils.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
                    logger.warning("ARC-XML", StringTemplate.STR."Failed to load existing object for merging. Saving new object as is.\{e}");
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
                return null;
            }

            JAXBContext context = JAXBContext.newInstance(clazz);
            return (T) context.createUnmarshaller().unmarshal(file);
        } finally {
            fileLock.unlock();  // Unlock the file after the operation is complete
        }
    }

    public void createUserAsync(String username, String password, String email,
                                String firstname, String lastname, Genders gender,
                                Consumer<Boolean> callback) {
        new Thread(() -> {
            boolean success = false;
            try {
                success = createUser(username, password, email, firstname, lastname, gender);
            } catch (Exception e) {
                logger.severe("ARC-XML", StringTemplate.STR."Error creating user asynchronously\{e}");
            }
            callback.accept(success);  // Notify callback with the result (true or false)
        }).start();
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
                saveToXML(registrar, "./ServerData/registeredUsers.xml");
                saveToXML(defaultPlayer, "./ServerData/Users/" + username + ".xml");

                return true;
            }

            // For non-default users
            User newUser = createUserObject(username, password, email, firstname, lastname, gender);
            registrar.getUsers().add(newUser);

            Player newPlayer = createPlayerObject(gender, newUser);
            saveToXML(registrar, "./ServerData/registeredUsers.xml");
            saveToXML(newPlayer, "./ServerData/Users/" + username + ".xml");

            return true;

        } catch (JAXBException e) {
            e.printStackTrace();
            return false;
        }
    }

    private PlayerRegistrar getOrCreateRegistrar() throws JAXBException {
        File registrarFile = new File("./ServerData/registeredUsers.xml");
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
        File registrarFile = new File("./Families/registeredFamilies.xml");
        if (registrarFile.exists()) {
            logger.info("ARC-XML", "Using provided family registrar found at def location");
            return loadFromXML(registrarFile.getPath(), FamilyRegistrar.class);
        }
        logger.info("ARC-XML", "No registrar found. Creating new registrar");
        FamilyRegistrar newRegistrar = new FamilyRegistrar();
        newRegistrar.setFamilies(new ArrayList<>());
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

    private Player createPlayerObject(Genders gender, User user) throws JAXBException {
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
        family.getFamilyMembers().add(mUser);

        familyRegistrar.getFamilies().add(family);

        PlayerFamily playerFamily = new PlayerFamily(family);
        playerFamily.setFamilyPosition(family.getFamilyMembers().size());
        player.setFamily(playerFamily);

        XmlConfigManager manager = new XmlConfigManager(Family.class, FamilyRegistrar.class, Player.class, User.class, PlayerRegistrar.class, TopCloth.class, BottomCloth.class, Shoes.class, Shoe.class, Accessory.class);
        manager.saveToXML(familyRegistrar, "./ServerData/registeredFamilies.xml");

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
