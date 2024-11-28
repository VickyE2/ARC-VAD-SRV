package com.arcvad.schoolquest.server.server.GlobalUtils;

import com.arcvad.schoolquest.server.server.ARCServer;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.HibernateDatabaseManager;

public class GlobalUtilities {
    public static AnsiLogger logger;
    public static boolean running = true;
    public static ARCServer server;
    public static HibernateDatabaseManager databaseManager;
}
