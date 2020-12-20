package com.broscraft.cda.repositories;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.broscraft.cda.model.items.ItemDTO;
import com.broscraft.cda.model.ItemOverviewDTO;

public class ItemOverviewRepository {
    Map<Long, ItemOverviewDTO> itemOverviews = new HashMap<>();

    public void loadItemOverviews() {
        ItemDTO item1 = new ItemDTO();
        item1.setId(1L);
        item1.setMaterial("STONE");

        ItemOverviewDTO overview1 = new ItemOverviewDTO();
        overview1.setDemand(100);
        overview1.setBestBid(10.0f);
        overview1.setSupply(200);
        overview1.setBestAsk(20.0f);
        overview1.setItem(item1);

        ItemDTO item2 = new ItemDTO();
        item2.setId(2L);
        item2.setMaterial("DIAMOND_BLOCK");

        ItemOverviewDTO overview2 = new ItemOverviewDTO();
        overview2.setDemand(100);
        overview2.setBestBid(10.0f);
        overview2.setSupply(200);
        overview2.setBestAsk(20.0f);
        overview2.setItem(item2);

        itemOverviews.put(item2.getId(), overview2);
    }

    public Collection<ItemOverviewDTO> getItemOverviews() {
        return this.itemOverviews.values();
    }

    public void hitBid(long itemId, int quantity) {
        ItemOverviewDTO itemOverview = this.itemOverviews.get(itemId);
        int demand = itemOverview.getDemand();
        itemOverview.setDemand(demand - quantity);
    }

    public void liftAsk(long itemId, int quantity) {
        ItemOverviewDTO itemOverview = this.itemOverviews.get(itemId);
        int supply = itemOverview.getSupply();
        itemOverview.setDemand(supply - quantity);
    }
}
