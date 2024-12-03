package com.arcvad.schoolquest.server.server.DataFormat.SQL;

import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.*;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.FamilyRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.Player;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.PlayerRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.User;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.DatabaseCreator;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.DatabaseCreator.DatabaseBuilder;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.DatabaseTypes;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.HibernateUtil;
import com.arcvad.schoolquest.server.server.GlobalUtils.RandomStringGenerator;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

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

    private static SessionFactory sessionFactory;
    private static String hibernateConfig = "";
    private static boolean isSqlite;
    private static File sqliteFile;
    private static String dialect;
    private static DatabaseCreator database;

    public static void createDatabase() throws Exception {
        saveCredentials(userName, password);
        String sqlType = getConfigValue("sql_type").toString();
        if (sqlType.equalsIgnoreCase("MYSQL")) {
            database =
                new DatabaseBuilder(DatabaseTypes.MYSQL)
                    .user(userName)
                    .password(password)
                    .databaseName("global")
                    .build();

            database.createDatabase();

            dialect = "org.hibernate.dialect.MySQLDialect";
            hibernateConfig = generateHibernateConfig(
                database,
                dialect
            );
        }else if (sqlType.equalsIgnoreCase("SQLITE")){
            File parentFolder = new File("./ServerData/databases");
            parentFolder.mkdirs();
            String sqlitePath = "./ServerData/databases/global.db";
            database =
                new DatabaseBuilder(DatabaseTypes.SQLITE)
                    .name(sqlitePath)
                    .build();

            database.createDatabase();

            isSqlite = true;
            sqliteFile = new File(sqlitePath);
            dialect = "org.hibernate.community.dialect.SQLiteDialect";
            hibernateConfig = generateHibernateConfig(
                database,
                dialect
            );
        }


        HibernateUtil.initialise(writeHibernateConfigToFile(
            generateHibernateConfig(database, dialect),
            "./ServerData/configs/hibernate.cfg.xml")
        );

        /*
        HibernateUtil.initialise(
            configureSessionFactory(
                database.getJdbcUrl(),
                userName,
                password,
                dialect,
                true,
                true,
                "update"
            ),
            isSqlite,
            sqliteFile);

         */
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

    public static SessionFactory configureSessionFactory(
        String jdbcUrl,
        String username,
        String password,
        String dialect,
        boolean showSql,
        boolean formatSql,
        String ddlAuto
    ) {
        try {
            Configuration configuration = new Configuration();

            // Database settings
            configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver"); // Change for other DBs
            configuration.setProperty("hibernate.connection.url", jdbcUrl);

            if (username != null && password != null) {
                configuration.setProperty("hibernate.connection.username", username);
                configuration.setProperty("hibernate.connection.password", password);
            }

            // Hibernate settings
            configuration.setProperty("hibernate.dialect", dialect);
            configuration.setProperty("hibernate.show_sql", String.valueOf(showSql));
            configuration.setProperty("hibernate.format_sql", String.valueOf(formatSql));
            configuration.setProperty("hibernate.hbm2ddl.auto", ddlAuto);

            // Add annotated classes
            configuration.addAnnotatedClass(Player.class);
            configuration.addAnnotatedClass(FamilyRegistrar.class);
            configuration.addAnnotatedClass(PlayerRegistrar.class);
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(BottomCloth.class);
            configuration.addAnnotatedClass(Shoe.class);
            configuration.addAnnotatedClass(TopCloth.class);
            configuration.addAnnotatedClass(Accessory.class);
            configuration.addAnnotatedClass(Family.class);
            configuration.addAnnotatedClass(AvailableStyles.class);
            configuration.addAnnotatedClass(EyelashStyle.class);
            configuration.addAnnotatedClass(HairStyle.class);
            configuration.addAnnotatedClass(PlayerFamily.class);
            configuration.addAnnotatedClass(MaterialRegistrar.class);

            sessionFactory = configuration.buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating Hibernate SessionFactory", e);
        }
        return sessionFactory;
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
            .append("        <property name=\"hibernate.jdbc.fetch_size\">50</property>\n")
            .append("        <property name=\"org.hibernate.SQL\">DEBUG</property>\n")
            .append("        <property name=\"org.hibernate.type.descriptor.sql.BasicBinder\">TRACE</property>\n")
            .append("""
                            <!-- Mappings -->
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.PlayerRegistrar"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.FamilyRegistrar"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.Player"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.User"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.AvailableStyles"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.EyelashStyle"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.HairStyle"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.PlayerFamily"/>
                            <mapping class="com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.MaterialRegistrar"/>

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
