package com.broscraft.cda.database.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.broscraft.cda.dtos.orders.OrderDTO;
import com.broscraft.cda.utils.EcoUtils;

public class AffectedOrderMapper extends RowMapper<OrderDTO> {

    @Override
    public OrderDTO getRow(ResultSet rs) throws SQLException {
        OrderDTO affectedOrder = new OrderDTO();
        affectedOrder.setId(rs.getLong("id"));
        affectedOrder.setPlayerUUID(UUID.fromString(rs.getString("player_uuid")));
        affectedOrder.setPrice(EcoUtils.parseMoney(rs.getInt("price")));
        affectedOrder.setQuantity(rs.getInt("quantity"));
        affectedOrder.setQuantityFilled(rs.getInt("quanitity_filled"));
        affectedOrder.setToCollect(rs.getInt("quantity_uncollected"));
        affectedOrder.setQuantityUnfilled(rs.getInt("quantity_unfilled"));
        return affectedOrder;
    }
    
}
