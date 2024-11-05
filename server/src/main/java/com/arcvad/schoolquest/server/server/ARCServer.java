package com.arcvad.schoolquest.server.server;

import com.arcvad.schoolquest.server.server.Managers.XmlConfigManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.spongepowered.configurate.AttributedConfigurationNode;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        if (message.startsWith("Dad what's up?")){
            conn.send("Im all good :]");
        }
        if (message.contains("requestUser")) {
            System.out.println("Received player request packet...");

            String player = "";
            String password = "";

            String currentDir = System.getProperty("user.dir");
            String regex = "requestUser\\{([^}]+)}\\{([^}]+)}";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(message);

            if (matcher.find()){
                player = matcher.group(1);
                password = matcher.group(2);
            }

            // Load user attributes from registered_users.xml
            Path userFilePath = Paths.get(currentDir, "Clients/Users/registered_users.xml");
            XmlConfigManager xmlConfigManager = new XmlConfigManager();
            xmlConfigManager.createDefaultUser("test1", "onlyifyouknewwhatitwas009!&");
            xmlConfigManager.createConfig(userFilePath.toString());

            String userPath = "users.user";
            Map<String, Object> combinedAttributes = new HashMap<>(); // Use Object to allow lists as values

            if (xmlConfigManager.doesPathExist(userPath)) {
                Map<String, String> userAttributes = xmlConfigManager.getAttributes(userPath);

                // Verify that the username matches
                if (userAttributes != null && userAttributes.get("username").equals(player)) {
                    if (userAttributes.get("password").equals(password)) {
                        combinedAttributes.putAll(userAttributes); // Add user attributes

                        // Load player-specific data from player_name/data.xml
                        Path playerDataPath = Paths.get(currentDir, "Clients/Users/" + player + "/data.xml");
                        XmlConfigManager playerDataConfig = new XmlConfigManager();
                        playerDataConfig.createConfig(playerDataPath.toString());

                        String playerDataPathString = "playerData"; // Assume root node in data.xml is <playerData>
                        if (playerDataConfig.doesPathExist(playerDataPathString)) {
                            combinedAttributes.put("eyeLashStyle", playerDataConfig.getConfigValue("playerData.eyeLashStyle"));
                            combinedAttributes.put("eyeLashColor", playerDataConfig.getConfigValue("playerData.eyeLashColor"));
                            combinedAttributes.put("hairStyle", playerDataConfig.getConfigValue("playerData.hairStyle"));
                            combinedAttributes.put("hairColor", playerDataConfig.getConfigValue("playerData.hairColor"));
                            combinedAttributes.put("eyeColor", playerDataConfig.getConfigValue("playerData.eyeColor"));
                            combinedAttributes.put("skinColor", playerDataConfig.getConfigValue("playerData.skinColor"));
                            combinedAttributes.put("topCloth", playerDataConfig.getConfigValue("playerData.topCloth"));
                            combinedAttributes.put("bottomCloth", playerDataConfig.getConfigValue("playerData.bottomCloth"));
                            combinedAttributes.put("shoe", playerDataConfig.getConfigValue("playerData.shoe"));


                            List<Map<String, String>> topClothesList = new ArrayList<>();
                            List<Map<String, String>> bottomClothesList = new ArrayList<>();
                            List<Map<String, String>> shoesList = new ArrayList<>();
                            List<Map<String, String>> accessoryList = new ArrayList<>();

                            List<AttributedConfigurationNode> topPlayerClothes = playerDataConfig.getChildNodes(playerDataPathString + ".ownedItems.topClothes");
                            List<AttributedConfigurationNode> bottomPlayerClothes = playerDataConfig.getChildNodes(playerDataPathString + ".ownedItems.bottomClothes");
                            List<AttributedConfigurationNode> playerShoes = playerDataConfig.getChildNodes(playerDataPathString + ".ownedItems.shoes");
                            List<AttributedConfigurationNode> playerAccessories = playerDataConfig.getChildNodes(playerDataPathString + ".ownedItems.accessories");

                            // Collect top clothes attributes
                            for (AttributedConfigurationNode clothNode : topPlayerClothes) {
                                Map<String, String> clothAttributes = new HashMap<>();
                                clothAttributes.put("name", clothNode.attribute("name"));
                                clothAttributes.put("id", clothNode.attribute("id"));
                                topClothesList.add(clothAttributes);
                            }
                            combinedAttributes.put("topClothes", topClothesList);

                            // Collect bottom clothes attributes
                            for (AttributedConfigurationNode clothNode : bottomPlayerClothes) {
                                Map<String, String> clothAttributes = new HashMap<>();
                                clothAttributes.put("name", clothNode.attribute("name"));
                                clothAttributes.put("id", clothNode.attribute("id"));
                                bottomClothesList.add(clothAttributes);
                            }
                            combinedAttributes.put("bottomClothes", bottomClothesList);

                            // Collect shoes attributes
                            for (AttributedConfigurationNode shoeNode : playerShoes) {
                                Map<String, String> shoeAttributes = new HashMap<>();
                                shoeAttributes.put("name", shoeNode.attribute("name"));
                                shoeAttributes.put("id", shoeNode.attribute("id"));
                                shoesList.add(shoeAttributes);
                            }
                            combinedAttributes.put("shoes", shoesList);

                            for (AttributedConfigurationNode accessoryNode : playerAccessories) {
                                Map<String, String> accessoryAttributes = new HashMap<>();
                                accessoryAttributes.put("name", accessoryNode.attribute("name"));
                                accessoryAttributes.put("id", accessoryNode.attribute("id"));
                                accessoryList.add(accessoryAttributes);
                            }
                            combinedAttributes.put("accessories", accessoryList);
                        }

                        // Convert combined attributes to JSON and send
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            String jsonString = mapper.writeValueAsString(combinedAttributes);
                            conn.send("playerDataResponse: " + jsonString);
                            System.out.println("PARSED PLAYER-JSON: ||->  " + jsonString + "  <-|| for player: " + player);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        System.out.println("Wrong Password...");
                    }
                } else {
                    conn.send("err");
                    System.out.println("Username " + player + " not found in registered_users.xml");
                }
            }
        } else if (message.equals("ping")){
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

    public static void main(String[] args) {
        XmlConfigManager xmlConfigManager = new XmlConfigManager();
        xmlConfigManager.createDefaultUser("test1", "onlyifyouknewwhatitwas009!&");

        int port = 55489; // Choose any port you like
        ARCServer server = new ARCServer(new InetSocketAddress(port));
        server.start();
        System.out.println("Server started on port: " + port + " with ip: " + server.getAddress());
    }
}
