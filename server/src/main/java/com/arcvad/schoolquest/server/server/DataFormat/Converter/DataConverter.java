package com.arcvad.schoolquest.server.server.DataFormat.Converter;

import com.arcvad.schoolquest.server.server.DataFormat.Converter.util.Backup;
import com.arcvad.schoolquest.server.server.DataFormat.Converter.util.BackupManager;
import com.arcvad.schoolquest.server.server.DataFormat.Converter.util.BackupType;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.JsonConfigManager;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.MinimalUser;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoe;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopClothes;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.SQLManager;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.FamilyRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.HibernateDatabaseManager;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.PlayerBuilder;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.Family;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.MaterialRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.PlayerFamily;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.Player;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.PlayerRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.User;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomClothes;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoes;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.XmlConfigManager;
import com.arcvad.schoolquest.server.server.Playerutils.FamilyNames;
import com.arcvad.schoolquest.server.server.Playerutils.FileBasedPlayerBuilder;
import com.arcvad.schoolquest.server.server.Playerutils.UUIDGenerator;
import jakarta.xml.bind.JAXBException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.arcvad.schoolquest.server.server.DataFormat.Converter.FolderZipper.compressFolder;
import static com.arcvad.schoolquest.server.server.DataFormat.Converter.FolderZipper.deleteFolder;
import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;
import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.server;

@SuppressWarnings({"preview", "checks"})
public class DataConverter {

