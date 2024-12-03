package com.arcvad.schoolquest.server.server.GlobalUtils.Entiies;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.JsonConfigManager;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.SQLManager;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.HibernateDatabaseManager;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.MaterialRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.PlayerFamily;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.Player;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.PlayerRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.User;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.XmlConfigManager;
import jakarta.xml.bind.JAXBException;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

public class PlayerCreator {
    public static boolean createSQLPlayer(TransactionUser user) {
        try {
            SQLManager.createDatabase();

            HibernateDatabaseManager manager = new HibernateDatabaseManager();

            if(!manager.entityExists(com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.User.class,
                "test_user1_sql")) {
                boolean isDone = manager.createUser(user);
                if (!isDone) {
                    logger.error("ARC-SQL", "Failed to create user");
                    return false;
                }
            }
            return true;
        }catch (Exception e) {
            logger.error("ARC-SQL", STR."Encountered error while creating SQLPlayer: \{e}");
        }
        return false;
    }
    public static boolean createXMLPlayer(TransactionUser user){
        try {
            logger.info("ARC-PLAYER", "Creating new XMLPlayer");
            XmlConfigManager manager = new XmlConfigManager(
                Player.class, PlayerFamily.class, User.class,
                Accessory.class, TopCloth.class, BottomCloth.class, Shoe.class,
                MaterialRegistrar.class, PlayerRegistrar.class
            );

            boolean isDone = manager.createUser(user);
            if(!isDone){
                logger.error("ARC-XML", "Failed to create user");
                return false;
            }
            return true;
        }catch (JAXBException e){
            logger.error("ARC-XML", STR."Encountered exception in user creation: \{e}");
            return false;
        }
    }
    public static boolean createJsonPlayer(TransactionUser user){
        try {
            logger.info("ARC-PLAYER", "Creating new JSONPlayer");
            JsonConfigManager manager = new JsonConfigManager();

            boolean isDone = manager.createUser(user);
            if(!isDone){
                logger.error("ARC-JSON", "Failed to create user");
                return false;
            }
            return true;
        }catch (Exception e){
            logger.error("ARC-JSON", STR."Encountered exception in user creation: \{e}");
            return false;
        }
    }
}


