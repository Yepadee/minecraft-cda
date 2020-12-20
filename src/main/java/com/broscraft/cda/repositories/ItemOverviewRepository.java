package com.broscraft.cda.repositories;

import java.util.HashMap;
import java.util.Map;

import com.broscraft.cda.model.items.ItemDTO;
import com.broscraft.cda.model.orders.input.NewOrderDTO;
import com.broscraft.cda.observers.NewOrderObserver;
import com.broscraft.cda.observers.OverviewUpdateObserver;
import com.broscraft.cda.model.ItemOverviewDTO;

public class ItemOverviewRepository implements NewOrderObserver {
    Map<Long, ItemOverviewDTO> itemOverviews = new HashMap<>();
    OverviewUpdateObserver overviewUpdateObserver;

    public ItemOverviewRepository(OverviewUpdateObserver overviewUpdateObserver) {
        this.overviewUpdateObserver = overviewUpdateObserver;
    }

    public void loadItemOverviews() {
        // TODO
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
        overviewUpdateObserver.onOverviewLoad(itemOverviews.values());
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

    @Override
    public void onNewOrder(NewOrderDTO newOrderDTO) {
        Long itemId = newOrderDTO.getItem().getId();
        ItemOverviewDTO itemOverview;
        if (this.itemOverviews.containsKey(itemId)) {
            itemOverview = this.itemOverviews.get(itemId);
            
            switch (newOrderDTO.getType()) {
                case ASK:
                    Float bestAsk = itemOverview.getBestAsk();
                    int supply = itemOverview.getSupply();
                    itemOverview.setSupply(supply + newOrderDTO.getQuantity());
                    if (newOrderDTO.getPrice() < bestAsk) itemOverview.setBestAsk(newOrderDTO.getPrice());
                    break;
                case BID:
                    Float bestBid = itemOverview.getBestBid();
                    int demand = itemOverview.getDemand();
                    itemOverview.setDemand(demand + newOrderDTO.getQuantity());
                    if (newOrderDTO.getPrice() > bestBid) itemOverview.setBestBid(newOrderDTO.getPrice());
                    break;
            }
        } else {
            itemOverview = new ItemOverviewDTO();
            
            switch (newOrderDTO.getType()) {
                case ASK:
                    itemOverview.setSupply(newOrderDTO.getQuantity());
                    itemOverview.setBestAsk(newOrderDTO.getPrice());
                    break;
                case BID:
                    itemOverview.setDemand(newOrderDTO.getQuantity());
                    itemOverview.setBestBid(newOrderDTO.getPrice());
                    break;
            }
            this.itemOverviews.put(itemId, itemOverview);
        }
        

        this.overviewUpdateObserver.onOverviewUpdate(itemOverview);
    }
}
