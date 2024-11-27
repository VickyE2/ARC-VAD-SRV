package com.arcvad.schoolquest.server.server;

import com.arcvad.schoolquest.server.server.Commands.CommandManager;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.SQLManager;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.HibernateDatabaseManager;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.Player;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.PlayerRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.User;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoes;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.XmlConfigManager;
import com.arcvad.schoolquest.server.server.GlobalUtils.AnsiLogger;
import com.arcvad.schoolquest.server.server.Playerutils.Genders;
import jakarta.xml.bind.JAXBException;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import picocli.CommandLine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.DefaultPlayerCreator.createDefaultUser;
import static com.arcvad.schoolquest.server.server.DataFormat.XML.utilities.XmlUtils.*;
import static com.arcvad.schoolquest.server.server.GlobalUtils.Config.getConfigValue;
import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;
import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.running;

@SuppressWarnings({"preview"})
public class ARCServer extends WebSocketServer {
    private final ConcurrentHashMap<WebSocket, PlayerData> players = new ConcurrentHashMap<>();
    public String mapPath = "/map_data/test_map.tmx";
    public InputStream mapInputStream = getClass().getResourceAsStream(mapPath);
    public static String server_data_format = getConfigValue("server_data_format").toString();

    public ARCServer(InetSocketAddress address) {
        super(address);
    }

    private String serialize(Object object) {
        // Convert object to JSON (use a library like Gson or Jackson)
        return new com.google.gson.Gson().toJson(object);
    }

    private <T> T deserialize(String json, Class<T> clazz) {
        // Convert JSON back to object
        return new com.google.gson.Gson().fromJson(json, clazz);
    }

    private void sendFileInChunks(WebSocket conn, String filePath) {
        File file = new File(filePath);
        long totalSize = file.length();
        int chunkSize = 0;
        if (totalSize < 4096) {
            chunkSize = (int) totalSize;
        } else if (totalSize > 4096 && totalSize < 20480) {
            chunkSize = 4096;
        } else if (totalSize > 20480 && totalSize < 1048576) {
            chunkSize = 20480;
        } else if (totalSize > 1048576) {
            chunkSize = 102400;
        }

        byte[] buffer = new byte[chunkSize];

        try (FileInputStream fis = new FileInputStream(file)) {
            long bytesSent = 0;
            int chunkNumber = 0;

            while (bytesSent < totalSize) {
                int bytesRead = fis.read(buffer);
                if (bytesRead == -1) break;

                // Create metadata
                String metadata = String.format("CHUNK %d %d %d\n", chunkNumber, bytesRead, totalSize);
                conn.send(metadata + new String(buffer, 0, bytesRead));

                bytesSent += bytesRead;
                chunkNumber++;
            }

            conn.send("TRANSFER_COMPLETE");
        } catch (IOException e) {
            e.printStackTrace();
            conn.send("TRANSFER_ERROR");
        }
    }

