package com.broscraft.cda.database.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.orders.OrderDTO;
import com.broscraft.cda.dtos.orders.OrderType;
import com.broscraft.cda.utils.EcoUtils;

public class PlayerOrderMapper extends RowMapper<OrderDTO> {

    @Override
    public OrderDTO getRow(ResultSet rs) throws SQLException {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.id(rs.getLong("id"))
        .type(OrderType.valueOf(rs.getString("type")))
        .item(new ItemDTO().id(rs.getLong("item_id")))
        .price(EcoUtils.parseMoney(rs.getInt("price")))
        .quantity(rs.getInt("quantity"))
        .quantityFilled(rs.getInt("quantity_filled"))
        .toCollect(rs.getInt("quantity_uncollected"))
        .quantityUnfilled(rs.getInt("quantity_unfilled"));
        return orderDTO;
    }
}