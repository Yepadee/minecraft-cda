package com.broscraft.cda.repositories;

import java.util.HashMap;
import java.util.Map;

import com.broscraft.cda.model.ItemOverviewDTO;
import com.broscraft.cda.model.items.ItemDTO;

import org.bukkit.Material;

public class ItemRepository {
    private Map<ItemDTO, Long> itemIds = new HashMap<>();

    public Map<Long, ItemOverviewDTO> getAllItemOverviews() {
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

        itemOverviews.forEach((itemId, itemOverview) -> {
            itemIds.put(itemOverview.getItem(), itemId);
        });

        return itemOverviews;
    }

    public Long getItemId(ItemDTO itemDTO) {
        return itemIds.get(itemDTO);
    }

    public Long create(ItemDTO itemDTO) {
        //TODO: submit request creating item and retrieve it's id;
        //TODO: MAKE SURE ID IS NOT NULL!!!
        
        Long itemId = 3L;
        itemIds.put(itemDTO, itemId);
        
        return itemId;
    }
}
