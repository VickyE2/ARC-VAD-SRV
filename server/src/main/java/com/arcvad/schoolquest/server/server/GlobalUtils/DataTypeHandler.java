package com.arcvad.schoolquest.server.server.GlobalUtils;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities.JsonUtils;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.SqlUtils;
import com.arcvad.schoolquest.server.server.DataFormat.XML.utilities.XmlUtils;
import org.java_websocket.WebSocket;

import static com.arcvad.schoolquest.server.server.GlobalUtils.Config.getConfigValue;
import static com.arcvad.schoolquest.server.server.GlobalUtils.Config.loadConfig;
import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

public class DataTypeHandler {

    private static String dataFormat = getConfigValue("server_data_format").toString();
    public static void handlePlayerRequest(WebSocket conn, String message){
        loadConfig();
        if (dataFormat.equalsIgnoreCase("xml")){
            XmlUtils.handleRequestUser(conn, message);
        }else if (dataFormat.equalsIgnoreCase("json")){
            JsonUtils.handleRequestUser(conn, message);
        }else if (dataFormat.equalsIgnoreCase("sqlite")){
            SqlUtils.handleRequestUser(conn, message);
        }else{
            conn.send("playerDataResponse:err->Exception gotten from server end");
            logger.severe("ARC-SOCKET", STR."Encountered error while trying to query user: DataFormat \{dataFormat.toUpperCase()} is not a valid dataformat please check server_meta file and change to the previous data format then use convert.");
        }
    }
    public static void handlePlayerRegistration(WebSocket conn, String message) throws Exception {
        loadConfig();
        if (dataFormat.equalsIgnoreCase("xml")){
            XmlUtils.handleRegisterUser(conn, message);
        } else if (dataFormat.equalsIgnoreCase("json")) {
            JsonUtils.handleRegisterUser(conn, message);
        } else if (dataFormat.equalsIgnoreCase("sqlite")) {
            SqlUtils.handleRegisterUser(conn, message);
        }else{
            conn.send("createdP:false:Exception gotten from server end");
            logger.severe("ARC-SOCKET", STR."Encountered error while trying to query user: DataFormat \{dataFormat.toUpperCase()} is not a valid dataformat please check server_meta file and change to the previous data format then use convert.");
        }
    }
}
