package com.broscraft.cda.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;

public class DB {
    private static String DB_NAME = "minecraft-cda.db";
    private static String DIALECT = "jdbc:sqlite:";
    private static String USER = "";
    private static String PASS = "";

    private static Connection con;

    public static void init(File dbFolder) {
        try {
            String url = DIALECT + dbFolder.getAbsolutePath() + File.separator + DB_NAME;
            con = DriverManager.getConnection(url, USER, PASS);
            con.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void commit() {
        try {
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean tableExists(String tableName) {
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

    public static PreparedStatement prepareStatement(String query) {
        try {
            return con.prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResultSet query(PreparedStatement stmt) {
        try {
            return stmt.executeQuery(PASS);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResultSet query(String query) {
        try {
            Statement stmt = con.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int update(PreparedStatement stmt) {
        try {
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Long create(PreparedStatement stmt) {
        try {
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            Long createdKey = null;
            if (rs.next()){
                createdKey=rs.getLong(1);
            }
            return createdKey;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int update(String udpate) {
        try {
            Statement stmt = con.createStatement();
            return stmt.executeUpdate(udpate);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
