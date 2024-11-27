package com.arcvad.schoolquest.server.server.DataFormat.SQL;

import com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.DatabaseCreator;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.DatabaseCreator.DatabaseBuilder;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.DatabaseTypes;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.HibernateUtil;
import com.arcvad.schoolquest.server.server.GlobalUtils.RandomStringGenerator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import static com.arcvad.schoolquest.server.server.GlobalUtils.Config.getConfigValue;

public class SQLManager {

    static RandomStringGenerator generator = RandomStringGenerator.getInstance();

    private static final Properties dbCredentials = loadCredentials();
    private static final String userName = dbCredentials.getProperty("userName", generator.generate(20, true, true, true, false));
    private static final String password = dbCredentials.getProperty("password", generator.generatePassword(30));

    private static String hibernateConfig = "";

    public static void createDatabase() throws Exception {
        saveCredentials(userName, password);
        String sqlType = getConfigValue("sql_type").toString();
        if (sqlType.equalsIgnoreCase("MYSQL")) {
            DatabaseCreator database =
                new DatabaseBuilder(DatabaseTypes.MYSQL)
                    .user(userName)
                    .password(password)
                    .databaseName("global")
                    .build();

            database.createDatabase();

            hibernateConfig = generateHibernateConfig(
                database,
                "org.hibernate.dialect.MySQLDialect"
            );
        }else if (sqlType.equalsIgnoreCase("SQLITE")){
            File parentFolder = new File("./ServerData/databases");
            parentFolder.mkdirs();
            DatabaseCreator database =
                new DatabaseBuilder(DatabaseTypes.SQLITE)
                    .name("./ServerData/databases/global.db")
                    .build();

            database.createDatabase();

            hibernateConfig = generateHibernateConfig(
                database,
                "org.hibernate.community.dialect.SQLiteDialect"
            );
        }

        HibernateUtil.initialise(writeHibernateConfigToFile(hibernateConfig, "./ServerData/configs/hibernate.cfg.xml"));

    }

    public static void saveCredentials(String userName, String password) {
        try (FileWriter writer = new FileWriter("./ServerData/configs/db_credentials.properties")) {
            Properties properties = new Properties();
            properties.setProperty("userName", userName);
            properties.setProperty("password", password);
            properties.store(writer, "Database Credentials");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties loadCredentials() {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("./ServerData/configs/db_credentials.properties")) {
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private static String generateHibernateConfig(DatabaseCreator database, String dialect) {
        StringBuilder config = new StringBuilder();

        config.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
            .append("<!DOCTYPE hibernate-configuration PUBLIC\n")
            .append("        \"-//Hibernate/Hibernate Configuration DTD 3.0//EN\"\n")
            .append("        \"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd\">\n")
            .append("<hibernate-configuration>\n")
            .append("    <session-factory>\n")
            .append("        <!-- Database connection settings -->\n")
            .append("        <property name=\"hibernate.connection.driver_class\">").append(getDriverClass(DatabaseCreator.getType())).append("</property>\n")
            .append("        <property name=\"hibernate.connection.url\">").append(database.getJdbcUrl()).append("</property>\n");

        if (DatabaseCreator.getUser() != null && DatabaseCreator.getPassword() != null) {
            config.append("        <property name=\"hibernate.connection.username\">").append(DatabaseCreator.getUser()).append("</property>\n")
                .append("        <property name=\"hibernate.connection.password\">").append(DatabaseCreator.getPassword()).append("</property>\n");
        }

        config.append("        <!-- Hibernate settings -->\n")
            .append("        <property name=\"hibernate.dialect\">").append(dialect).append("</property>\n")
            .append("        <property name=\"hibernate.show_sql\">true</property>\n")
            .append("        <property name=\"hibernate.format_sql\">true</property>\n")
            .append("        <property name=\"hibernate.hbm2ddl.auto\">update</property>\n")
            .append("""
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.FamilyRegistrar"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.FamilyRegistrar"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.Player"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.User"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.AvailableStyles"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.EyelashStyles"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.HairStyles"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.PlayerFamily"/>

                    """)
            .append("    </session-factory>\n")
            .append("</hibernate-configuration>\n");

        return config.toString();
    }

    private static File writeHibernateConfigToFile(String configContent, String filePath) {
        try {
            // Create a File object from the file path
            File file = new File(filePath);

            // Ensure the parent directory exists
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (parentDir.mkdirs()) {
                    System.out.println("Created directories: " + parentDir.getAbsolutePath());
                } else {
                    throw new IOException("Failed to create directories: " + parentDir.getAbsolutePath());
                }
            }

            // Write the file
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(configContent);
                System.out.println("Hibernate configuration file generated successfully at: " + filePath);

                return file;
            }
        } catch (IOException e) {
            System.err.println("Failed to write Hibernate configuration file: " + e.getMessage());
            return null;
        }
    }

    private static String getDriverClass(DatabaseTypes type) {
        return switch (type) {
            case MYSQL -> "com.mysql.cj.jdbc.Driver";
            case SQLITE -> "org.sqlite.JDBC";
            default -> throw new IllegalArgumentException("Unsupported database type: " + type);
        };
    }
}
