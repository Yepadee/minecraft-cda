package com.broscraft.cda.database.mappers;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.broscraft.cda.dtos.orders.grouped.GroupedAskDTO;
import com.broscraft.cda.utils.EcoUtils;

public class GroupedAskMapper extends RowMapper<GroupedAskDTO> {

    @Override
    public GroupedAskDTO getRow(ResultSet rs) throws SQLException {
        BigDecimal price = EcoUtils.parseMoney(rs.getInt("price"));
        int quantity = rs.getInt("total_quantity");
        GroupedAskDTO groupedAsk = new GroupedAskDTO();
        groupedAsk.price(price).quantity(quantity);
        return groupedAsk;
    }
    
}