    public static boolean convertXmlToSql(boolean createBackup, boolean flushFiles) {
        try {
            logger.debug("ARC-CONVERT", "Server is about to undergo data format conversion. All players will be kicked...");
            server.broadcast("ALERT:Server is undergoing data transfer...");

            XmlConfigManager manager = new XmlConfigManager(Player.class, User.class, PlayerRegistrar.class, TopCloth.class, BottomCloth.class, Shoes.class, Accessory.class);

            logger.debug("ARC-CONVERT", "Creating SQL database...");
            SQLManager.createDatabase();

            Map<String, com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family> sqlUserFamilyMap
                = new HashMap<>();

            HibernateDatabaseManager databaseManager = new HibernateDatabaseManager();

            PlayerRegistrar playerRegistrar = manager.loadFromXML("./ServerData/XML/registeredUsers.xml", PlayerRegistrar.class);
            com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.PlayerRegistrar sqlPlayerRegistrar =
                new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.PlayerRegistrar();
            sqlPlayerRegistrar.setUsers(new ArrayList<>());
            sqlPlayerRegistrar.setId(1L);
            sqlPlayerRegistrar.setCreatedDefault(true);

            logger.debug("ARC-CONVERT", "Converting stored family instances...");
            com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.FamilyRegistrar familyRegistrar =
                manager.loadFromXML("./ServerData/XML/registeredFamilies.xml", com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.FamilyRegistrar.class);

            FamilyRegistrar sqlFamilyRegistrar = new FamilyRegistrar();
            sqlFamilyRegistrar.setFamilies(new ArrayList<>());
            sqlFamilyRegistrar.setId(1L);
            databaseManager.saveEntity(sqlFamilyRegistrar);

            logger.debug("ARC-CONVERT", "Converting stored material instances...");
            MaterialRegistrar materialRegistrar = getOrCreateMaterialRegistrar(manager);
            com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.MaterialRegistrar sqlMaterialRegistrar =
                new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.MaterialRegistrar();
            sqlMaterialRegistrar.setAccessoryList(new ArrayList<>());
            sqlMaterialRegistrar.setShoesList(new ArrayList<>());
            sqlMaterialRegistrar.setBottomClothList(new ArrayList<>());
            sqlMaterialRegistrar.setTopClothList(new ArrayList<>());
            sqlMaterialRegistrar.setId(1L);
            databaseManager.saveEntity(sqlMaterialRegistrar);

            if (materialRegistrar.getAccessoryList() != null && !materialRegistrar.getAccessoryList().isEmpty()) {
                for (Accessory accessory : materialRegistrar.getAccessoryList()) {
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory sqlAccessory =
                        new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory();

                    logger.debug("ARC-CONVERT", "Processing accessory material: " + accessory.getkeyentifyer());
                    sqlAccessory.setKey(accessory.getkeyentifyer());
                    sqlAccessory.setMaterial(accessory.getItems());
                    sqlAccessory.setRarity(accessory.getCommonness());
                    sqlAccessory.setMaterialRegistrar(sqlMaterialRegistrar);
                    sqlMaterialRegistrar.getAccessoryList().add(sqlAccessory);

                    databaseManager.saveEntity(sqlAccessory);
                }
            }
            for (TopCloth topCloth : materialRegistrar.getTopClothList()){

                logger.debug("ARC-CONVERT", "Processing topCloth material: " + topCloth.getKey());
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth sqlTopCloth =
                    new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth();

                sqlTopCloth.setKey(topCloth.getKey());
                sqlTopCloth.setMaterial(topCloth.getMaterial());
                sqlTopCloth.setRarity(topCloth.getrarity());
                sqlTopCloth.setMaterialRegistrar(sqlMaterialRegistrar);
                sqlMaterialRegistrar.getTopClothList().add(sqlTopCloth);

                logger.debug("ARC-CONVERT",STR."Saving TopCloth: key=\{sqlTopCloth.getKey()}, material=\{sqlTopCloth.getMaterial()}, rarity=\{sqlTopCloth.getRarity()}");
                databaseManager.saveEntity(sqlTopCloth);

            }
            for (BottomCloth bottomCloth : materialRegistrar.getBottomClothList()){

                logger.debug("ARC-CONVERT", "Processing bottomCloth material: " + bottomCloth.getKey());
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth sqlBottomCloth =
                    new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth(
                        bottomCloth.getrarity(),
                        bottomCloth.getMaterial(),
                        bottomCloth.getKey()
                    );

                sqlBottomCloth.setMaterialRegistrar(sqlMaterialRegistrar);
                sqlMaterialRegistrar.getBottomClothList().add(sqlBottomCloth);

                databaseManager.saveEntity(sqlBottomCloth);
            }
            for (com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe shoe :
                materialRegistrar.getShoesList()){

                logger.debug("ARC-CONVERT", "Processing shoe: " + shoe.getKey());
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe sqlShoe =
                    new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe(
                        shoe.getrarity(),
                        shoe.getMaterial(),
                        shoe.getKey()
                    );

                sqlShoe.setMaterialRegistrar(sqlMaterialRegistrar);
                sqlMaterialRegistrar.getShoesList().add(sqlShoe);

                databaseManager.saveEntity(sqlShoe);
            }
            logger.debug("ARC-CONVERT", "Saving converted material data to SQL database...");

            logger.info("ARC-CONVERT", "purple[italic[Converting registered player instances.]]");

            //Player conversions
            databaseManager.saveEntity(sqlPlayerRegistrar);
            List<com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family> foundFamilies = new ArrayList<>();
            for (com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.User User : playerRegistrar.getUsers()){
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.User sqlUser =
                    new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.User();

                sqlUser.setEmail(User.getEmail());
                sqlUser.setFirstname(User.getFirstname());
                sqlUser.setLastname(User.getLastname());
                sqlUser.setGender(User.getGender());
                sqlUser.setUsername(User.getUsername());
                sqlUser.setPassword(User.getPassword());
                sqlUser.setPlayerRegistrar(sqlPlayerRegistrar);
                sqlUser.setFamily(new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family());
                String playerId = UUIDGenerator.generateUUIDFromString(sqlUser.getUsername()).toString();

                logger.debug("ARC-CONVERT", STR."purple[Converting player \{User.getUsername()}]");
                Player currentPlayer = manager.loadFromXML(STR."./ServerData/XML/Users/\{User.getUsername()}.xml", Player.class);
                List<Family> xmlFamilies = familyRegistrar.getFamilies();
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family sqlPlayerFamily;
                for (Family family : xmlFamilies) {
                    boolean isCurrentUserFamilyMember = family.getFamilyMembers().stream()
                        .anyMatch(minimalUser -> minimalUser.getUsername().equals(sqlUser.getUsername()));

                    if (isCurrentUserFamilyMember) {
                        sqlPlayerFamily = foundFamilies.stream()
                            .filter(existingFamily -> existingFamily.getFamilyName().equals(family.getFamilyName()))
                            .findAny()
                            .orElseGet(() -> {
                                // Create a new family and add it to the list
                                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family newFamily =
                                    new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family(
                                        FamilyNames.valueOf(family.getFamilyName().toUpperCase())
                                    );
                                newFamily.setFamilyRegistrar(sqlFamilyRegistrar);
                                newFamily.setFamilySize(family.getFamilySize());
                                newFamily.setFamilyWealth(family.getFamilyWealth());
                                newFamily.setFamilyMembers(new ArrayList<>());
                                foundFamilies.add(newFamily);
                                return newFamily;
                            });

                        sqlUser.setFamily(sqlPlayerFamily);
                        sqlUser.setFamilyPosition(sqlPlayerFamily.getFamilySize());
                        break;
                    }
                }

                logger.debug("ARC-CONVERT", STR."Player Family: \{sqlUser.getFamily().getFamilyNames()}");
                logger.debug("ARC-CONVERT", STR."Player Family Registrar: \{sqlUser.getFamily().getFamilyRegistrar()}");

                List<com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory> currentSqlAccessories =
                    new ArrayList<>();
                List<com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory> ownedSqlAccessories =
                    new ArrayList<>();
                List<com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth> ownedSqlTopClothes =
                    new ArrayList<>();
                List<com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth> ownedSqlBottomCloth =
                    new ArrayList<>();
                List<com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe> ownedSqlShoes =
                    new ArrayList<>();

                if (currentPlayer.getAdornments() != null && !currentPlayer.getAdornments().isEmpty()) {
                    for (Accessory accessory : currentPlayer.getAdornments()) {
                        logger.info("ARC-CONVERT", STR."Converting player item with key: \{accessory.getkeyentifyer()}");
                        com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory sqlAccessory =
                            sqlMaterialRegistrar.getAccessoryList().stream()
                                .filter(accessory1 -> accessory1.getKey().equals(accessory.getkeyentifyer()))
                                .findFirst()
                                .get();

                        currentSqlAccessories.add(sqlAccessory);
                    }
                }
                if (currentPlayer.getCollectedAdornments() != null && !currentPlayer.getCollectedAdornments().isEmpty()){
                    for (Accessory accessory : currentPlayer.getCollectedAdornments()){
                        logger.info("ARC-CONVERT", STR."Converting player item with key: \{accessory.getkeyentifyer()}");
                        com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory sqlAccessory =
                            sqlMaterialRegistrar.getAccessoryList().stream()
                                .filter(accessory1 -> accessory1.getKey().equals(accessory.getkeyentifyer()))
                                .findFirst()
                                .get();
                        ownedSqlAccessories.add(sqlAccessory);
                    }
                }
                for (TopCloth topCloth : currentPlayer.getCollectedUpperWear()){
                    String topClothKey = topCloth.getKey();
                    logger.info("ARC-CONVERT", STR."Converting collected player item with key: \{topClothKey}");
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth sqlTopCloth =
                        sqlMaterialRegistrar.getTopClothList().stream()
                            .filter(topCloth1 -> topCloth1.getKey().equals(topClothKey))
                            .findFirst()
                            .get();

                    ownedSqlTopClothes.add(sqlTopCloth);
                }
                for (BottomCloth bottomCloth : currentPlayer.getCollectedLowerWear()){
                    logger.info("ARC-CONVERT", STR."Converting collected player item with key: \{bottomCloth.getKey()}");
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth sqlBottomCloth =
                        sqlMaterialRegistrar.getBottomClothList().stream()
                            .filter(bottomCloth1 -> bottomCloth1.getKey().equalsIgnoreCase(bottomCloth.getKey()))
                            .findFirst()
                            .get();

                    ownedSqlBottomCloth.add(sqlBottomCloth);
                }
                for (com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe shoe :
                    currentPlayer.getCollectedFootwear()){
                    logger.info("ARC-CONVERT", STR."Converting collected player item with key: \{shoe.getKey()}");
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe sqlShoe =
                        sqlMaterialRegistrar.getShoesList().stream()
                                .filter(shoe1 -> shoe1.getKey().equals(shoe.getKey()))
                                    .findFirst()
                                        .get();

                    ownedSqlShoes.add(sqlShoe);
                }
                logger.info("ARC-CONVERT", STR."Converting player item with key: \{
                    currentPlayer.getSecondLayerCloth().getWearable().getKey()}");
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth sqlPlayerBottomCloth =
                    ownedSqlBottomCloth.stream()
                        .filter(bottomCloth -> bottomCloth.getKey().equals(currentPlayer.getSecondLayerCloth().getWearable().getKey()))
                        .findFirst()
                        .get();
                logger.info("ARC-CONVERT", STR."Converting player item with key: \{
                    currentPlayer.getFirstLayerCloth().getWearable().getKey()}");
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth sqlPlayerTopCloth =
                    ownedSqlTopClothes.stream()
                        .filter(topCloth -> topCloth.getKey().equals(currentPlayer.getFirstLayerCloth().getWearable().getKey()))
                        .findFirst()
                        .get();
                logger.info("ARC-CONVERT", STR."Converting player item with key: \{
                    currentPlayer.getFootwear().getWearable().getKey()}");
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe sqlPlayerShoe=
                    ownedSqlShoes.stream()
                        .filter(shoe -> shoe.getKey().equals(currentPlayer.getFootwear().getWearable().getKey()))
                        .findFirst()
                        .get();
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.Player sqlPlayer =
                    new PlayerBuilder()
                        .setEyeLashHue(currentPlayer.getEyelashColor())
                        .setHairHue(currentPlayer.getHairHue())
                        .setIrisHue(currentPlayer.getIrisHue())
                        .setSkinHue(currentPlayer.getSkinHue())
                        .setEyeLashType(currentPlayer.getEyeLashDesign())
                        .setHairType(currentPlayer.getHairType())
                        .setCurrentTopCloth(sqlPlayerTopCloth)
                        .setCurrentBottomCloth(sqlPlayerBottomCloth)
                        .setCurrentShoe(sqlPlayerShoe)
                        .setCurrentAccessories(currentSqlAccessories)
                        .setCollectedShoes(ownedSqlShoes)
                        .setCollectedAccessories(ownedSqlAccessories)
                        .setCollectedBottomCloth(ownedSqlBottomCloth)
                        .setCollectedTopCloth(ownedSqlTopClothes)
                        .setUser(sqlUser)
                        .setId(playerId)
                        .build();

                logger.debug("ARC-CONVERT",
                    STR."""
                        \n
                        Eyelash Hue: \{sqlPlayer.getEyeLashHue()}
                        Hair Hue: \{sqlPlayer.getHairHue()}
                        Iris Hue: \{sqlPlayer.getIrisHue()}
                        Skin Hue: \{sqlPlayer.getSkinHue()}
                        Eyelash Type: \{sqlPlayer.getEyeLashType()}
                        Hair Type: \{sqlPlayer.getHairType()}
                        Current Top Cloth: \{sqlPlayer.getCurrentTopCloth()}
                        Current Bottom Cloth: \{sqlPlayer.getCurrentBottomCloth()}
                        Current Shoe: \{sqlPlayer.getCurrentShoe()}
                        Current Accessories: \{sqlPlayer.getCurrentAccessories()}
                        Collected Shoes: \{sqlPlayer.getCollectedShoes()}
                        Collected Accessories: \{sqlPlayer.getCollectedAccessories()}
                        Collected Bottom Cloth: \{sqlPlayer.getCollectedBottomCloth()}
                        Collected Top Cloth: \{sqlPlayer.getCollectedTopCloth()}
                        User: \{sqlPlayer.getUser()}
                        Player ID: \{sqlPlayer.getId()}
                    """
                );

                sqlUser.setPlayer(sqlPlayer);

                logger.debug("ARC-SQL", "Saving entity: " + sqlUser);

                sqlPlayerRegistrar.addUser(sqlUser);

                databaseManager.saveEntity(sqlUser);
            }

            if (createBackup) {
                logger.debug("ARC-CONVERT", "Creating backup...");
                boolean fullySuccessful = createBackup("xml", flushFiles);
                if (fullySuccessful) {
                    logger.debug("ARC-CONVERT", "Conversion complete with backup created.");
                } else {
                    logger.debug("ARC-CONVERT", "Conversion complete but backup failed. Please check logs.");
                }
            }

            return true;
        }
        catch (Exception e) {
            logger.debug("ARC-CONVERT", "Exception during conversion: " + e.getMessage());
            File database = new File("./ServerData/databases/global.db");
            database.delete();
            e.printStackTrace();
            return false;
        }
    }
    public static boolean convertXmlToJson(boolean createBackup, boolean flushFiles) {
        try {

            logger.warning("ARC-CONVERT", "Server is about to undergo data format conversion. All players will be kicked...");
            server.broadcast("ALERT:Server is undergoing data transfer...");

            XmlConfigManager manager = new XmlConfigManager(Player.class, User.class, PlayerRegistrar.class, TopCloth.class, BottomCloth.class, Shoes.class, Accessory.class);
            JsonConfigManager jsonManager = new JsonConfigManager();

            logger.info("ARC-CONVERT", "purple[italic[Converting stored family instances.]]");
            com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.FamilyRegistrar familyRegistrar =
                manager.loadFromXML("./ServerData/XML/registeredFamilies.xml", com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.FamilyRegistrar.class);
            com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.FamilyRegistrar jsonFamilyRegistrar =
                new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.FamilyRegistrar();
            List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.Family> jsonFamilies =
                new ArrayList<>();
            for (Family family : familyRegistrar.getFamilies()) {
                com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.Family jsonFamily =
                    new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.Family();
                List<MinimalUser> jsonMinimalUsers = new ArrayList<>();
                for (com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.MinimalUser minimalUser : family.getFamilyMembers()){
                    MinimalUser jsonMinimalUser = new MinimalUser();

                    jsonMinimalUser.setFirstname(minimalUser.getFirstname());
                    jsonMinimalUser.setUsername(minimalUser.getUsername());
                    jsonMinimalUser.setLastname(minimalUser.getLastname());

                    jsonMinimalUsers.add(jsonMinimalUser);
                }

                jsonFamily.setFamilyName(family.getFamilyName());
                jsonFamily.setFamilySize(family.getFamilySize());
                jsonFamily.setFamilyWealth(family.getFamilyWealth());
                jsonFamily.setFamilySize(family.getFamilySize());
                jsonFamily.setFamilyMembers(jsonMinimalUsers);

                jsonFamilies.add(jsonFamily);
            }

            jsonFamilyRegistrar.setFamilies(jsonFamilies);
            jsonManager.saveToJSONAsync(jsonFamilyRegistrar, "./ServerData/JSON/registeredFamilies.json");

            logger.info("ARC-CONVERT", "purple[italic[Converting stored material instances.]]");
            MaterialRegistrar materialRegistrar = getOrCreateMaterialRegistrar(manager);
            com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.MaterialRegistrar jsonMaterialRegistrar =
                new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.MaterialRegistrar();

            List<Accessory> registeredAccessories = materialRegistrar.getAccessoryList();
            List<TopCloth> registeredTopCloth = materialRegistrar.getTopClothList();
            List<BottomCloth> registeredBottomCloth = materialRegistrar.getBottomClothList();
            List<com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe> registeredShoes = materialRegistrar.getShoesList();

            List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory> registeredJsonAccessories = new ArrayList<>();
            List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth> registeredJsonTopClothes = new ArrayList<>();
            List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth> registeredJsonBottomClothes = new ArrayList<>();
            List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoe> registeredJsonShoes = new ArrayList<>();

            for (Accessory accessory : registeredAccessories) {
                com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory registeredJsonAccessory
                    = new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory();

                registeredJsonAccessory.setItems(accessory.getItems());
                registeredJsonAccessory.setkeyentifyer(accessory.getkeyentifyer());
                registeredJsonAccessory.setCommonness(accessory.getCommonness());

                registeredJsonAccessories.add(registeredJsonAccessory);
            }
            for (TopCloth topCloth : registeredTopCloth) {
                com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth registeredJsonTopCLoth
                    = new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth(
                    topCloth.getrarity(),
                    topCloth.getMaterial(),
                    topCloth.getKey()
                );

                registeredJsonTopClothes.add(registeredJsonTopCLoth);
            }
            for (BottomCloth bottomCloth : registeredBottomCloth) {
                com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth registeredJsonBottomCLoth
                    = new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth(
                    bottomCloth.getrarity(),
                    bottomCloth.getMaterial(),
                    bottomCloth.getKey()
                );

                registeredJsonBottomClothes.add(registeredJsonBottomCLoth);
            }
            for (com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe shoe : registeredShoes) {
                 Shoe registeredJsonShoe = new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoe(
                     shoe.getrarity(),
                     shoe.getMaterial(),
                     shoe.getKey()
                 )

                ;

                registeredJsonShoes.add(registeredJsonShoe);
            }

            jsonMaterialRegistrar.setAccessoryList(registeredJsonAccessories);
            jsonMaterialRegistrar.setBottomClothList(registeredJsonBottomClothes);
            jsonMaterialRegistrar.setShoesList(registeredJsonShoes);
            jsonMaterialRegistrar.setTopClothList(registeredJsonTopClothes);

            jsonManager.saveToJSONAsync(jsonMaterialRegistrar, "./ServerData/JSON/registeredMaterials.json");

            logger.info("ARC-CONVERT", "purple[italic[Converting registered player instances.]]");
            PlayerRegistrar registrar = manager.loadFromXML("./ServerData/XML/registeredUsers.xml", PlayerRegistrar.class);
            com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.PlayerRegistrar jsonPlayerRegistrar =
                new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.PlayerRegistrar();
            List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.User> jsonUsers =
                new ArrayList<>();

            for (User user : registrar.getUsers()){
                com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.User jsonUser
                    = new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.User();

                jsonUser.setUsername(user.getUsername());
                jsonUser.setPassword(user.getPassword());
                jsonUser.setLastname(user.getLastname());
                jsonUser.setGender(user.getGender());
                jsonUser.setFirstname(user.getFirstname());
                jsonUser.setEmail(user.getEmail());

                jsonUsers.add(jsonUser);
            }

            jsonPlayerRegistrar.setUsers(jsonUsers);
            jsonManager.saveToJSONAsync(jsonPlayerRegistrar, "./ServerData/JSON/registeredUsers.json");
            for (User user : registrar.getUsers()){
                Player player = manager.loadFromXML(StringTemplate.STR."./ServerData/XML/Users/\{user.getUsername()}.xml", Player.class);

                List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory> collectedAccessories = new ArrayList<>();
                for (Accessory accessory : player.getCollectedAdornments()) {
                    com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory jsonAccessory =
                        new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory();
                    jsonAccessory.setItems(accessory.getItems());
                    jsonAccessory.setkeyentifyer(accessory.getkeyentifyer());
                    jsonAccessory.setCommonness(accessory.getCommonness());
                    collectedAccessories.add(jsonAccessory);
                }
                List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory> currentAccessories = new ArrayList<>();
                for (Accessory accessory : player.getAdornments()) {
                    com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory jsonAccessory =
                        new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory();
                    jsonAccessory.setItems(accessory.getItems());
                    jsonAccessory.setkeyentifyer(accessory.getkeyentifyer());
                    jsonAccessory.setCommonness(accessory.getCommonness());
                    currentAccessories.add(jsonAccessory);
                }
                List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth> collectedTopCloth = new ArrayList<>();
                for (TopCloth topCloth : player.getCollectedUpperWear()) {
                    com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth jsonTopCloth =
                        new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth(
                            topCloth.getrarity(),
                            topCloth.getMaterial(),
                            topCloth.getKey()
                        );
                    collectedTopCloth.add(jsonTopCloth);
                }
                List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth> collectedBottomCloth = new ArrayList<>();
                for (BottomCloth bottomCloth : player.getCollectedLowerWear()) {
                    com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth jsonBottomCloth =
                        new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth(
                            bottomCloth.getrarity(),
                            bottomCloth.getMaterial(),
                            bottomCloth.getKey()
                        );
                    collectedBottomCloth.add(jsonBottomCloth);
                }
                List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoe> collectedShoes = new ArrayList<>();
                for (com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe shoe : player.getCollectedFootwear()) {
                    com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoe jsonShoes =
                        new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoe(shoe.getrarity(), shoe.getMaterial(), shoe.getKey());
                    collectedShoes.add(jsonShoes);
                }

                com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.Player jsonPlayer =
                    new FileBasedPlayerBuilder()
                        .setSkinHue(player.getSkinHue())
                        .setIrisHue(player.getIrisHue())
                        .setHairHue(player.getHairHue())
                        .setEyeLashHue(player.getEyelashColor())
                        .setHairType(player.getHairType())
                        .setEyeLashType(player.getEyeLashDesign())
                        .setFamilyJson(
                            new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.PlayerFamily(
                                new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.Family(
                                    FamilyNames.valueOf(player.getFamily().getFamilyName().toUpperCase())
                                )
                            )
                        )
                        .setCurrentTopClothJson(
                            new TopClothes(
                                new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth(
                                    player.getFirstLayerCloth().getWearable().getrarity(),
                                    player.getFirstLayerCloth().getWearable().getMaterial(),
                                    player.getFirstLayerCloth().getWearable().getKey()
                                )
                            )
                        )
                        .setCurrentBottomClothJson(
                            new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomClothes(
                                new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth(
                                    player.getSecondLayerCloth().getWearable().getrarity(),
                                    player.getSecondLayerCloth().getWearable().getMaterial(),
                                    player.getSecondLayerCloth().getWearable().getKey()
                                )
                            )
                        )
                        .setCurrentShoeJson(
                            new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoes(
                                new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoe(
                                    player.getFootwear().getWearable().getrarity(),
                                    player.getFootwear().getWearable().getMaterial(),
                                    player.getFootwear().getWearable().getKey()
                                )
                            )
                        )
                        .setCurrentAccessoriesJson(currentAccessories)
                        .setCollectedAccessoriesJson(collectedAccessories)
                        .setCollectedBottomClothJson(collectedBottomCloth)
                        .setCollectedTopClothJson(collectedTopCloth)
                        .setCollectedShoesJson(collectedShoes)
                        .buildJson();


                jsonManager.saveToJSONAsync(jsonPlayer, StringTemplate.STR."./ServerData/JSON/Users/\{user.getUsername()}.json");
            }

            if (createBackup) {
                boolean fullySuccessful = createBackup("xml", flushFiles);
                if (fullySuccessful) {
                    logger.success("ARC-CONVERTER", "Conversion complete");
                }else {
                    logger.warning("ARC-CONVERTER", "Conversion complete but backup failed. Please check logs");
                }
            }

            return true;
        }
        catch (Exception e) {
            logger.severe("ARC-CONVERT", STR."Conversion failed \{e}");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean convertJsonToSql(boolean createBackup, boolean flushFiles) {
        try {
            logger.debug("ARC-CONVERT", "Server is about to undergo data format conversion. All players will be kicked...");
            server.broadcast("ALERT:Server is undergoing data transfer...");

            JsonConfigManager manager = new JsonConfigManager();

            logger.debug("ARC-CONVERT", "Creating SQL database...");
            SQLManager.createDatabase();

            Map<String, com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family> sqlUserFamilyMap
                = new HashMap<>();

            HibernateDatabaseManager databaseManager = new HibernateDatabaseManager();

            com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.PlayerRegistrar playerRegistrar =
                manager.loadFromJSON("./ServerData/JSON/registeredUsers.json", com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.PlayerRegistrar.class);
            com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.PlayerRegistrar sqlPlayerRegistrar =
                new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.PlayerRegistrar();
            sqlPlayerRegistrar.setUsers(new ArrayList<>());
            sqlPlayerRegistrar.setId(1L);
            sqlPlayerRegistrar.setCreatedDefault(true);

            logger.debug("ARC-CONVERT", "Converting stored family instances...");
            com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.FamilyRegistrar familyRegistrar =
                manager.loadFromJSON("./ServerData/JSON/registeredFamilies.json", com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.FamilyRegistrar.class);

            FamilyRegistrar sqlFamilyRegistrar = new FamilyRegistrar();
            sqlFamilyRegistrar.setFamilies(new ArrayList<>());
            sqlFamilyRegistrar.setId(1L);
            databaseManager.saveEntity(sqlFamilyRegistrar);

            logger.debug("ARC-CONVERT", "Converting stored material instances...");
            com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.MaterialRegistrar materialRegistrar =
                getOrCreateJsonMaterialRegistrar(manager);
            com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.MaterialRegistrar sqlMaterialRegistrar =
                new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.MaterialRegistrar();
            sqlMaterialRegistrar.setAccessoryList(new ArrayList<>());
            sqlMaterialRegistrar.setShoesList(new ArrayList<>());
            sqlMaterialRegistrar.setBottomClothList(new ArrayList<>());
            sqlMaterialRegistrar.setTopClothList(new ArrayList<>());
            sqlMaterialRegistrar.setId(1L);
            databaseManager.saveEntity(sqlMaterialRegistrar);

            if (materialRegistrar.getAccessoryList() != null && !materialRegistrar.getAccessoryList().isEmpty()) {
                for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory accessory : materialRegistrar.getAccessoryList()) {
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory sqlAccessory =
                        new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory();

                    logger.debug("ARC-CONVERT", "Processing accessory material: " + accessory.getkeyentifyer());
                    sqlAccessory.setKey(accessory.getkeyentifyer());
                    sqlAccessory.setMaterial(accessory.getItems());
                    sqlAccessory.setRarity(accessory.getCommonness());
                    sqlAccessory.setMaterialRegistrar(sqlMaterialRegistrar);
                    sqlMaterialRegistrar.getAccessoryList().add(sqlAccessory);

                    databaseManager.saveEntity(sqlAccessory);
                }
            }
            for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth topCloth : materialRegistrar.getTopClothList()){

                logger.debug("ARC-CONVERT", "Processing topCloth material: " + topCloth.getKey());
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth sqlTopCloth =
                    new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth();

                sqlTopCloth.setKey(topCloth.getKey());
                sqlTopCloth.setMaterial(topCloth.getMaterial());
                sqlTopCloth.setRarity(topCloth.getrarity());
                sqlTopCloth.setMaterialRegistrar(sqlMaterialRegistrar);
                sqlMaterialRegistrar.getTopClothList().add(sqlTopCloth);

                logger.debug("ARC-CONVERT",STR."Saving TopCloth: key=\{sqlTopCloth.getKey()}, material=\{sqlTopCloth.getMaterial()}, rarity=\{sqlTopCloth.getRarity()}");
                databaseManager.saveEntity(sqlTopCloth);

            }
            for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth bottomCloth : materialRegistrar.getBottomClothList()){

                logger.debug("ARC-CONVERT", "Processing bottomCloth material: " + bottomCloth.getKey());
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth sqlBottomCloth =
                    new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth(
                        bottomCloth.getrarity(),
                        bottomCloth.getMaterial(),
                        bottomCloth.getKey()
                    );

                sqlBottomCloth.setMaterialRegistrar(sqlMaterialRegistrar);
                sqlMaterialRegistrar.getBottomClothList().add(sqlBottomCloth);

                databaseManager.saveEntity(sqlBottomCloth);
            }
            for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoe shoe :
                materialRegistrar.getShoesList()){

                logger.debug("ARC-CONVERT", "Processing shoe: " + shoe.getKey());
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe sqlShoe =
                    new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe(
                        shoe.getrarity(),
                        shoe.getMaterial(),
                        shoe.getKey()
                    );

                sqlShoe.setMaterialRegistrar(sqlMaterialRegistrar);
                sqlMaterialRegistrar.getShoesList().add(sqlShoe);

                databaseManager.saveEntity(sqlShoe);
            }
            logger.debug("ARC-CONVERT", "Saving converted material data to SQL database...");

