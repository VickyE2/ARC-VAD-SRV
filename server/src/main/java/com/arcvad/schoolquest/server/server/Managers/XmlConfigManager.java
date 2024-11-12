package com.arcvad.schoolquest.server.server.Managers;

import com.arcvad.schoolquest.server.server.GlobalUtils.EnumRandomizer;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import com.arcvad.schoolquest.server.server.Playerutils.*;
import com.arcvad.schoolquest.server.server.Templates.Attributes.Family;
import com.arcvad.schoolquest.server.server.Templates.Attributes.PlayerFamily;
import com.arcvad.schoolquest.server.server.Templates.BaseTemplate;
import com.arcvad.schoolquest.server.server.Templates.Entities.*;
import com.arcvad.schoolquest.server.server.Templates.Wearables.Accessory.Accessory;
import com.arcvad.schoolquest.server.server.Templates.Wearables.BottomCloth.BottomCloth;
import com.arcvad.schoolquest.server.server.Templates.Wearables.BottomCloth.BottomClothes;
import com.arcvad.schoolquest.server.server.Templates.Wearables.Shoe.Shoe;
import com.arcvad.schoolquest.server.server.Templates.Wearables.Shoe.Shoes;
import com.arcvad.schoolquest.server.server.Templates.Wearables.TopCloth.TopCloth;
import com.arcvad.schoolquest.server.server.Templates.Wearables.TopCloth.TopClothes;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XmlConfigManager {
    private final JAXBContext jaxbContext;
    private static final Logger logger = Logger.getLogger(XmlConfigManager.class.getName());


    @SafeVarargs
    public XmlConfigManager(Class<? extends BaseTemplate>... classesToBeBound) throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance(classesToBeBound);
    }

    // Save data, loading and merging if the file already exists
    public void saveToXML(Object object, String filePath) throws JAXBException {
        File file = new File(filePath);

        if (file.exists()) {
            try {
                // Load existing object
                Object existingObject = loadFromXML(filePath, object.getClass());

                // Check if the object implements Mergeable and merge if so
                if (existingObject instanceof Mergeable) {
                    ((Mergeable) existingObject).mergeWith(object);
                    logger.info("Merged object into existing data for file: " + filePath);
                    object = existingObject;  // Update reference to save the merged version
                }
            } catch (JAXBException e) {
                logger.log(Level.WARNING, "Failed to load existing object for merging. Saving new object as is.", e);
            }
        }

        File parentDir = file.getParentFile();
        parentDir.mkdirs();

        // Save the (merged or new) object to XML
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(object, file);
        logger.info("Object saved to XML at: " + filePath);
    }

    // Load XML into an object
    public <T> T loadFromXML(String filePath, Class<T> clazz) throws JAXBException {
        File file = new File(filePath);

        if (!file.exists()) {
            return null;
        }

        JAXBContext context = JAXBContext.newInstance(clazz);
        return (T) context.createUnmarshaller().unmarshal(file);
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

                System.out.println("Making the default user...");
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
            System.out.println("Using provided registrar found at def location");
            return loadFromXML(registrarFile.getPath(), PlayerRegistrar.class);
        }
        System.out.println("No registrar found. Creating new registrar");
        PlayerRegistrar newRegistrar = new PlayerRegistrar();
        newRegistrar.setUsers(new ArrayList<>());
        return newRegistrar;
    }

    private FamilyRegistrar getOrCreateFamilyRegistrar() throws JAXBException {
        File registrarFile = new File("./Families/registeredFamilies.xml");
        if (registrarFile.exists()) {
            System.out.println("Using provided family registrar found at def location");
            return loadFromXML(registrarFile.getPath(), FamilyRegistrar.class);
        }
        System.out.println("No registrar found. Creating new registrar");
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
        Wealth familyWealth = Wealth.getRandomWealthByWeight();

        FamilyRegistrar familyRegistrar = getOrCreateFamilyRegistrar();
        Family family = new Family(familyName);
        family.setFamilyWealth(familyWealth);
        family.setFamilyMembers(new ArrayList<>());
        MinimalUser mUser = new MinimalUser(user);
        family.getFamilyMembers().add(mUser);
        family.setFamilySize();
        familyRegistrar.getFamilies().add(family);

        PlayerFamily playerFamily = new PlayerFamily(family);
        playerFamily.setFamilyPosition(family.getFamilyMembers().size() + 1);
        player.setFamily(playerFamily);

        XmlConfigManager manager = new XmlConfigManager(Family.class, FamilyRegistrar.class, Player.class, User.class, PlayerRegistrar.class, TopCloth.class, BottomCloth.class, Shoes.class, Shoe.class, Accessory.class);
        manager.saveToXML(familyRegistrar, "./ServerData/registeredFamilies.xml");

        return player;
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
