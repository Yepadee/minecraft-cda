package com.broscraft.cda.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.broscraft.cda.database.DB;
import com.broscraft.cda.dtos.ItemOverviewDTO;
import com.broscraft.cda.dtos.items.EnchantedItemDTO;
import com.broscraft.cda.dtos.items.EnchantmentDTO;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.items.PotionDTO;
import com.broscraft.cda.dtos.items.visitors.ItemInserterDB;

import org.bukkit.Material;
import org.bukkit.potion.PotionType;

public class ItemRepository {
    private ItemInserterDB itemInserter = new ItemInserterDB();
    private PreparedStatement getItemOverviewsStmt;
    private PreparedStatement getItemEnchantsStmt;
    public ItemRepository() {
        getItemOverviewsStmt = DB.prepareStatement(
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

        getItemEnchantsStmt = DB.prepareStatement(
            "SELECT item_id, enchantment, level " +
            "FROM Enchantments"  
        );
    }

    public Map<Long, ItemOverviewDTO> getItemOverviews() {
        Map<Long, ItemOverviewDTO> itemOverviews = new HashMap<>();

        ResultSet itemResults = DB.query(getItemOverviewsStmt);

        ResultSet enchantResults = DB.query(getItemEnchantsStmt);

        try {
			while (itemResults.next()) {
                int demand = itemResults.getInt(1);
                Float bestBid = itemResults.getFloat(2);
                int supply = itemResults.getInt(3);
                Float bestAsk = itemResults.getFloat(4);

                long itemId = itemResults.getLong(5);
                Material material = Material.valueOf(itemResults.getString(6));
                String potionType = itemResults.getString(7);
                Boolean upgraded = itemResults.getBoolean(8);
                Boolean extended = itemResults.getBoolean(9);
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
            itemResults.close();

            Map<Long, List<EnchantmentDTO>> itemEnchantments = new HashMap<>();
            while (enchantResults.next()) {
                Long itemId = enchantResults.getLong(1);
                String enchantment = enchantResults.getString(2);
                Integer level = enchantResults.getInt(3);
                EnchantmentDTO enchantmentDTO = new EnchantmentDTO()
                .enchantment(enchantment)
                .level(level);

                if (!itemEnchantments.containsKey(itemId))
                    itemEnchantments.put(itemId, new ArrayList<>());

                itemEnchantments.get(itemId).add(enchantmentDTO);
            }
            itemEnchantments.entrySet().forEach(e -> {
                Long itemId = e.getKey();
                List<EnchantmentDTO> enchantments = e.getValue();
                ItemDTO itemDTO = itemOverviews.get(itemId).getItem();
                EnchantedItemDTO enchantedItemDTO = new EnchantedItemDTO();
                enchantedItemDTO.setId(itemDTO.getId());
                enchantedItemDTO.setMaterial(itemDTO.getMaterial());
                enchantedItemDTO.setEnchantments(enchantments);
                itemOverviews.get(itemId).setItem(enchantedItemDTO);

            });
		} catch (SQLException e) {
			e.printStackTrace();
		}

        return itemOverviews;
    }

    public Long create(ItemDTO itemDTO) {
        itemDTO.accept(itemInserter);
        DB.commit();
        Long itemId = itemInserter.getCreatedKey();
        itemDTO.setId(itemId);
        return itemId;
    }
}