            logger.info("ARC-CONVERT", "purple[italic[Converting registered player instances.]]");

            //Player conversions
            databaseManager.saveEntity(sqlPlayerRegistrar);
            List<com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family> foundFamilies = new ArrayList<>();
            for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.User User : playerRegistrar.getUsers()){
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.User sqlUser =
                    new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.User();

                sqlUser.setEmail(User.getEmail());
                sqlUser.setFirstname(User.getFirstname());
                sqlUser.setLastname(User.getLastname());
                sqlUser.setGender(User.getGender());
                sqlUser.setUsername(User.getUsername());
                sqlUser.setPassword(User.getPassword());
                sqlUser.setPlayerRegistrar(sqlPlayerRegistrar);
                sqlUser.setFamily(new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family());
                String playerId = UUIDGenerator.generateUUIDFromString(sqlUser.getUsername()).toString();

                logger.debug("ARC-CONVERT", STR."purple[Converting player \{User.getUsername()}]");
                com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.Player currentPlayer =
                    manager.loadFromJSON(STR."./ServerData/JSON/Users/\{User.getUsername()}.json", com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.Player.class);
                List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.Family> jsonFamilies =
                    familyRegistrar.getFamilies();
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family sqlPlayerFamily;
                for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.Family family : jsonFamilies) {
                    boolean isCurrentUserFamilyMember = family.getFamilyMembers().stream()
                        .anyMatch(minimalUser -> minimalUser.getUsername().equals(sqlUser.getUsername()));

                    if (isCurrentUserFamilyMember) {
                        sqlPlayerFamily = foundFamilies.stream()
                            .filter(existingFamily -> existingFamily.getFamilyName().equals(family.getFamilyName()))
                            .findAny()
                            .orElseGet(() -> {
                                // Create a new family and add it to the list
                                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family newFamily =
                                    new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family(
                                        FamilyNames.valueOf(family.getFamilyName().toUpperCase())
                                    );
                                newFamily.setFamilyRegistrar(sqlFamilyRegistrar);
                                newFamily.setFamilySize(family.getFamilySize());
                                newFamily.setFamilyWealth(family.getFamilyWealth());
                                newFamily.setFamilyMembers(new ArrayList<>());
                                foundFamilies.add(newFamily);
                                return newFamily;
                            });

                        sqlUser.setFamily(sqlPlayerFamily);
                        sqlUser.setFamilyPosition(sqlPlayerFamily.getFamilySize());
                        break;
                    }
                }

                logger.debug("ARC-CONVERT", STR."Player Family: \{sqlUser.getFamily().getFamilyNames()}");
                logger.debug("ARC-CONVERT", STR."Player Family Registrar: \{sqlUser.getFamily().getFamilyRegistrar()}");

                List<com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory> currentSqlAccessories =
                    new ArrayList<>();
                List<com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory> ownedSqlAccessories =
                    new ArrayList<>();
                List<com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth> ownedSqlTopClothes =
                    new ArrayList<>();
                List<com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth> ownedSqlBottomCloth =
                    new ArrayList<>();
                List<com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe> ownedSqlShoes =
                    new ArrayList<>();

                if (currentPlayer.getAdornments() != null && !currentPlayer.getAdornments().isEmpty()) {
                    for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory accessory : currentPlayer.getAdornments()) {
                        logger.info("ARC-CONVERT", STR."Converting player item with key: \{accessory.getkeyentifyer()}");
                        com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory sqlAccessory =
                            sqlMaterialRegistrar.getAccessoryList().stream()
                                .filter(accessory1 -> accessory1.getKey().equals(accessory.getkeyentifyer()))
                                .findFirst()
                                .get();

                        currentSqlAccessories.add(sqlAccessory);
                    }
                }
                if (currentPlayer.getCollectedAdornments() != null && !currentPlayer.getCollectedAdornments().isEmpty()){
                    for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory accessory : currentPlayer.getCollectedAdornments()){
                        logger.info("ARC-CONVERT", STR."Converting player item with key: \{accessory.getkeyentifyer()}");
                        com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory sqlAccessory =
                            sqlMaterialRegistrar.getAccessoryList().stream()
                                .filter(accessory1 -> accessory1.getKey().equals(accessory.getkeyentifyer()))
                                .findFirst()
                                .get();
                        ownedSqlAccessories.add(sqlAccessory);
                    }
                }
                for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth topCloth : currentPlayer.getCollectedUpperWear()){
                    String topClothKey = topCloth.getKey();
                    logger.info("ARC-CONVERT", STR."Converting collected player item with key: \{topClothKey}");
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth sqlTopCloth =
                        sqlMaterialRegistrar.getTopClothList().stream()
                            .filter(topCloth1 -> topCloth1.getKey().equals(topClothKey))
                            .findFirst()
                            .get();

                    ownedSqlTopClothes.add(sqlTopCloth);
                }
                for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth bottomCloth : currentPlayer.getCollectedLowerWear()){
                    logger.info("ARC-CONVERT", STR."Converting collected player item with key: \{bottomCloth.getKey()}");
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth sqlBottomCloth =
                        sqlMaterialRegistrar.getBottomClothList().stream()
                            .filter(bottomCloth1 -> bottomCloth1.getKey().equalsIgnoreCase(bottomCloth.getKey()))
                            .findFirst()
                            .get();

                    ownedSqlBottomCloth.add(sqlBottomCloth);
                }
                for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoe shoe :
                    currentPlayer.getCollectedFootwear()){
                    logger.info("ARC-CONVERT", STR."Converting collected player item with key: \{shoe.getKey()}");
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe sqlShoe =
                        sqlMaterialRegistrar.getShoesList().stream()
                            .filter(shoe1 -> shoe1.getKey().equals(shoe.getKey()))
                            .findFirst()
                            .get();

                    ownedSqlShoes.add(sqlShoe);
                }
                logger.info("ARC-CONVERT", STR."Converting player item with key: \{
                    currentPlayer.getSecondLayerCloth().getWearable().getKey()}");
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth sqlPlayerBottomCloth =
                    ownedSqlBottomCloth.stream()
                        .filter(bottomCloth -> bottomCloth.getKey().equals(currentPlayer.getSecondLayerCloth().getWearable().getKey()))
                        .findFirst()
                        .get();
                logger.info("ARC-CONVERT", STR."Converting player item with key: \{
                    currentPlayer.getFirstLayerCloth().getWearable().getKey()}");
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth sqlPlayerTopCloth =
                    ownedSqlTopClothes.stream()
                        .filter(topCloth -> topCloth.getKey().equals(currentPlayer.getFirstLayerCloth().getWearable().getKey()))
                        .findFirst()
                        .get();
                logger.info("ARC-CONVERT", STR."Converting player item with key: \{
                    currentPlayer.getFootwear().getWearable().getKey()}");
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe sqlPlayerShoe=
                    ownedSqlShoes.stream()
                        .filter(shoe -> shoe.getKey().equals(currentPlayer.getFootwear().getWearable().getKey()))
                        .findFirst()
                        .get();
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.Player sqlPlayer =
                    new PlayerBuilder()
                        .setEyeLashHue(currentPlayer.getEyelashColor())
                        .setHairHue(currentPlayer.getHairHue())
                        .setIrisHue(currentPlayer.getIrisHue())
                        .setSkinHue(currentPlayer.getSkinHue())
                        .setEyeLashType(currentPlayer.getEyeLashDesign())
                        .setHairType(currentPlayer.getHairType())
                        .setCurrentTopCloth(sqlPlayerTopCloth)
                        .setCurrentBottomCloth(sqlPlayerBottomCloth)
                        .setCurrentShoe(sqlPlayerShoe)
                        .setCurrentAccessories(currentSqlAccessories)
                        .setCollectedShoes(ownedSqlShoes)
                        .setCollectedAccessories(ownedSqlAccessories)
                        .setCollectedBottomCloth(ownedSqlBottomCloth)
                        .setCollectedTopCloth(ownedSqlTopClothes)
                        .setUser(sqlUser)
                        .setId(playerId)
                        .build();

                logger.debug("ARC-CONVERT",
                    STR."""
                        \n
                        Eyelash Hue: \{sqlPlayer.getEyeLashHue()}
                        Hair Hue: \{sqlPlayer.getHairHue()}
                        Iris Hue: \{sqlPlayer.getIrisHue()}
                        Skin Hue: \{sqlPlayer.getSkinHue()}
                        Eyelash Type: \{sqlPlayer.getEyeLashType()}
                        Hair Type: \{sqlPlayer.getHairType()}
                        Current Top Cloth: \{sqlPlayer.getCurrentTopCloth()}
                        Current Bottom Cloth: \{sqlPlayer.getCurrentBottomCloth()}
                        Current Shoe: \{sqlPlayer.getCurrentShoe()}
                        Current Accessories: \{sqlPlayer.getCurrentAccessories()}
                        Collected Shoes: \{sqlPlayer.getCollectedShoes()}
                        Collected Accessories: \{sqlPlayer.getCollectedAccessories()}
                        Collected Bottom Cloth: \{sqlPlayer.getCollectedBottomCloth()}
                        Collected Top Cloth: \{sqlPlayer.getCollectedTopCloth()}
                        User: \{sqlPlayer.getUser()}
                        Player ID: \{sqlPlayer.getId()}
                    """
                );

                sqlUser.setPlayer(sqlPlayer);

                logger.debug("ARC-SQL", "Saving entity: " + sqlUser);

                sqlPlayerRegistrar.addUser(sqlUser);

                databaseManager.saveEntity(sqlUser);
            }

            if (createBackup) {
                logger.debug("ARC-CONVERT", "Creating backup...");
                boolean fullySuccessful = createBackup("json", flushFiles);
                if (fullySuccessful) {
                    logger.debug("ARC-CONVERT", "Conversion complete with backup created.");
                } else {
                    logger.debug("ARC-CONVERT", "Conversion complete but backup failed. Please check logs.");
                }
            }

            return true;
        }
        catch (Exception e) {
            logger.debug("ARC-CONVERT", "Exception during conversion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public static boolean convertJsonToXml(boolean createBackup, boolean flushFiles) {
        try {

            logger.warning("ARC-CONVERT", "Server is about to undergo data format conversion. All players will be kicked...");
            server.broadcast("ALERT:Server is undergoing data transfer...");

            JsonConfigManager manager = new JsonConfigManager();
            XmlConfigManager xmlManager = new XmlConfigManager(Player.class, User.class, PlayerRegistrar.class, TopCloth.class, BottomCloth.class, Shoes.class, Accessory.class, com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.FamilyRegistrar.class, MaterialRegistrar.class);

            logger.info("ARC-CONVERT", "purple[italic[Converting stored family instances.]]");
            com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.FamilyRegistrar familyRegistrar =
                manager.loadFromJSON("./ServerData/JSON/registeredFamilies.json", com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.FamilyRegistrar.class);
            com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.FamilyRegistrar xmlFamilyRegistrar =
                new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.FamilyRegistrar();
            List<com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.Family> xmlFamilies =
                new ArrayList<>();
            for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.Family family : familyRegistrar.getFamilies()) {
                com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.Family xmlFamily =
                    new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.Family();
                List<com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.MinimalUser> xmlMinimalUsers = new ArrayList<>();
                for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.MinimalUser minimalUser : family.getFamilyMembers()){
                    com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.MinimalUser xmlMinimalUser = new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.MinimalUser();

                    xmlMinimalUser.setFirstname(minimalUser.getFirstname());
                    xmlMinimalUser.setUsername(minimalUser.getUsername());
                    xmlMinimalUser.setLastname(minimalUser.getLastname());

                    xmlMinimalUsers.add(xmlMinimalUser);
                }

                xmlFamily.setFamilyName(family.getFamilyName());
                xmlFamily.setFamilySize(family.getFamilySize());
                xmlFamily.setFamilyWealth(family.getFamilyWealth());
                xmlFamily.setFamilySize(family.getFamilySize());
                xmlFamily.setFamilyMembers(xmlMinimalUsers);

                xmlFamilies.add(xmlFamily);
            }

            xmlFamilyRegistrar.setFamilies(xmlFamilies);
            xmlManager.saveToXMLAsync(xmlFamilyRegistrar, "./ServerData/XML/registeredFamilies.xml");

            logger.info("ARC-CONVERT", "purple[italic[Converting stored material instances.]]");
            com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.MaterialRegistrar materialRegistrar = getOrCreateJsonMaterialRegistrar(manager);
            com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.MaterialRegistrar xmlMaterialRegistrar =
                new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.MaterialRegistrar();

            List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory> registeredAccessories = materialRegistrar.getAccessoryList();
            List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth> registeredTopCloth = materialRegistrar.getTopClothList();
            List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth> registeredBottomCloth = materialRegistrar.getBottomClothList();
            List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoe> registeredShoes = materialRegistrar.getShoesList();

            List<com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory> registeredXmlAccessories = new ArrayList<>();
            List<com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopCloth> registeredXmlTopClothes = new ArrayList<>();
            List<com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomCloth> registeredXmlBottomClothes = new ArrayList<>();
            List<com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe> registeredXmlShoes = new ArrayList<>();

            for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory accessory : registeredAccessories) {
                com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory registeredXmlAccessory
                    = new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory();

                registeredXmlAccessory.setItems(accessory.getItems());
                registeredXmlAccessory.setkeyentifyer(accessory.getkeyentifyer());
                registeredXmlAccessory.setCommonness(accessory.getCommonness());

                registeredXmlAccessories.add(registeredXmlAccessory);
            }
            for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth topCloth : registeredTopCloth) {
                com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopCloth registeredXmlTopCLoth
                    = new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopCloth(
                    topCloth.getrarity(),
                    topCloth.getMaterial(),
                    topCloth.getKey()
                );

                registeredXmlTopClothes.add(registeredXmlTopCLoth);
            }
            for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth bottomCloth : registeredBottomCloth) {
                com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomCloth registeredXmlBottomCLoth
                    = new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomCloth(
                    bottomCloth.getrarity(),
                    bottomCloth.getMaterial(),
                    bottomCloth.getKey()
                );

                registeredXmlBottomClothes.add(registeredXmlBottomCLoth);
            }
            for (Shoe shoe : registeredShoes) {
                com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe registeredXmlShoe =
                    new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe(
                            shoe.getrarity(),
                            shoe.getMaterial(),
                            shoe.getKey()
                    );

                registeredXmlShoes.add(registeredXmlShoe);
            }

            xmlMaterialRegistrar.setAccessoryList(registeredXmlAccessories);
            xmlMaterialRegistrar.setBottomClothList(registeredXmlBottomClothes);
            xmlMaterialRegistrar.setShoesList(registeredXmlShoes);
            xmlMaterialRegistrar.setTopClothList(registeredXmlTopClothes);

            xmlManager.saveToXMLAsync(xmlMaterialRegistrar, "./ServerData/XML/registeredMaterials.xml");

            logger.info("ARC-CONVERT", "purple[italic[Converting registered player instances.]]");
            com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.PlayerRegistrar registrar =
                manager.loadFromJSON("./ServerData/JSON/registeredUsers.json", com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.PlayerRegistrar.class);
            com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.PlayerRegistrar xmlPlayerRegistrar =
                new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.PlayerRegistrar();
            List<com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.User> jsonUsers =
                new ArrayList<>();

            for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.User user : registrar.getUsers()){
                com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.User xmlUser
                    = new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.User();

                xmlUser.setUsername(user.getUsername());
                xmlUser.setPassword(user.getPassword());
                xmlUser.setLastname(user.getLastname());
                xmlUser.setGender(user.getGender());
                xmlUser.setFirstname(user.getFirstname());
                xmlUser.setEmail(user.getEmail());

                jsonUsers.add(xmlUser);
            }

            xmlPlayerRegistrar.setUsers(jsonUsers);
            xmlManager.saveToXMLAsync(xmlPlayerRegistrar, "./ServerData/XML/registeredUsers.xml");

            for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.User user : registrar.getUsers()){
                com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.Player player =
                    manager.loadFromJSON(StringTemplate.STR."./ServerData/JSON/Users/\{user.getUsername()}.json",
                        com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.Player.class);

                List<com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory> collectedAccessories = new ArrayList<>();
                for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory accessory : player.getCollectedAdornments()) {
                    com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory xmlAccessory =
                        new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory();
                    xmlAccessory.setItems(accessory.getItems());
                    xmlAccessory.setkeyentifyer(accessory.getkeyentifyer());
                    xmlAccessory.setCommonness(accessory.getCommonness());
                    collectedAccessories.add(xmlAccessory);
                }
                List<com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory> currentAccessories = new ArrayList<>();
                for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory accessory : player.getAdornments()) {
                    com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory xmlAccessory =
                        new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory();
                    xmlAccessory.setItems(accessory.getItems());
                    xmlAccessory.setkeyentifyer(accessory.getkeyentifyer());
                    xmlAccessory.setCommonness(accessory.getCommonness());
                    currentAccessories.add(xmlAccessory);
                }
                List<com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopCloth> collectedTopCloth = new ArrayList<>();
                for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth topCloth : player.getCollectedUpperWear()) {
                    com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopCloth xmlTopCloth =
                        new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopCloth(
                            topCloth.getrarity(),
                            topCloth.getMaterial(),
                            topCloth.getKey()
                        );
                    collectedTopCloth.add(xmlTopCloth);
                }
                List<com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomCloth> collectedBottomCloth = new ArrayList<>();
                for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth bottomCloth : player.getCollectedLowerWear()) {
                    com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomCloth xmlBottomCloth =
                        new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomCloth(
                            bottomCloth.getrarity(),
                            bottomCloth.getMaterial(),
                            bottomCloth.getKey()
                        );
                    collectedBottomCloth.add(xmlBottomCloth);
                }
                List<com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe> collectedShoes = new ArrayList<>();
                for (com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoe shoe : player.getCollectedFootwear()) {
                    com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe xmlShoes =
                        new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe(shoe.getrarity(), shoe.getMaterial(), shoe.getKey());
                    collectedShoes.add(xmlShoes);
                }

                com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.Player xmlPlayer =
                    new FileBasedPlayerBuilder()
                        .setSkinHue(player.getSkinHue())
                        .setIrisHue(player.getIrisHue())
                        .setHairHue(player.getHairHue())
                        .setEyeLashHue(player.getEyelashColor())
                        .setHairType(player.getHairType())
                        .setEyeLashType(player.getEyeLashDesign())
                        .setFamilyXml(
                            new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.PlayerFamily(
                                new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.Family(
                                    FamilyNames.valueOf(player.getFamily().getFamilyName().toUpperCase())
                                )
                            )
                        )
                        .setCurrentTopClothXml(
                            new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopClothes(
                                new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopCloth(
                                    player.getFirstLayerCloth().getWearable().getrarity(),
                                    player.getFirstLayerCloth().getWearable().getMaterial(),
                                    player.getFirstLayerCloth().getWearable().getKey()
                                )
                            )
                        )
                        .setCurrentBottomClothXml(
                            new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomClothes(
                                new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomCloth(
                                    player.getSecondLayerCloth().getWearable().getrarity(),
                                    player.getSecondLayerCloth().getWearable().getMaterial(),
                                    player.getSecondLayerCloth().getWearable().getKey()
                                )
                            )
                        )
                        .setcurrentshoexml(
                            new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoes(
                                new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe(
                                    player.getFootwear().getWearable().getrarity(),
                                    player.getFootwear().getWearable().getMaterial(),
                                    player.getFootwear().getWearable().getKey()
                                )
                            )
                        )
                        .setCurrentAccessoriesXml(currentAccessories)
                        .setCollectedAccessoriesXml(collectedAccessories)
                        .setCollectedBottomClothXml(collectedBottomCloth)
                        .setCollectedTopClothXml(collectedTopCloth)
                        .setCollectedShoesXml(collectedShoes)
                        .buildXml();


                xmlManager.saveToXMLAsync(xmlPlayer, StringTemplate.STR."./ServerData/XML/Users/\{user.getUsername()}.xml");
            }

            if (createBackup) {
                boolean fullySuccessful = createBackup("json", flushFiles);
                if (fullySuccessful) {
                    logger.success("ARC-CONVERTER", "Conversion complete");
                }else {
                    logger.warning("ARC-CONVERTER", "Conversion complete but backup failed. Please check logs");
                }
            }

            return true;
        }
        catch (Exception e) {
            logger.severe("ARC-CONVERT", STR."Conversion failed \{e}");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean convertSqlToXml(boolean createBackup, boolean flushFiles) {
        try {
            logger.warning("ARC-CONVERT", "Server is about to undergo data format conversion. All players will be kicked...");
            server.broadcast("ALERT:Server is undergoing data transfer...");

            SQLManager.createDatabase();

            HibernateDatabaseManager databaseManager = new HibernateDatabaseManager();
            XmlConfigManager xmlManager = new XmlConfigManager(
                Player.class, User.class, PlayerRegistrar.class,
                TopCloth.class, BottomCloth.class, Shoes.class, Accessory.class,
                MaterialRegistrar.class, com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.FamilyRegistrar.class,
                Family.class
            );

            //SQL Registrars
            FamilyRegistrar familyRegistrar = databaseManager.getEntityById(FamilyRegistrar.class, 1L);

            com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.PlayerRegistrar playerRegistrar =
                databaseManager.getEntityById(
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.PlayerRegistrar.class,
                    1L
                );

            com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.MaterialRegistrar materialRegistrar =
                databaseManager.getEntityById(
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.MaterialRegistrar.class,
                    1L
                );

            //XML Registrars
            com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.FamilyRegistrar xmlFamilyRegistrar =
                new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.FamilyRegistrar();
            xmlFamilyRegistrar.setFamilies(new ArrayList<>());
            PlayerRegistrar xmlPlayerRegistrar = new PlayerRegistrar();
            xmlPlayerRegistrar.setUsers(new ArrayList<>());
            MaterialRegistrar xmlMaterialRegistrar = new MaterialRegistrar();
            xmlMaterialRegistrar.setTopClothList(new ArrayList<>());
            xmlMaterialRegistrar.setShoesList(new ArrayList<>());
            xmlMaterialRegistrar.setBottomClothList(new ArrayList<>());
            xmlMaterialRegistrar.setAccessoryList(new ArrayList<>());

            logger.info("ARC-CONVERT", "purple[italic[Starting conversion of materials]]");
            for (com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory accessory :
                materialRegistrar.getAccessoryList()) {

                logger.debug("ARC-CONVERT", STR."Converting material with id \{accessory.getKey()}");
                Accessory xmlAccessory = new Accessory();
                xmlAccessory.setCommonness(accessory.getRarity());
                xmlAccessory.setItems(accessory.getMaterial());
                xmlAccessory.setkeyentifyer(accessory.getKey());

                xmlMaterialRegistrar.getAccessoryList().add(xmlAccessory);
            }
            for (com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth topCloth :
                materialRegistrar.getTopClothList()) {

                logger.debug("ARC-CONVERT", STR."Converting material with id \{topCloth.getKey()}");
                TopCloth xmlTopCloth = new TopCloth(
                    topCloth.getRarity(),
                    topCloth.getMaterial(),
                    topCloth.getKey()
                );

                xmlMaterialRegistrar.getTopClothList().add(xmlTopCloth);
            }
            for (com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth bottomCloth :
                materialRegistrar.getBottomClothList()) {

                logger.debug("ARC-CONVERT", STR."Converting material with id \{bottomCloth.getKey()}");
                BottomCloth xmlBottomCloth = new BottomCloth(
                    bottomCloth.getRarity(),
                    bottomCloth.getMaterial(),
                    bottomCloth.getKey()
                );

                xmlMaterialRegistrar.getBottomClothList().add(xmlBottomCloth);
            }
            for (com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe shoe :
                materialRegistrar.getShoesList()) {

                logger.debug("ARC-CONVERT", STR."Converting material with id \{shoe.getKey()}");
                com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe xmlShoe
                    = new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe(
                    shoe.getRarity(),
                    shoe.getMaterial(),
                    shoe.getKey()
                );

                xmlMaterialRegistrar.getShoesList().add(xmlShoe);
            }
            logger.info("ARC-CONVERT", "purple[Saving converted materials to file...]");
            xmlManager.saveToXMLAsync(xmlMaterialRegistrar, "./ServerData/XML/registeredMaterials.xml");

            logger.info("ARC-CONVERT", "purple[italic[Starting conversion of families]]");
            Map<String, Family> playerToFamilyMap = new HashMap<>();
            for (com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family family :
                familyRegistrar.getFamilies()) {

                logger.debug("ARC-CONVERT", STR."Converting family \{family.getFamilyNames().getFamilyName()}");
                Family xmlFamily = new Family();
                xmlFamily.setFamilyMembers(new ArrayList<>());
                xmlFamily.setFamilyName(family.getFamilyName());
                xmlFamily.setFamilySize(family.getFamilySize());
                xmlFamily.setFamilyWealth(family.getFamilyWealth());

                for (com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.User user :
                    family.getFamilyMembers()) {

                    logger.debug("ARC-CONVERT", STR."Converting family member \{user.getUsername()}");
                    com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.MinimalUser xmlUser
                        = new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.MinimalUser();
                    xmlUser.setFirstname(user.getFirstname());
                    xmlUser.setLastname(user.getLastname());
                    xmlUser.setUsername(user.getUsername());

                    xmlFamily.getFamilyMembers().add(xmlUser);
                    playerToFamilyMap.put(user.getUsername(), xmlFamily);
                }

                xmlFamilyRegistrar.getFamilies().add(xmlFamily);
            }
            logger.info("ARC-CONVERT", "purple[Saving converted families to file...]");
            xmlManager.saveToXMLAsync(xmlFamilyRegistrar, "./ServerData/XML/registeredFamilies.xml");

            logger.info("ARC-CONVERT", "purple[italic[Starting conversion of players]]");
            for (com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.User user :
                playerRegistrar.getUsers()) {

                User xmlUser = new User();
                xmlUser.setEmail(user.getEmail());
                xmlUser.setFirstname(user.getFirstname());
                xmlUser.setGender(user.getGender());
                xmlUser.setLastname(user.getLastname());
                xmlUser.setPassword(user.getPassword());
                xmlUser.setUsername(user.getUsername());

                xmlPlayerRegistrar.getUsers().add(xmlUser);

                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.Player player =
                    databaseManager.getEntityById(
                        com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.Player.class,
                        user.getUsername()
                    );
                List<Accessory> collectedAccessories = new ArrayList<>();
                List<Accessory> currentAccessories = new ArrayList<>();
                List<TopCloth> collectedTopCloth = new ArrayList<>();
                List<BottomCloth> collectedBottomCloth = new ArrayList<>();
                List<com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe> collectedShoes = new ArrayList<>();

                for (com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory accessory :
                    player.getCollectedAccessories()) {

                    logger.debug("ARC-CONVERT", STR."Converting player collected accessory with id \{accessory.getKey()}");
                    Accessory xmlAccessory = new Accessory();
                    xmlAccessory.setkeyentifyer(accessory.getKey());
                    xmlAccessory.setCommonness(accessory.getRarity());
                    xmlAccessory.setItems(accessory.getMaterial());

                    collectedAccessories.add(xmlAccessory);
                }
                for (com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory accessory :
                    player.getCurrentAccessories()) {

                    logger.debug("ARC-CONVERT", STR."Converting worn player accessory with id \{accessory.getKey()}");
                    Accessory xmlAccessory = new Accessory();
                    xmlAccessory.setkeyentifyer(accessory.getKey());
                    xmlAccessory.setCommonness(accessory.getRarity());
                    xmlAccessory.setItems(accessory.getMaterial());

                    currentAccessories.add(xmlAccessory);
                }
                for (com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth topCloth :
                    player.getCollectedTopCloth()) {

                    logger.debug("ARC-CONVERT", STR."Converting collected player topcloth with id \{topCloth.getKey()}");
                    TopCloth xmlTopCloth = new TopCloth(
                        topCloth.getRarity(),
                        topCloth.getMaterial(),
                        topCloth.getKey()
                    );

                    collectedTopCloth.add(xmlTopCloth);
                }
                for (com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth bottomCloth :
                    player.getCollectedBottomCloth()) {

                    logger.debug("ARC-CONVERT", STR."Converting collected player bottomcloth with id \{bottomCloth.getKey()}");
                    BottomCloth xmlBottomCloth = new BottomCloth(
                        bottomCloth.getRarity(),
                        bottomCloth.getMaterial(),
                        bottomCloth.getKey()
                    );

                    collectedBottomCloth.add(xmlBottomCloth);
                }
                for (com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe shoe :
                    player.getCollectedShoes()) {

                    logger.debug("ARC-CONVERT", STR."Converting collected player topcloth with id \{shoe.getKey()}");
                    com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe xmlShoe =
                        new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe(
                            shoe.getRarity(),
                            shoe.getMaterial(),
                            shoe.getKey()
                        );

                    collectedShoes.add(xmlShoe);
                }
                com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopClothes xmlCurrentTopCloth =
                    new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopClothes(
                        new TopCloth(
                            player.getCurrentTopCloth().getRarity(),
                            player.getCurrentTopCloth().getMaterial(),
                            player.getCurrentTopCloth().getKey()
                        )
                    );
                BottomClothes xmlCurrentBottomCloth = new BottomClothes(
                    new BottomCloth(
                        player.getCurrentBottomCloth().getRarity(),
                        player.getCurrentBottomCloth().getMaterial(),
                        player.getCurrentBottomCloth().getKey()
                    )
                );
                com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoes xmlCurrentShoe =
                    new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoes(
                        new com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe(
                            player.getCurrentShoe().getRarity(),
                            player.getCurrentShoe().getMaterial(),
                            player.getCurrentShoe().getKey()
                        )
                    );
                Family xmlFamily = playerToFamilyMap.get(user.getUsername());
                PlayerFamily xmlPlayerFamily = new PlayerFamily(xmlFamily);
                xmlPlayerFamily.setFamilyPosition(xmlFamily.getFamilySize());

                Player xmlPlayer =
                    new FileBasedPlayerBuilder()
                        .setCollectedAccessoriesXml(collectedAccessories)
                        .setCollectedBottomClothXml(collectedBottomCloth)
                        .setCollectedTopClothXml(collectedTopCloth)
                        .setCollectedShoesXml(collectedShoes)
                        .setCurrentAccessoriesXml(currentAccessories)
                        .setCurrentBottomClothXml(xmlCurrentBottomCloth)
                        .setCurrentTopClothXml(xmlCurrentTopCloth)
                        .setcurrentshoexml(xmlCurrentShoe)
                        .setSkinHue(player.getSkinHue())
                        .setIrisHue(player.getIrisHue())
                        .setHairHue(player.getHairHue())
                        .setEyeLashHue(player.getEyeLashHue())
                        .setHairType(player.getHairType())
                        .setEyeLashType(player.getEyeLashType())
                        .setFamilyXml(xmlPlayerFamily)
                        .buildXml();

                xmlManager.saveToXMLAsync(xmlPlayer, STR."./ServerData/XML/Users/\{user.getUsername()}.xml");
            }

            xmlManager.saveToXMLAsync(xmlPlayerRegistrar, "./ServerData/XML/registeredUsers.xml");

            if (createBackup) {
                boolean fullySuccessful = createBackup("sqlite", flushFiles);
                if (fullySuccessful) {
                    logger.success("ARC-CONVERTER", "Conversion complete");
                }else {
                    logger.warning("ARC-CONVERTER", "Conversion complete but backup failed. Please check logs");
                }
            }

            return true;
        }
        catch (Exception e) {
            logger.severe("ARC-CONVERT", STR."Conversion failed \{e}");
            return false;
        }
    }

    private static @NotNull String generateSessionId() {
        return UUID.randomUUID().toString(); // Combine timestamp and UUID for uniqueness
    }
    private static boolean createBackup(@NotNull String dataFormat, boolean flush) throws JAXBException {
        if (dataFormat.equalsIgnoreCase("xml") || dataFormat.equalsIgnoreCase("json")) {
            String currentDataFormatU = dataFormat.toUpperCase();
            String currentDataFormatL = dataFormat.toLowerCase();
            String sourceFolder = StringTemplate.STR."./ServerData/\{currentDataFormatU}";
            final String backupId = generateSessionId();
            String finalFolder = StringTemplate.STR.
                "./ServerData/backups/\{currentDataFormatU}/\{currentDataFormatL}-backup-\{backupId}.zip";
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

            boolean compressionSuccessful = compressFolder(sourceFolder, finalFolder);
            if (compressionSuccessful) {

                XmlConfigManager manager = new XmlConfigManager(BackupManager.class, Backup.class);
                logger.success("ARC-BACKUP", "Folder compression was successful");
                BackupManager backupManager = getOrCreateBackupRegistrar(manager);
                Backup backup = new Backup.BackupBuilder()
                    .setBackupId(backupId)
                    .setBackupPath(finalFolder)
                    .setBackupSize(new File(finalFolder).length())
                    .setBackupType(BackupType.valueOf(currentDataFormatU))
                    .setDate(timestamp)
                    .build();
                backupManager.getBackup().add(backup);
                manager.saveToXMLAsync(backupManager, "./ServerData/backups/registeredBackups.xml");
                if (flush) {
                    boolean flushed = deleteFolder(sourceFolder);
                    if (flushed) {
                        logger.success("ARC-BACKUP", "Folder deletion was successful");
                        return true;
                    } else {
                        logger.severe("ARC-BACKUP", "Folder deletion was unsuccessful");
                        return false;
                    }
                } else {
                    return true;
                }
            } else {
                logger.severe("ARC-BACKUP", "Folder compression was unsuccessful");
                return false;
            }
        }
        else if (dataFormat.equalsIgnoreCase("sqlite")) {
            String currentDataFormatU = dataFormat.toUpperCase();
            String currentDataFormatL = dataFormat.toLowerCase();
            String sourceFolder = "./ServerData/databases";
            final String backupId = generateSessionId();
            String finalFolder = StringTemplate.STR.
                "./ServerData/backups/\{currentDataFormatU}/\{currentDataFormatL}-backup-\{backupId}.zip";
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

            boolean compressionSuccessful = compressFolder(sourceFolder, finalFolder);
            if (compressionSuccessful) {

                XmlConfigManager manager = new XmlConfigManager(BackupManager.class, Backup.class);
                logger.success("ARC-BACKUP", "Folder compression was successful");
                BackupManager backupManager = getOrCreateBackupRegistrar(manager);
                Backup backup = new Backup.BackupBuilder()
                    .setBackupId(backupId)
                    .setBackupPath(finalFolder)
                    .setBackupSize(new File(finalFolder).length())
                    .setBackupType(BackupType.SQLITE)
                    .setDate(timestamp)
                    .build();
                backupManager.getBackup().add(backup);
                manager.saveToXMLAsync(backupManager, "./ServerData/backups/registeredBackups.xml");
                if (flush) {
                    boolean flushed = deleteFolder(sourceFolder);
                    if (flushed) {
                        logger.success("ARC-BACKUP", "Folder deletion was successful");
                        return true;
                    } else {
                        logger.severe("ARC-BACKUP", "Folder deletion was unsuccessful");
                        return false;
                    }
                }
                else {
                    return true;
                }
            } else {
                logger.severe("ARC-BACKUP", "Folder compression was unsuccessful");
                return false;
            }
        }
        else {
            logger.error("ARC-BACKUP", STR."Unknown backup format \{dataFormat}");
            return false;
        }
    }
    private static BackupManager getOrCreateBackupRegistrar(XmlConfigManager manager) throws JAXBException {
        File registrarFile = new File("./ServerData/backups/registeredBackups.xml");
        if (registrarFile.exists()) {
            logger.info("ARC-XML", "Using provided backup registrar found at def location");
            return manager.loadFromXML(registrarFile.getPath(), BackupManager.class);
        }
        logger.info("ARC-XML", "No Backup registrar found. Creating new backup registrar");
        BackupManager newRegistrar = new BackupManager();
        newRegistrar.setBackup(new ArrayList<>());
        return newRegistrar;
    }
    private static MaterialRegistrar getOrCreateMaterialRegistrar(XmlConfigManager manager) throws JAXBException {
        File registrarFile = new File("./ServerData/XML/registeredMaterials.xml");
        if (registrarFile.exists()) {
            MaterialRegistrar registrar = manager.loadFromXML(registrarFile.getPath(), MaterialRegistrar.class);
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
        MaterialRegistrar newRegistrar = new MaterialRegistrar();
        newRegistrar.setAccessoryList(new ArrayList<>());
        newRegistrar.setBottomClothList(new ArrayList<>());
        newRegistrar.setShoesList(new ArrayList<>());
        newRegistrar.setTopClothList(new ArrayList<>());
        return newRegistrar;
    }
    private static com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.MaterialRegistrar getOrCreateJsonMaterialRegistrar(JsonConfigManager manager) throws IOException {
        File registrarFile = new File("./ServerData/JSON/registeredMaterials.json");
        if (registrarFile.exists()) {
            com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.MaterialRegistrar registrar = manager.loadFromJSON(registrarFile.getPath(), com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.MaterialRegistrar.class);
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
        com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.MaterialRegistrar newRegistrar = new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.MaterialRegistrar();
        newRegistrar.setAccessoryList(new ArrayList<>());
        newRegistrar.setBottomClothList(new ArrayList<>());
        newRegistrar.setShoesList(new ArrayList<>());
        newRegistrar.setTopClothList(new ArrayList<>());
        return newRegistrar;
    }
}
