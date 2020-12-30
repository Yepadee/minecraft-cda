package com.broscraft.cda.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.broscraft.cda.database.DB;
import com.broscraft.cda.dtos.ItemOverviewDTO;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.items.PotionDTO;
import com.broscraft.cda.dtos.items.visitors.ItemInserterDB;

import org.bukkit.Material;
import org.bukkit.potion.PotionType;

public class ItemRepository {
    private ItemInserterDB itemInserter = new ItemInserterDB();

    public Map<Long, ItemOverviewDTO> getItemOverviews() {
        Map<Long, ItemOverviewDTO> itemOverviews = new HashMap<>();

        ResultSet results = DB.query(
            "SELECT d.demand, bb.best_bid, " +
            "s.supply, ab.best_ask, " +
            "i.id item_id, i.material, i.potion_type, i.is_upgraded, i.is_extended " +
            "FROM Items i " +
            "LEFT JOIN (" +
                "SELECT item_id, MAX(price) best_bid " +
                "FROM Orders " +
                "WHERE type='BID' " +
                "GROUP BY item_id " +
            ") bb ON i.id = bb.item_id " +
            "LEFT JOIN (" +
                "SELECT item_id, SUM(quantity - quantity_filled) demand " +
                "FROM Orders " +
                "WHERE type='BID' " +
                "GROUP BY item_id " +
            ") d ON i.id = d.item_id " +
            "LEFT JOIN (" +
                "SELECT item_id, MIN(price) best_ask " +
                "FROM Orders " +
                "WHERE type='ASK' " +
                "GROUP BY item_id " +
            ") ab ON i.id = ab.item_id " +
            "LEFT JOIN (" +
                "SELECT item_id, SUM(quantity - quantity_filled) supply " +
                "FROM Orders " +
                "WHERE type='ASK' " +
                "GROUP BY item_id " +
            ") s ON i.id = s.item_id "
        );

        try {
			while (results.next()) {
                int demand = results.getInt(1);
                Float bestBid = results.getFloat(2);
                int supply = results.getInt(3);
                Float bestAsk = results.getFloat(4);

                long itemId = results.getLong(5);
                Material material = Material.valueOf(results.getString(6));
                String potionType = results.getString(7);
                Boolean upgraded = results.getBoolean(8);
                Boolean extended = results.getBoolean(9);
                ItemDTO itemDTO;
                if (potionType == null) {
                    itemDTO = new ItemDTO();
                } else {
                    PotionDTO potionDTO = new PotionDTO();
                    potionDTO.setType(PotionType.valueOf(potionType));
                    potionDTO.upgraded(upgraded)
                    .extended(extended);
                    itemDTO = potionDTO;
                }
                 
                itemDTO.id(itemId);
                itemDTO.material(material);

                ItemOverviewDTO itemOverviewDTO = new ItemOverviewDTO()
                .demand(demand)
                .bestBid(bestBid == 0 ? null : bestBid)
                .supply(supply)
                .bestAsk(bestAsk == 0 ? null : bestAsk)
                .item(itemDTO);

                itemOverviews.put(itemId, itemOverviewDTO);

            }
            results.close();

            Set<Long> itemIds = itemOverviews.keySet();
            System.out.println(itemIds);
            // TODO: load enchantments with itemId.

		} catch (SQLException e) {
			e.printStackTrace();
		}

        return itemOverviews;
    }

    public Long create(ItemDTO itemDTO) {
        itemDTO.accept(itemInserter);
        DB.commit();
        return itemInserter.getCreatedKey();
    }
}
