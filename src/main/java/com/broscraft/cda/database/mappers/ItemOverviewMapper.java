package com.broscraft.cda.database.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.broscraft.cda.dtos.ItemOverviewDTO;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.utils.EcoUtils;

public class ItemOverviewMapper extends RowMapper<ItemOverviewDTO> {
    private ItemMapper itemMapper = new ItemMapper();

    @Override
    public ItemOverviewDTO getRow(ResultSet rs) throws SQLException {
        int demand = rs.getInt("demand");
        int bestBid = rs.getInt("best_bid");
        int supply = rs.getInt("supply");
        int bestAsk = rs.getInt("best_ask");
        
        ItemDTO itemDTO = itemMapper.getRow(rs);

        ItemOverviewDTO itemOverviewDTO = new ItemOverviewDTO()
        .demand(demand)
        .bestBid(bestBid == 0 ? null : EcoUtils.parseMoney(bestBid))
        .supply(supply)
        .bestAsk(bestAsk == 0 ? null : EcoUtils.parseMoney(bestAsk))
        .item(itemDTO);

        return itemOverviewDTO;
    }
    
}
