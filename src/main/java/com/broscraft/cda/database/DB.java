package com.broscraft.cda.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class DB {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    public static void init(FileConfiguration pluginConfig, Plugin plugin) {
        String storageMethod = pluginConfig.getString("storage-method");
        String JDBC_DRIVER;
        String DIALECT;
        if (storageMethod.equals("MariaDB")) {
            JDBC_DRIVER = "org.mariadb.jdbc.Driver";
            DIALECT = "jdbc:mariadb";
        } else {
            plugin.getLogger().warning("ERROR! Invalid storage-method specified. Please check the storage method in the plugin config.");
            return;
        }

        String USER = pluginConfig.getString("data.user");
        String PASS = pluginConfig.getString("data.password");
        String HOST = pluginConfig.getString("data.host");
        String PORT = pluginConfig.getString("data.port");
        String DATABASE = pluginConfig.getString("data.database");
        String URL = DIALECT + "://" + HOST + ":" + PORT + "/" + DATABASE;

        int maxLifeTime = pluginConfig.getInt("data.pool-settings.maximum-lifetime");
        int minPoolSize = pluginConfig.getInt("data.pool-settings.minimum-idle");
        int maxPoolSize = pluginConfig.getInt("data.pool-settings.maximum-pool-size");
        int idleTimeout = pluginConfig.getInt("data.pool-settings.connection-timeout");
    
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
