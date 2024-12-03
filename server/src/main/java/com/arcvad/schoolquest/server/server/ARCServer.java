package com.arcvad.schoolquest.server.server;

import com.arcvad.schoolquest.server.server.Commands.CommandManager;
import com.arcvad.schoolquest.server.server.GlobalUtils.AnsiLogger;
import com.arcvad.schoolquest.server.server.GlobalUtils.Entiies.PlayerCreator;
import com.arcvad.schoolquest.server.server.GlobalUtils.Entiies.TransactionUser;
import com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities;
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

import static com.arcvad.schoolquest.server.server.GlobalUtils.Config.getConfigValue;
import static com.arcvad.schoolquest.server.server.GlobalUtils.DataTypeHandler.handlePlayerRegistration;
import static com.arcvad.schoolquest.server.server.GlobalUtils.DataTypeHandler.handlePlayerRequest;
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
            if (message.contains("RequestStream:")) {
                String newMessage = message.replace("Request:", "");
                if (newMessage.equalsIgnoreCase("map")){
                    sendInputStreamInChunks(conn, mapInputStream);
                }
            }
            if (message.startsWith("Movement:")) {
                handleMovementBroadcast(conn, message);
            }
            if (message.startsWith("Dad what's up?")) {
                conn.send("Im all good :]");
            }
            if (message.contains("requestUser")) {
                handlePlayerRequest(conn, message);
            }
            if (message.startsWith("registerUser->")) {
                try {
                    handlePlayerRegistration(conn, message);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
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

            if (server_data_format.equalsIgnoreCase("XML")) {
                TransactionUser testUser1 =
                    new TransactionUser.TransactionUserBuilder()
                        .setEmail("test_transactionUser1_xml@test.com")
                        .setFirstname("RoverX")
                        .setLastname("Logic")
                        .setGender(Genders.MALE)
                        .setUsername("test_user1_xml")
                        .setPassword("0n!y!fIw0uldt3lly0u{-}")
                        .build();
                boolean isSuccessful = PlayerCreator.createXMLPlayer(testUser1);
                if (isSuccessful){
                    logger.success("ARC-XML", "Created and saved defaut user");
                }else {
                    logger.error("ARC-XML", "Failed to create test user. Check logs.");
                }
            }
            else if(server_data_format.equalsIgnoreCase("JSON")){
                TransactionUser testUser1 =
                    new TransactionUser.TransactionUserBuilder()
                        .setEmail("test_transactionUser1_json@test.com")
                        .setFirstname("RoverJ")
                        .setLastname("Logic")
                        .setGender(Genders.MALE)
                        .setUsername("test_user1_json")
                        .setPassword("0n!y!fIw0uldt3lly0u{-}")
                        .build();
                boolean isSuccessful = PlayerCreator.createJsonPlayer(testUser1);
                if (isSuccessful){
                    logger.success("ARC-JSON", "Created and saved defaut user");
                }else {
                    logger.error("ARC-JSON", "Failed to create test user. Check logs.");
                }
            }
            else if(server_data_format.equalsIgnoreCase("SQL") || server_data_format.equalsIgnoreCase("SQLITE")){
                TransactionUser testUser1 =
                    new TransactionUser.TransactionUserBuilder()
                        .setEmail("test_transactionUser1_sql@test.com")
                        .setFirstname("RoverSQ")
                        .setLastname("Logic")
                        .setGender(Genders.MALE)
                        .setUsername("test_user1_sql")
                        .setPassword("0n!y!fIw0uldt3lly0u{-}")
                        .build();
                boolean isSuccessful = PlayerCreator.createSQLPlayer(testUser1);
                if (isSuccessful){
                    logger.success("ARC-SQL", "Created and saved defaut user");
                }else {
                    logger.error("ARC-SQL", "Failed to create test user. Check logs.");
                }
            }

            int port = 55489;
            GlobalUtilities.server = new ARCServer(new InetSocketAddress(port));
            GlobalUtilities.server.start();
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
                logger.info("ARC-SOCKET", StringTemplate.STR."green[Server started on port:]yellow[underline[\{port}]]green[with ip:]yellow[underline[\{GlobalUtilities.server.getAddress()}]]");
                System.out.println("Server is running...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
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

