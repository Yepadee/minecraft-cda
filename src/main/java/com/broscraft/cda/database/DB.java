package com.broscraft.cda.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DB {
    // private static String DB_NAME = "MinecraftCDA";
    // private static String DIALECT = "jdbc:mariadb:";
    static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    private static String USER = "root";
    private static String PASS = "";
    private static String URL = "jdbc:mariadb://localhost:3306/MinecraftCDA?user=root&password=";
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    public static void init(File dbFolder) {
        //String url = DIALECT + dbFolder.getAbsolutePath() + File.separator + DB_NAME;
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/?user=root&password=");
            Statement s = conn.createStatement();
            s.executeUpdate("CREATE DATABASE IF NOT EXISTS MinecraftCDA");
            s.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASS);
        config.addDataSourceProperty("cachePrepStmts" , "true");
        config.addDataSourceProperty("prepStmtCacheSize" , "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit" , "2048");
        ds = new HikariDataSource(config);
        ds.setMaxLifetime(60000);
        ds.setMinimumIdle(0);
        ds.setMaximumPoolSize(1);
        ds.setIdleTimeout(5000);
        ds.setAutoCommit(false);

        try (Connection con = DB.getConnection()) {
            con.setAutoCommit(false);
            PreparedStatement stmt;

            if (!DB.tableExists(con, "Items")) {
                stmt = con.prepareStatement(
                    "CREATE TABLE Items " +
                    "(id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    " material VARCHAR(64), " + 
                    " potion_type VARCHAR(64), " + 
                    " is_upgraded TINYINT, " + 
                    " is_extended TINYINT" + 
                    ")"
                );
                stmt.execute();
                stmt.close();
            }

            if (!DB.tableExists(con, "Orders")) {
                stmt = con.prepareStatement(
                    "CREATE TABLE Orders " +
                    "(id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    " type CHAR(3) NOT NULL, " + 
                    " player_uuid CHAR(36) NOT NULL, " + 
                    " item_id INTEGER NOT NULL, " + 
                    " price INTEGER NOT NULL, " + 
                    " quantity INTEGER NOT NULL, " +
                    " quantity_filled INTEGER DEFAULT 0, " +
                    " quantity_uncollected INTEGER DEFAULT 0, " +
                    " created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    " FOREIGN KEY (item_id) REFERENCES Items(id) " +
                    ")"
                );
                stmt.execute();
                stmt.close();
            }
    
            if (!DB.tableExists(con, "Enchantments")) {
                stmt = con.prepareStatement(
                    "CREATE TABLE Enchantments " +
                    "(id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    " item_id INTEGER NOT NULL, " +
                    " enchantment VARCHAR(64) NOT NULL, " + 
                    " level TINYINT NOT NULL, " + 
                    " FOREIGN KEY (item_id) REFERENCES Items(id) " +
                    ")"
                );
                stmt.execute();
                stmt.close();
            }
    
            con.commit();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection con = ds.getConnection();
        con.setAutoCommit(false);
        return con;
    }

    public static void close() {
        ds.close();
    }

    public static boolean tableExists(Connection con, String tableName) {
        ResultSet tables;
        try {
            tables = con.getMetaData().getTables(null, null, tableName, null);
            if (tables.next()) {
                return true;
              } else {
                return false;
              }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // public static Long create(PreparedStatement stmt) {
    //     try {
    //         stmt.executeUpdate();
    //         ResultSet rs = stmt.getGeneratedKeys();
    //         Long createdKey = null;
    //         if (rs.next()){
    //             createdKey=rs.getLong(1);
    //         }
    //         return createdKey;
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         return null;
    //     }
    // }
}