    private void sendInputStreamInChunks(WebSocket conn, InputStream inputStream) {
        // Determine chunk size based on totalSize
        long totalSize = new File(mapPath).length();

        int chunkSize;
        if (totalSize > 4096 && totalSize <= 20480) {
            chunkSize = 4096;
        } else if (totalSize > 20480 && totalSize <= 1048576) {
            chunkSize = 20480;
        } else if (totalSize > 1048576) {
            chunkSize = 102400;
        } else {
            chunkSize = (int) totalSize; // If size is smaller, send it in one chunk
        }

        byte[] buffer = new byte[chunkSize];

        try {
            long bytesSent = 0;
            int chunkNumber = 0;

            while (bytesSent < totalSize) {
                int bytesRead = inputStream.read(buffer);
                if (bytesRead == -1) break;

                // Create metadata
                String metadata = String.format("CHUNK %d %d %d\n", chunkNumber, bytesRead, totalSize);
                conn.send(metadata + new String(buffer, 0, bytesRead));

                bytesSent += bytesRead;
                chunkNumber++;
            }

            conn.send("TRANSFER_COMPLETE");
        } catch (IOException e) {
            e.printStackTrace();
            conn.send("TRANSFER_ERROR");
        } finally {
            try {
                inputStream.close(); // Ensure the InputStream is closed
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info("ARC-SOCKET", STR."New connection: \{conn.getRemoteSocketAddress()}");
        conn.send("Successfully joined server");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println(STR."Closed connection: \{conn.getRemoteSocketAddress()}");
        System.out.println(STR."Code: \{code} Reason: \{reason}");
        players.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        CompletableFuture.runAsync(() -> {
            System.out.println(STR."Received request: \{message} from client: \{conn}");
            if (message.equals("RequestMap")) {
                sendInputStreamInChunks(conn, mapInputStream);
            }
            if (message.startsWith("Movement:")) {
                handleMovementBroadcast(conn, message);
            }
            if (message.startsWith("Dad what's up?")) {
                conn.send("Im all good :]");
            }
            if (message.contains("requestUser")) {
                handleRequestUser(conn, message);
            }
            if (message.startsWith("registerUser->")) {
                handleRegisterUser(conn, message);
            } else if (message.equals("ping")) {
                conn.send("PING");
            }
        });
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.error("ARC-SOCKET", "red[Encountered server error during run: ]");
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        logger.info("ARC-SOCKET", "Server started successfully...");
    }

    public void handleMovementBroadcast(WebSocket conn, String message) {
        message = message.replace("Movement:", "");
        PlayerData data = deserialize(message, PlayerData.class);
        players.put(conn, data);
        broadcast(serialize(data));
    }


    public static void main(String[] args) throws JAXBException {
        try {
            System.setProperty("org.jline.terminal.debug", "true");
            System.setProperty("org.jline.reader.debug", "true");
            logger = new AnsiLogger((Boolean) getConfigValue("enable_logger_saving"), "ServerData/Logs/");

            logger.toast(
                """
                    purple[
                    ░█████╗░██████╗░░█████╗░░░░░░░░██████╗███████╗██████╗░██╗░░░██╗███████╗██████╗░
                    ██╔══██╗██╔══██╗██╔══██╗░░░░░░██╔════╝██╔════╝██╔══██╗██║░░░██║██╔════╝██╔══██╗
                    ███████║██████╔╝██║░░╚═╝█████╗╚█████╗░█████╗░░██████╔╝╚██╗░██╔╝█████╗░░██████╔╝
                    ██╔══██║██╔══██╗██║░░██╗╚════╝░╚═══██╗██╔══╝░░██╔══██╗░╚████╔╝░██╔══╝░░██╔══██╗
                    ██║░░██║██║░░██║╚█████╔╝░░░░░░██████╔╝███████╗██║░░██║░░╚██╔╝░░███████╗██║░░██║
                    ╚═╝░░╚═╝╚═╝░░╚═╝░╚════╝░░░░░░░╚═════╝░╚══════╝╚═╝░░╚═╝░░░╚═╝░░░╚══════╝╚═╝░░╚═╝
                    ]
                    """
            );
            logger.info("ARC-MAIN", "purple[ARC-SERVER is starting....]");

            if (server_data_format.equals("XML")) {
                XmlConfigManager xmlConfigManager = new XmlConfigManager(Player.class, User.class, PlayerRegistrar.class, TopCloth.class, BottomCloth.class, Shoes.class, Accessory.class);

                boolean isCreatedSYNC = xmlConfigManager.createUser("test1-sync", "onlyifyouknewwhatitwas009!&", "testuser-async@test.com", "Robo", "Logic", Genders.MALE);
                AtomicBoolean isAsyncCreated = new AtomicBoolean(false);
                xmlConfigManager.createUserAsync("test1-async", "onlyifyouknewwhatitwas009!&", "testuser-async@test.com", "Robo", "Logic", Genders.MALE, (success) -> {
                    isAsyncCreated.set(success);
                    checkResults(isAsyncCreated.get(), isCreatedSYNC);
                });
            }else if(server_data_format.equals("SQL")){
                SQLManager.createDatabase();

                HibernateDatabaseManager databaseManager = new HibernateDatabaseManager();
                com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.Player player;

                if (!databaseManager.entityExists(com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.Player.class, "test-user")){
                    player = createDefaultUser();
                    databaseManager.saveEntity(player);
                    logger.info("ARC-SQL", "Created and saved defaut user");
                }else{
                    logger.info("ARC-SQL", "Player already exists...");
                }
            }

            int port = 55489;
            ARCServer server = new ARCServer(new InetSocketAddress(port));
            server.start();
            CommandLine cmd = new CommandLine(new CommandManager());
            Terminal terminal = TerminalBuilder.builder()
                .color(false)
                .system(true)
                .build();
            LineReader reader = LineReaderBuilder.builder()
                .completer(new picocli.shell.jline3.PicocliJLineCompleter(new CommandLine(new CommandManager()).getCommandSpec()))
                .terminal(terminal)
                .build();

            Thread serverThread = new Thread(() -> {
                System.out.println("Server is running...");
                logger.info("ARC-SOCKET", StringTemplate.STR."green[Server started on port:]yellow[underline[\{port}]]green[with ip:]yellow[underline[\{server.getAddress()}]]");
            });
            serverThread.start();

            while (running) {
                try {
                    String input = reader.readLine("> ");
                    if ("exit".equalsIgnoreCase(input) || "quit".equalsIgnoreCase(input)) {
                        running = false;
                    } else if (!input.isEmpty()) {
                        cmd.execute(input.split("\\s+"));
                    }
                } catch (Exception e) {
                    System.out.println(StringTemplate.STR."Error: \{e.getMessage()}");
                }

                try {
                    serverThread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        } catch (Exception e) {
            logger.error("ARC-MAIN", STR."red[During server load an error was encountered] \{e.getMessage()}");
            logger.error("ARC-MAIN", STR."red[Error cause: ]\{e.getCause()}");
            e.printStackTrace();
        }
    }
}

class PlayerData {
    public int id;
    public float x, y, z;
}

class MapData {
    public String mapFile;

    public MapData(String mapFile) {
        this.mapFile = mapFile;
    }
}

