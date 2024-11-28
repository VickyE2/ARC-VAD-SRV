package com.arcvad.schoolquest.server.server.DataFormat.Converter;

import com.arcvad.schoolquest.server.server.DataFormat.SQL.SQLManager;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.FamilyRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.HibernateDatabaseManager;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.PlayerBuilder;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.MaterialRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.PlayerFamily;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.Player;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.PlayerRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.User;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoes;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.XmlConfigManager;
import com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities;

import java.util.ArrayList;
import java.util.List;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.server;

@SuppressWarnings("preview")
public class DataConverter {
    public static boolean convertToSql() {
        try {

            server.broadcast("ALERT:Server is undergoing data transfer...");
            server.stop();

            XmlConfigManager manager = new XmlConfigManager(Player.class, User.class, PlayerRegistrar.class, TopCloth.class, BottomCloth.class, Shoes.class, Accessory.class);

            SQLManager.createDatabase();
            HibernateDatabaseManager databaseManager = GlobalUtilities.databaseManager;

            PlayerRegistrar registrar = manager.loadFromXML("./ServerData/XML/registeredUsers.xml", PlayerRegistrar.class);
            for (User user : registrar.getUsers()) {
                Player player = manager.loadFromXML(StringTemplate.STR."./ServerData/XML/Users/\{user.getUsername()}.xml", Player.class);

                List<com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory> collectedAccessories = new ArrayList<>();
                for (Accessory accessory : player.getCollectedAdornments()) {
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory sqlAccessory =
                        new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory();
                    sqlAccessory.setMaterial(accessory.getItems());
                    sqlAccessory.setKey(accessory.getkeyentifyer());
                    sqlAccessory.setRarity(accessory.getCommonness());
                    collectedAccessories.add(sqlAccessory);
                }
                List<com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory> currentAccessories = new ArrayList<>();
                for (Accessory accessory : player.getAdornments()) {
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory sqlAccessory =
                        new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory();
                    sqlAccessory.setMaterial(accessory.getItems());
                    sqlAccessory.setKey(accessory.getkeyentifyer());
                    sqlAccessory.setRarity(accessory.getCommonness());
                    currentAccessories.add(sqlAccessory);
                }
                List<com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth> collectedTopCloth = new ArrayList<>();
                for (TopCloth topCloth : player.getCollectedUpperWear()) {
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth sqlTopCloth =
                        new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth(
                            topCloth.getrarity(),
                            topCloth.getMaterial(),
                            topCloth.getKey()
                        );
                    collectedTopCloth.add(sqlTopCloth);
                }
                List<com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth> collectedBottomCloth = new ArrayList<>();
                for (BottomCloth bottomCloth : player.getCollectedLowerWear()) {
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth sqlBottomCloth =
                        new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth(
                            bottomCloth.getrarity(),
                            bottomCloth.getMaterial(),
                            bottomCloth.getKey()
                        );
                    collectedBottomCloth.add(sqlBottomCloth);
                }

                List<com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe> collectedShoes = new ArrayList<>();
                for (com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe shoe : player.getCollectedFootwear()) {
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe sqlShoes =
                        new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe(shoe.getrarity(), shoe.getMaterial(), shoe.getKey());
                    collectedShoes.add(sqlShoes);
                }

                PlayerFamily family = player.getFamily();

                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family sqlFamily =
                    new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family(family.getFamily().getFamilyNames());

                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.Player sqlPlayer =
                    new PlayerBuilder()
                        .setFamily(sqlFamily)
                        .setSkinHue(player.getSkinHue())
                        .setHairHue(player.getIrisHue())
                        .setIrisHue(player.getIrisHue())
                        .setEyeLashHue(player.getEyelashColor())
                        .setHairType(player.getHairType())
                        .setEyeLashType(player.getEyeLashDesign())
                        .setCollectedAccessories(collectedAccessories)
                        .setCollectedTopCloth(collectedTopCloth)
                        .setCollectedBottomCloth(collectedBottomCloth)
                        .setCollectedShoes(collectedShoes)
                        .setCurrentAccessories(currentAccessories)
                        .setCurrentTopCloth(new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth(
                            player.getFirstLayerCloth().getWearable().getrarity(),
                            player.getFirstLayerCloth().getWearable().getMaterial(),
                            player.getFirstLayerCloth().getWearable().getKey()
                        ))
                        .setCurrentBottomCloth(new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth(
                            player.getSecondLayerCloth().getWearable().getrarity(),
                            player.getSecondLayerCloth().getWearable().getMaterial(),
                            player.getSecondLayerCloth().getWearable().getKey()
                        ))
                        .setCurrentShoe(new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe(
                            player.getFootwear().getWearable().getrarity(),
                            player.getFootwear().getWearable().getMaterial(),
                            player.getFootwear().getWearable().getKey()
                        ))
                        .build();

                FamilyRegistrar familyRegistrar = new FamilyRegistrar();
                familyRegistrar.getFamilies().add(sqlFamily);

                MaterialRegistrar materialRegistrar = manager.loadFromXML("./ServerData/XML/registeredMaterials.xml", MaterialRegistrar.class);
                List<Accessory> registeredAccessories = materialRegistrar.getAccessoryList();
                List<TopCloth> registeredTopCloth = materialRegistrar.getTopClothList();
                List<BottomCloth> registeredBottomCloth = materialRegistrar.getBottomClothList();
                List<Shoes> registeredShoes = materialRegistrar.getShoesList();

                for (Accessory accessory : registeredAccessories) {
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory registeredSqlAccessory
                        = new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory();

                    registeredSqlAccessory.setMaterial(accessory.getItems());
                    registeredSqlAccessory.setKey(accessory.getkeyentifyer());
                    registeredSqlAccessory.setRarity(accessory.getCommonness());

                    databaseManager.saveEntity(registeredSqlAccessory);
                }
                for (TopCloth topCloth : registeredTopCloth) {
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth registeredSqlTopCLoth
                        = new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth(
                        topCloth.getrarity(),
                        topCloth.getMaterial(),
                        topCloth.getKey()
                    );

                    databaseManager.saveEntity(registeredSqlTopCLoth);
                }
                for (BottomCloth bottomCloth : registeredBottomCloth) {
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth registeredSqlBottomCLoth
                        = new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth(
                        bottomCloth.getrarity(),
                        bottomCloth.getMaterial(),
                        bottomCloth.getKey()
                    );

                    databaseManager.saveEntity(registeredSqlBottomCLoth);
                }
                for (Shoes shoe : registeredShoes) {
                    com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe registeredSqlShoe
                        = new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe(
                        shoe.getWearable().getrarity(),
                        shoe.getWearable().getMaterial(),
                        shoe.getWearable().getKey()
                    );

                    databaseManager.saveEntity(registeredSqlShoe);
                }

                databaseManager.saveEntity(sqlFamily);
                databaseManager.saveEntity(familyRegistrar);
                databaseManager.saveEntity(sqlPlayer);
            }

            server.start();
            return true;
        }
        catch (Exception e){
            server.start();
            return false;
        }
    }

}
