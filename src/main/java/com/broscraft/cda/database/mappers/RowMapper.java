package com.broscraft.cda.database.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class RowMapper<T> {
    public abstract T getRow(ResultSet rs) throws SQLException;

    public List<T> getResults(ResultSet rs) {
        List<T> results = new ArrayList<>();

        try {
            while(rs.next()) {
                results.add(getRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return results;
    }
}
