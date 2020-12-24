package com.broscraft.cda.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.broscraft.cda.model.items.ItemDTO;
import com.broscraft.cda.model.orders.OrderDTO;
import com.broscraft.cda.model.orders.input.NewOrderDTO;
import com.broscraft.cda.observers.OrderObserver;
import com.broscraft.cda.observers.OverviewUpdateObserver;

import org.bukkit.Material;

import com.broscraft.cda.model.ItemOverviewDTO;

public class ItemOverviewService implements OrderObserver {
    Map<Long, ItemOverviewDTO> itemOverviews = new HashMap<>();
    Set<ItemDTO> allItems = new HashSet<>();

    OverviewUpdateObserver overviewUpdateObserver;

    public ItemOverviewService(OverviewUpdateObserver overviewUpdateObserver) {
        this.overviewUpdateObserver = overviewUpdateObserver;
    }

    public void loadItemOverviews() {
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
        overviewUpdateObserver.onOverviewLoad(itemOverviews.values());
    }

    @Override
    public void onNewOrder(NewOrderDTO newOrderDTO) {
        // TODO: check item exists
        Long itemId = Objects.requireNonNull(newOrderDTO.getItem().getId());
        ItemOverviewDTO itemOverview;
        if (this.itemOverviews.containsKey(itemId)) {
            itemOverview = this.itemOverviews.get(itemId);

            switch (newOrderDTO.getType()) {
                case ASK:
                    Float bestAsk = itemOverview.getBestAsk();
                    int supply = itemOverview.getSupply();
                    itemOverview.setSupply(supply + newOrderDTO.getQuantity());
                    if (bestAsk == null)
                        itemOverview.setBestAsk(newOrderDTO.getPrice());
                    else if (newOrderDTO.getPrice() < bestAsk)
                        itemOverview.setBestAsk(newOrderDTO.getPrice());
                    break;
                case BID:
                    Float bestBid = itemOverview.getBestBid();
                    int demand = itemOverview.getDemand();
                    itemOverview.setDemand(demand + newOrderDTO.getQuantity());
                    if (bestBid == null)
                        itemOverview.setBestBid(newOrderDTO.getPrice());
                    else if (newOrderDTO.getPrice() > bestBid)
                        itemOverview.setBestBid(newOrderDTO.getPrice());
                    break;
            }
        } else {
            itemOverview = new ItemOverviewDTO();
            itemOverview.setItem(newOrderDTO.getItem());
            switch (newOrderDTO.getType()) {
                case ASK:
                    itemOverview.setSupply(newOrderDTO.getQuantity());
                    itemOverview.setDemand(0);
                    itemOverview.setBestAsk(newOrderDTO.getPrice());
                    break;
                case BID:
                    itemOverview.setDemand(newOrderDTO.getQuantity());
                    itemOverview.setSupply(0);
                    itemOverview.setBestBid(newOrderDTO.getPrice());
                    break;
            }
            this.itemOverviews.put(itemId, itemOverview);
        }

        this.overviewUpdateObserver.onOverviewUpdate(itemOverview);
    }

    @Override
    public void onRemoveOrder(OrderDTO orderDTO, Float nextBestPrice) {
        Long itemId = Objects.requireNonNull(orderDTO.getItem().getId());
        ItemOverviewDTO itemOverview = Objects.requireNonNull(this.itemOverviews.get(itemId));
        int orderQuantityRemaining = orderDTO.getQuantity() - orderDTO.getQuantityFilled();
        switch (orderDTO.getType()) {
            case ASK:
                int supply = itemOverview.getSupply();
                itemOverview.setSupply(supply - orderQuantityRemaining);
                itemOverview.setBestAsk(nextBestPrice);
                break;
            case BID:
                int demand = itemOverview.getDemand();
                itemOverview.setDemand(demand - orderQuantityRemaining);
                itemOverview.setBestBid(nextBestPrice);
                break;
        }

        this.overviewUpdateObserver.onOverviewUpdate(itemOverview);
    }
}
