package com.arcvad.schoolquest.server.server;

import com.arcvad.schoolquest.server.server.Managers.XmlConfigManager;
import com.arcvad.schoolquest.server.server.Playerutils.Genders;
import com.arcvad.schoolquest.server.server.Templates.Entities.Player;
import com.arcvad.schoolquest.server.server.Templates.Entities.PlayerRegistrar;
import com.arcvad.schoolquest.server.server.Templates.Entities.User;
import com.arcvad.schoolquest.server.server.Templates.Wearables.Accessory.Accessory;
import com.arcvad.schoolquest.server.server.Templates.Wearables.BottomCloth.BottomCloth;
import com.arcvad.schoolquest.server.server.Templates.Wearables.Shoe.Shoe;
import com.arcvad.schoolquest.server.server.Templates.Wearables.Shoe.Shoes;
import com.arcvad.schoolquest.server.server.Templates.Wearables.TopCloth.TopCloth;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ARCServer extends WebSocketServer {
    public ARCServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
        conn.send("Successfully joined server"); // Send a welcome message
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
        System.out.println("Code: " + code + " Reason: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received request: " + message + " from client: " + conn);
        if (message.startsWith("Dad what's up?")) {
            conn.send("Im all good :]");
        }
        if (message.contains("requestUser")) {
            System.out.println("Received player request packet...");

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
                    XmlConfigManager manager = new XmlConfigManager(PlayerRegistrar.class);
                    Map<String, Object> combinedAttributes = new HashMap();
                    List<User> users = manager.loadFromXML("./ServerData/registeredUsers.xml", PlayerRegistrar.class).getUsers();

                    for (User user : users) {
                        if (user.getEmail().equals(player)) {
                            if (user.getPassword().equals(password)) {
                                Player registeredPlayer = manager.loadFromXML("./ServerData/Users/" + player + ".xml", Player.class);
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

                                ObjectMapper mapper = new ObjectMapper();

                                try {
                                    String jsonString = mapper.writeValueAsString(combinedAttributes);
                                    conn.send("playerDataResponse: " + jsonString);
                                    System.out.println("PARSED PLAYER-JSON: ||->  " + jsonString + "  <-|| for player: " + player);
                                } catch (JsonProcessingException e) {
                                    System.out.println("Error creating data response: " + e.getCause());
                                    conn.send("playerDataResponse: null");
                                }
                            } else {
                                System.out.println("Wrong Password...");
                            }
                        } else {
                            conn.send("err");
                            System.out.println("Username " + player + " not found in registered_users.xml");
                        }
                    }

                } catch (JAXBException e) {
                    throw new RuntimeException(e);
                }
                //how to convert string to boolean
            } else {
                System.out.println("Failed to match pattern in message: " + message);
            }
        } else if (message.startsWith("registerUser->")) {
            String username = "";
            String password = "";
            String email = "";
            String firstname = "";
            String lastname = "";
            Genders gender = null;
            Map<String, Object> combinedAttributes = new HashMap();

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
                    XmlConfigManager manager = new XmlConfigManager(Player.class, User.class, PlayerRegistrar.class, TopCloth.class, BottomCloth.class, Shoes.class, Shoe.class, Accessory.class);
                    List<User> users = manager.loadFromXML("./ServerData/registeredUsers.xml", PlayerRegistrar.class).getUsers();

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
                        // Proceed to create new user
                        boolean playerCreated = manager.createUser(username, password, email, firstname, lastname, gender);
                        if (playerCreated) {
                            try {
                                Thread.sleep(500);
                                // Reload user data after creation
                                List<User> savedUsers = manager.loadFromXML("./ServerData/registeredUsers.xml", PlayerRegistrar.class).getUsers();
                                boolean foundUser = false;

                                for (User user : savedUsers) {
                                    if (user.getUsername().equals(username)) {
                                        Player registeredPlayer = manager.loadFromXML("./ServerData/Users/" + username + ".xml", Player.class);

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

                                        // Convert to JSON and send back
                                        ObjectMapper mapper = new ObjectMapper();
                                        try {
                                            String jsonString = mapper.writeValueAsString(combinedAttributes);
                                            conn.send("playerDataResponse: " + jsonString);
                                            conn.send("createdP:true:User registered successfully");
                                            System.out.println("PARSED PLAYER-JSON: ||->  " + jsonString + "  <-|| for player: " + username);
                                        } catch (JsonProcessingException e) {
                                            System.out.println("Error creating data response: " + e.getCause() + " " + e.getMessage());
                                            conn.send("createdP:false:JsonProcessingException on server end");
                                        }
                                        foundUser = true;
                                        break;
                                    }
                                }
                                if (!foundUser) {
                                    System.out.println("Username " + username + " not found in registered_users.xml");
                                    conn.send("createdP:false:Exception gotten from server end");
                                }
                            } catch (JAXBException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            conn.send("createdP:false:Server failed to create player...Please try again later. If issue persists report this to a server admin");
                        }
                    }
                } catch (JAXBException e) {
                    throw new RuntimeException(e);
                }


            }
        } else if (message.equals("ping")) {
            conn.send("PING");
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {

    }


    public static void main(String[] args) throws JAXBException {
        XmlConfigManager xmlConfigManager = new XmlConfigManager(Player.class, User.class, PlayerRegistrar.class, TopCloth.class, BottomCloth.class, Shoes.class, Accessory.class);
        boolean isCreated = xmlConfigManager.createUser("test1", "onlyifyouknewwhatitwas009!&", "testuser@test.com", "Robo", "Logic", Genders.MALE);

        int port = 55489;
        ARCServer server = new ARCServer(new InetSocketAddress(port));
        server.start();
        System.out.println("Server started on port: " + port + " with ip: " + server.getAddress());
        System.out.println("default user created? " + isCreated);
    }
}
