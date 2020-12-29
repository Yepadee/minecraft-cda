package com.broscraft.cda.repositories;

import java.util.HashMap;
import java.util.Map;

import com.broscraft.cda.database.DB;
import com.broscraft.cda.dtos.ItemOverviewDTO;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.items.visitors.ItemInserterDB;

import org.bukkit.Material;

public class ItemRepository {
    private ItemInserterDB itemInserter = new ItemInserterDB();

    public Map<Long, ItemOverviewDTO> getItemOverviews() {
        Map<Long, ItemOverviewDTO> itemOverviews = new HashMap<>();

        // TODO
        ItemDTO item1 = new ItemDTO();
        item1.setId(1L);
        item1.setMaterial(Material.STONE);

        ItemOverviewDTO overview1 = new ItemOverviewDTO();
        overview1.setDemand(100);
        overview1.setBestBid(10.0f);
        overview1.setSupply(200);
        overview1.setBestAsk(20.0f);
        overview1.setItem(item1);

        ItemDTO item2 = new ItemDTO();
        item2.setId(2L);
        item2.setMaterial(Material.DIAMOND_BLOCK);

        ItemOverviewDTO overview2 = new ItemOverviewDTO();
        overview2.setDemand(100);
        overview2.setBestBid(10.0f);
        overview2.setSupply(200);
        overview2.setBestAsk(20.0f);
        overview2.setItem(item2);

        itemOverviews.put(item1.getId(), overview1);
        itemOverviews.put(item2.getId(), overview2);

        return itemOverviews;
    }

    public Long create(ItemDTO itemDTO) {
        itemDTO.accept(itemInserter);
        DB.commit();
        return itemInserter.getCreatedKey();
    }
}
