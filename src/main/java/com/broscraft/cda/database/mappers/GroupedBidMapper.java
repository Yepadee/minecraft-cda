package com.broscraft.cda.database.mappers;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.broscraft.cda.dtos.orders.grouped.GroupedBidDTO;
import com.broscraft.cda.utils.EcoUtils;

public class GroupedBidMapper extends RowMapper<GroupedBidDTO> {

    @Override
    public GroupedBidDTO getRow(ResultSet rs) throws SQLException {
        BigDecimal price = EcoUtils.parseMoney(rs.getInt("price"));
        int quantity = rs.getInt("total_quantity");
        GroupedBidDTO groupedBid = new GroupedBidDTO();
        groupedBid.price(price).quantity(quantity);
        return groupedBid;
    }
    
}
