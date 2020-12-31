package com.broscraft.cda.database;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

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
