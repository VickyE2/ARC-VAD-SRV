package com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities;

import com.arcvad.schoolquest.server.server.DataFormat.SQL.SQLManager;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.Player;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.User;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth;
import com.arcvad.schoolquest.server.server.GlobalUtils.Entiies.TransactionUser;
import com.arcvad.schoolquest.server.server.Playerutils.Genders;
import com.arcvad.schoolquest.server.server.Playerutils.UUIDGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

public class SqlUtils {
    public static void handleRequestUser(WebSocket conn, String message){
        logger.info("ARC-SOCKET", "Received player request packet...");

        String player = "";
        String password = "";

        String regex = "requestUser->\\{([^}]+)}\\{([^}]+)}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            player = matcher.group(1);
            password = matcher.group(2);

            try{
                SQLManager.createDatabase();

                HibernateDatabaseManager databaseManager = new HibernateDatabaseManager();


                Map<String, Object> combinedAttributes = new HashMap<>();

                boolean isPlayerExists = databaseManager.entityExists(User.class, player);
                if (isPlayerExists) {
                    User sqlUser = databaseManager.getEntityById(User.class, player);
                    if(sqlUser.getPassword().equals(password)){
                        Player sqlPlayer = databaseManager.getEntityById(Player.class, UUIDGenerator.generateUUIDFromString(sqlUser.getUsername()).toString());
                        combinedAttributes.put("username", sqlUser.getUsername());
                        combinedAttributes.put("password", sqlUser.getPassword());
                        combinedAttributes.put("email", sqlUser.getEmail());
                        combinedAttributes.put("firstname", sqlUser.getFirstname());
                        combinedAttributes.put("lastname", sqlUser.getLastname());
                        combinedAttributes.put("gender", sqlUser.getGender());

                        combinedAttributes.put("eyeLashStyle", sqlPlayer.getEyeLashType());
                        combinedAttributes.put("eyeLashColor", sqlPlayer.getEyeLashHue());
                        combinedAttributes.put("hairStyle", sqlPlayer.getHairType());
                        combinedAttributes.put("hairColor", sqlPlayer.getHairHue());
                        combinedAttributes.put("eyeColor", sqlPlayer.getIrisHue());
                        combinedAttributes.put("skinColor", sqlPlayer.getSkinHue());
                        combinedAttributes.put("topCloth", sqlPlayer.getCurrentTopCloth());
                        combinedAttributes.put("bottomCloth", sqlPlayer.getCurrentBottomCloth());
                        combinedAttributes.put("shoe", sqlPlayer.getCurrentShoe());

                        List<TopCloth> ownedTopClothes = new ArrayList<>(sqlPlayer.getCollectedTopCloth());
                        List<BottomCloth> ownedBottomClothes = new ArrayList<>(sqlPlayer.getCollectedBottomCloth());
                        List<Shoe> ownedShoes = new ArrayList<>(sqlPlayer.getCollectedShoes());
                        List<Accessory> ownedAccessories = new ArrayList<>(sqlPlayer.getCollectedAccessories());
                        List<Accessory> wornAccessories = new ArrayList<>(sqlPlayer.getCurrentAccessories());

                        combinedAttributes.put("ownedTopClothes", ownedTopClothes);
                        combinedAttributes.put("ownedBottomClothes", ownedBottomClothes);
                        combinedAttributes.put("ownedShoes", ownedShoes);
                        combinedAttributes.put("ownedAccessory", ownedAccessories);
                        combinedAttributes.put("accessories", wornAccessories);

                        combinedAttributes.put("familyName", sqlUser.getFamily().getFamilyName());
                        combinedAttributes.put("familyWealth", sqlUser.getFamily().getFamilyWealth());
                        combinedAttributes.put("familyPosition", sqlUser.getFamilyPosition());

                        ObjectMapper mapper = new ObjectMapper();

                        try {
                            String jsonString = mapper.writeValueAsString(combinedAttributes);
                            conn.send("playerDataResponse: " + jsonString);
                            logger.debug("ARC-SOCKET", "purple[PARSED PLAYER-JSON:] ||->  " + jsonString + "  <-|| for player: " + player);
                        } catch (JsonProcessingException e) {
                            logger.error("ARC-SOCKET", "red[Error creating data response: ]" + e.getCause());
                            conn.send("playerDataResponse: null");
                        }
                    }else{
                        conn.send("playerDataResponse:err->Wrong password");
                        logger.info("ARC-USER", "Tried to get User but password was wrong");
                    }
                }else{
                    conn.send("playerDataResponse:err->Player dosent exist");
                    logger.warning("ARC-USER", StringTemplate.STR."Username \{player} not found in registered_users.xml");

                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else {
            System.out.println("Failed to match pattern in message: " + message);
        }

    }
    public static void handleRegisterUser(WebSocket conn, String message) throws Exception {
        logger.info("ARC-SOCKET", "Recieved player register request...");

        String username = "";
        String password = "";
        String email = "";
        String firstname = "";
        String lastname = "";
        Genders gender = null;
        Map<String, Object> combinedAttributes = new HashMap<>();

        String regex = "registerUser->\\{([^}]+)}\\{([^}]+)}\\{([^}]+)}\\{([^}]+)}\\{([^}]+)}\\{([^}]+)}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            username = matcher.group(1);
            password = matcher.group(2);
            email = matcher.group(3);
            firstname = matcher.group(4);
            lastname = matcher.group(5);
            gender = Genders.valueOf(matcher.group(6));

            SQLManager.createDatabase();

            HibernateDatabaseManager databaseManager = new HibernateDatabaseManager();

            boolean usernameExists = databaseManager.entityExists(User.class, username);
            boolean emailExists = databaseManager.entityExistsByNaturalId(User.class, "email", email);

            if (usernameExists) {
                conn.send("createdP:false:Username already exists");
            }
            else if (emailExists) {
                conn.send("createdP:false:Email already exists");
            }
            else {
                TransactionUser tUser = new TransactionUser.TransactionUserBuilder()
                    .setEmail(email)
                    .setFirstname(firstname)
                    .setLastname(lastname)
                    .setPassword(password)
                    .setUsername(username)
                    .build();

                boolean userCreated = databaseManager.createUser(tUser);

                if (userCreated) {
                    User sqlUser = databaseManager.getEntityById(User.class, username);
                    Player sqlPlayer = databaseManager.getEntityById(Player.class, UUIDGenerator.generateUUIDFromString(sqlUser.getUsername()).toString());
                    combinedAttributes.put("username", sqlUser.getUsername());
                    combinedAttributes.put("password", sqlUser.getPassword());
                    combinedAttributes.put("email", sqlUser.getEmail());
                    combinedAttributes.put("firstname", sqlUser.getFirstname());
                    combinedAttributes.put("lastname", sqlUser.getLastname());
                    combinedAttributes.put("gender", sqlUser.getGender());

                    combinedAttributes.put("eyeLashStyle", sqlPlayer.getEyeLashType());
                    combinedAttributes.put("eyeLashColor", sqlPlayer.getEyeLashHue());
                    combinedAttributes.put("hairStyle", sqlPlayer.getHairType());
                    combinedAttributes.put("hairColor", sqlPlayer.getHairHue());
                    combinedAttributes.put("eyeColor", sqlPlayer.getIrisHue());
                    combinedAttributes.put("skinColor", sqlPlayer.getSkinHue());
                    combinedAttributes.put("topCloth", sqlPlayer.getCurrentTopCloth());
                    combinedAttributes.put("bottomCloth", sqlPlayer.getCurrentBottomCloth());
                    combinedAttributes.put("shoe", sqlPlayer.getCurrentShoe());

                    List<TopCloth> ownedTopClothes = new ArrayList<>(sqlPlayer.getCollectedTopCloth());
                    List<BottomCloth> ownedBottomClothes = new ArrayList<>(sqlPlayer.getCollectedBottomCloth());
                    List<Shoe> ownedShoes = new ArrayList<>(sqlPlayer.getCollectedShoes());
                    List<Accessory> ownedAccessories = new ArrayList<>(sqlPlayer.getCollectedAccessories());
                    List<Accessory> wornAccessories = new ArrayList<>(sqlPlayer.getCurrentAccessories());

                    combinedAttributes.put("ownedTopClothes", ownedTopClothes);
                    combinedAttributes.put("ownedBottomClothes", ownedBottomClothes);
                    combinedAttributes.put("ownedShoes", ownedShoes);
                    combinedAttributes.put("ownedAccessory", ownedAccessories);
                    combinedAttributes.put("accessories", wornAccessories);

                    combinedAttributes.put("familyName", sqlUser.getFamily().getFamilyName());
                    combinedAttributes.put("familyWealth", sqlUser.getFamily().getFamilyWealth());
                    combinedAttributes.put("familyPosition", sqlUser.getFamilyPosition());

                    ObjectMapper mapper = new ObjectMapper();

                    try {
                        String jsonString = mapper.writeValueAsString(combinedAttributes);
                        conn.send("playerDataResponse: " + jsonString);
                        logger.debug("ARC-SOCKET", "purple[PARSED PLAYER-JSON:] ||->  " + jsonString + "  <-|| for player: " + sqlUser.getUsername());
                    } catch (JsonProcessingException e) {
                        logger.error("ARC-SOCKET", "red[Error creating data response: ]" + e.getCause());
                        conn.send("playerDataResponse: null");
                    }
                }else {
                    conn.send("createdP:false:Exception gotten from server end");
                }
            }

        }
    }
}
