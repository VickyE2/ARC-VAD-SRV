package com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseCreator {

    private static String name = "";
    private static String user = "";
    private static String password = "";
    private static String databaseName = "";
    private static DatabaseTypes type = null;
    private final String jdbcUrl;

    // Private constructor
    private DatabaseCreator(DatabaseBuilder builder) {
        name = builder.name;
        user = builder.user;
        password = builder.password;
        databaseName = builder.databaseName;
        type = builder.type;

        jdbcUrl = switch (type) {
            case MYSQL -> "jdbc:mysql://localhost:3306/" + databaseName;
            case SQLITE -> "jdbc:sqlite:" + name;
            default -> throw new IllegalArgumentException("Unsupported database type: " + type);
        };
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public static String getDatabaseName() {
        return databaseName;
    }

    public static String getName() {
        return name;
    }

    public static String getPassword() {
        return password;
    }

    public static String getUser() {
        return user;
    }

    public static DatabaseTypes getType() {
        return type;
    }

    public void createDatabase() throws Exception {
        if (this.type == DatabaseTypes.MYSQL) {
            if (name == null || user == null || password == null || databaseName == null) {
                throw new Exception("When using database type 'MYSQL', url, username, password, and databaseName values are required");
            }
            String jdbcUrl = "jdbc:mysql://localhost:57586/" + name + "/";
            try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
                 Statement statement = connection.createStatement()) {

                // Create the database
                String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS " + databaseName;
                statement.executeUpdate(createDatabaseSQL);
                System.out.println("Database '" + databaseName + "' created or already exists.");

            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
            }
        } else if (this.type == DatabaseTypes.SQLITE) {
            String jdbcUrl = "jdbc:sqlite:" + name;
            try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
                if (conn != null) {
                    System.out.println("Database created and connected: " + jdbcUrl);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Builder Class
    public static class DatabaseBuilder {
        private String name;
        private String user;
        private String password;
        private String databaseName;
        private DatabaseTypes type;

        public DatabaseBuilder(DatabaseTypes type) {
            this.type = type;
        }

        public DatabaseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public DatabaseBuilder user(String user) {
            this.user = user;
            return this;
        }

        public DatabaseBuilder password(String password) {
            this.password = password;
            return this;
        }

        public DatabaseBuilder databaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        public DatabaseCreator build() {
            return new DatabaseCreator(this);
        }
    }
}
