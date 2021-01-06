package com.broscraft.cda.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.broscraft.cda.database.DB;
import com.broscraft.cda.database.mappers.EnchantmentMapper;
import com.broscraft.cda.database.mappers.ItemOverviewMapper;
import com.broscraft.cda.dtos.ItemOverviewDTO;
import com.broscraft.cda.dtos.items.EnchantedItemDTO;
import com.broscraft.cda.dtos.items.EnchantmentDTO;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.items.visitors.ItemInserterDB;

public class ItemRepository {
    private ItemInserterDB itemInserter = new ItemInserterDB();

    public Map<Long, ItemOverviewDTO> getItemOverviews() {  
        try (Connection con = DB.getConnection()) {
            PreparedStatement getItemOverviewsStmt = con.prepareStatement(
                "SELECT d.demand demand, bb.best_bid best_bid, " +
                "s.supply supply, ab.best_ask best_ask, " +
                "i.id item_id, i.material material, i.potion_type potion_type, i.is_upgraded is_upgraded, i.is_extended is_extended " +
                "FROM Items i " +
                "LEFT JOIN (" +
                    "SELECT item_id, MAX(price) best_bid " +
                    "FROM Orders " +
                    "WHERE type='BID' AND quantity_filled < quantity " +
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
                    "WHERE type='ASK' AND quantity_filled < quantity " +
                    "GROUP BY item_id " +
                ") ab ON i.id = ab.item_id " +
                "LEFT JOIN (" +
                    "SELECT item_id, SUM(quantity - quantity_filled) supply " +
                    "FROM Orders " +
                    "WHERE type='ASK' " +
                    "GROUP BY item_id " +
                ") s ON i.id = s.item_id " + 
                "ORDER BY i.material ASC"
            );
    
            PreparedStatement getItemEnchantsStmt = con.prepareStatement(
                "SELECT item_id, enchantment, level " +
                "FROM Enchantments"  
            );

            ResultSet itemOverviewResults = getItemOverviewsStmt.executeQuery();
            ResultSet enchantResults = getItemEnchantsStmt.executeQuery();

            Map<Long, ItemOverviewDTO> itemOverviews = new LinkedHashMap<>();
            ItemOverviewMapper itemOverviewMapper = new ItemOverviewMapper();
			while (itemOverviewResults.next()) {
                ItemOverviewDTO itemOverviewDTO = itemOverviewMapper.getRow(itemOverviewResults);
                Long itemId = itemOverviewDTO.getItem().getId();
                itemOverviews.put(itemId, itemOverviewDTO);

            }
            itemOverviewResults.close();

            Map<Long, List<EnchantmentDTO>> itemEnchantments = new HashMap<>();
            EnchantmentMapper enchantmentMapper = new EnchantmentMapper();
            while (enchantResults.next()) {
                Long itemId = enchantResults.getLong("item_id");
               
                EnchantmentDTO enchantmentDTO = enchantmentMapper.getRow(enchantResults);
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

            getItemOverviewsStmt.close();
            getItemEnchantsStmt.close();

            return itemOverviews;
		} catch (SQLException e) {
			e.printStackTrace();
		}

        return null;
    }

    public Long create(ItemDTO itemDTO) {
        try (Connection con = DB.getConnection()) {
            itemInserter.setConnection(con);
            itemDTO.accept(itemInserter);

            Long itemId = Objects.requireNonNull(itemInserter.getCreatedKey());
            itemDTO.setId(itemId);
            
            con.commit();

            return itemId;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
