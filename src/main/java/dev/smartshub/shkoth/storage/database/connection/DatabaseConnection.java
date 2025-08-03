package dev.smartshub.shkoth.storage.database.connection;


import dev.smartshub.shkoth.storage.file.Configuration;
import dev.smartshub.shkoth.storage.file.FileManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static String URL;
    private static String USER;
    private static String PASSWORD;
    private static boolean initialized = false;

     {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't load a valid JDBC driver!", e);
        }
    }

    public static void init() {
        if (initialized) return;

        Configuration config = FileManager.get("database");
        config.saveFile();

        String DRIVER = config.getString("driver", "mysql").toLowerCase();
        String dbName = config.getString("db-name");

        if (dbName == null) {
            throw new IllegalArgumentException("No 'db-name' at database.yml");
        }

        if (DRIVER.equals("mysql")) {
            String host = config.getString("host");
            String port = config.getString("port");
            String username = config.getString("username");
            String password = config.getString("password");

            if (host == null || port == null || username == null || password == null) {
                throw new IllegalArgumentException("MySQL configuration is incomplete!");
            }

            URL = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                    + "?useSSL=false&autoReconnect=true&characterEncoding=utf8";
            USER = username;
            PASSWORD = password;

        } else if (DRIVER.equals("h2")) {
            URL = "jdbc:h2:file:./plugins/SH-Koth/data/" + dbName + ";AUTO_SERVER=TRUE";
            USER = "shkoth";
            PASSWORD = "";
        } else {
            throw new IllegalArgumentException("Unsupported driver: " + DRIVER);
        }

        initialized = true;
    }

    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            throw new IllegalStateException("DatabaseConnection not initialized!");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}


