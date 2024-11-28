package com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities;

import com.arcvad.schoolquest.server.server.ARCServer;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.JsonConfigManager;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.Player;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.PlayerRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.User;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoe;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth;
import com.arcvad.schoolquest.server.server.Playerutils.Genders;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

public class JsonUtils {

    public static void handleRequestUser(WebSocket conn, String message) {
        logger.info("ARC-SOCKET", "Received player request packet...");

        String player = "";
        String password = "";

        String currentDir = null;
        try {
            currentDir = new File(ARCServer.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String regex = "requestUser->\\{([^}]+)}\\{([^}]+)}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            player = matcher.group(1);
            password = matcher.group(2);

            try {
                JsonConfigManager manager = new JsonConfigManager();
                Map<String, Object> combinedAttributes = new HashMap<>();
                List<User> users = manager.loadFromJSON("./ServerData/XML/registeredUsers.xml", PlayerRegistrar.class).getUsers();

                for (User user : users) {
                    if (user.getEmail().equals(player)) {
                        if (user.getPassword().equals(password)) {
                            Player registeredPlayer = manager.loadFromJSON("./ServerData/XML/Users/" + player + ".xml", Player.class);
                            combinedAttributes.put("username", user.getUsername());
                            combinedAttributes.put("password", user.getPassword());
                            combinedAttributes.put("email", user.getEmail());
                            combinedAttributes.put("firstname", user.getFirstname());
                            combinedAttributes.put("lastname", user.getLastname());
                            combinedAttributes.put("gender", user.getGender());

                            combinedAttributes.put("eyeLashStyle", registeredPlayer.getEyeLashDesign());
                            combinedAttributes.put("eyeLashColor", registeredPlayer.getEyelashColor());
                            combinedAttributes.put("hairStyle", registeredPlayer.getHairType());
                            combinedAttributes.put("hairColor", registeredPlayer.getHairHue());
                            combinedAttributes.put("eyeColor", registeredPlayer.getIrisHue());
                            combinedAttributes.put("skinColor", registeredPlayer.getSkinHue());
                            combinedAttributes.put("topCloth", registeredPlayer.getFirstLayerCloth());
                            combinedAttributes.put("bottomCloth", registeredPlayer.getSecondLayerCloth());
                            combinedAttributes.put("shoe", registeredPlayer.getFootwear());

                            List<TopCloth> ownedTopClothes = new ArrayList<>(registeredPlayer.getCollectedUpperWear());
                            List<BottomCloth> ownedBottomClothes = new ArrayList<>(registeredPlayer.getCollectedLowerWear());
                            List<Shoe> ownedShoes = new ArrayList<>(registeredPlayer.getCollectedFootwear());
                            List<Accessory> ownedAccessories = new ArrayList<>(registeredPlayer.getCollectedAdornments());
                            List<Accessory> wornAccessories = new ArrayList<>(registeredPlayer.getAdornments());

                            combinedAttributes.put("ownedTopClothes", ownedTopClothes);
                            combinedAttributes.put("ownedBottomClothes", ownedBottomClothes);
                            combinedAttributes.put("ownedShoes", ownedShoes);
                            combinedAttributes.put("ownedAccessory", ownedAccessories);
                            combinedAttributes.put("accessories", wornAccessories);

                            combinedAttributes.put("familyName", registeredPlayer.getFamily().getFamilyName());
                            combinedAttributes.put("familyWealth", registeredPlayer.getFamily().getFamilyWealth());
                            combinedAttributes.put("familyPosition", registeredPlayer.getFamily().getFamilyPosition());

                            ObjectMapper mapper = new ObjectMapper();

                            try {
                                String jsonString = mapper.writeValueAsString(combinedAttributes);
                                conn.send("playerDataResponse: " + jsonString);
                                logger.debug("ARC-SOCKET", "purple[PARSED PLAYER-JSON:] ||->  " + jsonString + "  <-|| for player: " + player);
                            } catch (JsonProcessingException e) {
                                logger.error("ARC-SOCKET", "red[Error creating data response: ]" + e.getCause());
                                conn.send("playerDataResponse: null");
                            }
                        } else {
                            logger.info("ARC-USER", "Tried to get User but password was wrong");
                        }
                    } else {
                        conn.send("err");
                        logger.warning("ARC-USER", StringTemplate.STR."Username \{player} not found in registered_users.xml");
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //how to convert string to boolean
        } else {
            System.out.println("Failed to match pattern in message: " + message);
        }
    }

    public static void handleRegisterUser(WebSocket conn, String message) {
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

            try {
                JsonConfigManager manager = new JsonConfigManager();
                List<User> users = manager.loadFromJSON("./ServerData/XML/registeredUsers.xml", PlayerRegistrar.class).getUsers();

                // Check if username or email already exists
                boolean usernameExists = false;
                boolean emailExists = false;

                for (User mainUser : users) {
                    if (mainUser.getUsername().equals(username)) {
                        usernameExists = true;
                        break;  // Exit loop once username is found
                    }
                    if (mainUser.getEmail().equals(email)) {
                        emailExists = true;
                        break;  // Exit loop once email is found
                    }
                }

                if (usernameExists) {
                    conn.send("createdP:false:Username already exists");
                } else if (emailExists) {
                    conn.send("createdP:false:Email already exists");
                } else {
                    AtomicBoolean playerCreated = new AtomicBoolean(false);
                    manager.createUserAsync(username, password, email, firstname, lastname, gender,
                        playerCreated::set);
                    if (playerCreated.get()) {
                        try {
                            Thread.sleep(500);
                            // Reload user data after creation
                            List<User> savedUsers = manager.loadFromJSON("./ServerData/XML/registeredUsers.xml", PlayerRegistrar.class).getUsers();
                            boolean foundUser = false;

                            for (User user : savedUsers) {
                                if (user.getUsername().equals(username)) {
                                    Player registeredPlayer = manager.loadFromJSON("./ServerData/XML/Users/" + username + ".xml", Player.class);

                                    // Combine user and player attributes
                                    combinedAttributes.put("username", user.getUsername());
                                    combinedAttributes.put("password", user.getPassword());
                                    combinedAttributes.put("email", user.getEmail());
                                    combinedAttributes.put("firstname", user.getFirstname());
                                    combinedAttributes.put("lastname", user.getLastname());
                                    combinedAttributes.put("gender", user.getGender());

                                    // Add player attributes
                                    combinedAttributes.put("eyeLashStyle", registeredPlayer.getEyeLashDesign());
                                    combinedAttributes.put("eyeLashColor", registeredPlayer.getEyelashColor());
                                    combinedAttributes.put("hairStyle", registeredPlayer.getHairType());
                                    combinedAttributes.put("hairColor", registeredPlayer.getHairHue());
                                    combinedAttributes.put("eyeColor", registeredPlayer.getIrisHue());
                                    combinedAttributes.put("skinColor", registeredPlayer.getSkinHue());
                                    combinedAttributes.put("topCloth", registeredPlayer.getFirstLayerCloth());
                                    combinedAttributes.put("bottomCloth", registeredPlayer.getSecondLayerCloth());
                                    combinedAttributes.put("shoe", registeredPlayer.getFootwear());

                                    // Owned items
                                    List<TopCloth> ownedTopClothes = new ArrayList<>(registeredPlayer.getCollectedUpperWear());
                                    List<BottomCloth> ownedBottomClothes = new ArrayList<>(registeredPlayer.getCollectedLowerWear());
                                    List<Shoe> ownedShoes = new ArrayList<>(registeredPlayer.getCollectedFootwear());
                                    List<Accessory> ownedAccessories = new ArrayList<>(registeredPlayer.getCollectedAdornments());
                                    List<Accessory> wornAccessories = new ArrayList<>(registeredPlayer.getAdornments());

                                    combinedAttributes.put("ownedTopClothes", ownedTopClothes);
                                    combinedAttributes.put("ownedBottomClothes", ownedBottomClothes);
                                    combinedAttributes.put("ownedShoes", ownedShoes);
                                    combinedAttributes.put("ownedAccessory", ownedAccessories);
                                    combinedAttributes.put("accessories", wornAccessories);

                                    combinedAttributes.put("familyName", registeredPlayer.getFamily().getFamilyName());
                                    combinedAttributes.put("familyWealth", registeredPlayer.getFamily().getFamilyWealth());
                                    combinedAttributes.put("familyPosition", registeredPlayer.getFamily().getFamilyPosition());

                                    // Convert to JSON and send back
                                    ObjectMapper mapper = new ObjectMapper();
                                    try {
                                        String jsonString = mapper.writeValueAsString(combinedAttributes);
                                        conn.send("playerDataResponse: " + jsonString);
                                        conn.send("createdP:true:User registered successfully");
                                        logger.debug("ARC-SOCKET", "purple[PARSED PLAYER-JSON:] ||->  " + jsonString + "  <-|| for player: " + username);
                                    } catch (JsonProcessingException e) {
                                        logger.error("ARC-SOCKET", "red[Error creating data response:] " + e.getCause() + " " + e.getMessage());
                                        conn.send("createdP:false:JsonProcessingException on server end");
                                    }
                                    foundUser = true;
                                    break;
                                }
                            }
                            if (!foundUser) {
                                logger.error("ARC-SOCKET", "yellow[Username " + username + " not found in registered_users.xml]");
                                conn.send("createdP:false:Exception gotten from server end");
                            }
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        conn.send("createdP:false:Server failed to create player...Please try again later. If issue persists report this to a server admin");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }

    public static void checkResults(boolean isAsyncCreated, boolean isSyncCreated) {
        if (isAsyncCreated && isSyncCreated) {
            logger.info("ARC-USER", "Default asynchronous and synchronous users were created and saved.");
        } else if (isAsyncCreated && !isSyncCreated) {
            logger.warning("ARC-USER", "Default asynchronous user was created but failed to create sync user.");
            logger.warning("ARC-USER", "There might be a server issue. check server logs.");
        } else if (!isSyncCreated && isAsyncCreated) {
            logger.warning("ARC-USER", "Default synchronous user was created but failed to create async user.");
            logger.warning("ARC-USER", "There might be a server issue. check server logs.");
        } else {
            logger.warning("ARC-USER", "Failed to create both async and sync users. They might already exist or there might be a server issue. check server logs");
        }
    }
}
