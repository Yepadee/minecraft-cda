package com.broscraft.cda.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.bukkit.configuration.file.FileConfiguration;

public class DB {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    public static void init(FileConfiguration pluginConfig) {
        String JDBC_DRIVER = pluginConfig.getString("db.driverclassname");
        String DIALECT = pluginConfig.getString("db.dialect");
        String USER = pluginConfig.getString("db.user");
        String PASS = pluginConfig.getString("db.password");
        String HOST = pluginConfig.getString("db.host");
        String PORT = pluginConfig.getString("db.port");
        String DATABASE = pluginConfig.getString("db.database");
        String URL = DIALECT + "://" + HOST + ":" + PORT + "/" + DATABASE;

        int maxLifeTime = pluginConfig.getInt("db.pool.maxlifetime");
        int minPoolSize = pluginConfig.getInt("db.pool.minpoolsize");
        int maxPoolSize = pluginConfig.getInt("db.pool.maxpoolsize");
        int idleTimeout = pluginConfig.getInt("db.pool.idletimeout");
    
        config.setDriverClassName(JDBC_DRIVER);
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASS);
        config.addDataSourceProperty("cachePrepStmts" , "true");
        config.addDataSourceProperty("prepStmtCacheSize" , "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit" , "2048");
        ds = new HikariDataSource(config);
        ds.setMaxLifetime(maxLifeTime);
        ds.setMinimumIdle(minPoolSize);
        ds.setMaximumPoolSize(maxPoolSize);
        ds.setIdleTimeout(idleTimeout);
        ds.setAutoCommit(false);

        try (Connection con = DB.getConnection()) {
            con.setAutoCommit(false);
            PreparedStatement stmt;

            Statement s = con.createStatement();
            s.executeUpdate("CREATE DATABASE IF NOT EXISTS  " + DATABASE);
            s.close();

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
