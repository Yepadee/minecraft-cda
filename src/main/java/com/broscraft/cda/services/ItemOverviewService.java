package com.broscraft.cda.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.broscraft.cda.model.orders.OrderDTO;
import com.broscraft.cda.model.orders.input.NewOrderDTO;
import com.broscraft.cda.observers.OrderObserver;
import com.broscraft.cda.observers.OverviewUpdateObserver;
import com.broscraft.cda.repositories.ItemRepository;

import com.broscraft.cda.CDAPlugin;
import com.broscraft.cda.model.ItemOverviewDTO;

public class ItemOverviewService implements OrderObserver {
    private ItemRepository itemRepository;
    Map<Long, ItemOverviewDTO> itemOverviews = new HashMap<>();

    OverviewUpdateObserver overviewUpdateObserver;

    public ItemOverviewService(
        ItemRepository itemRepository,
        OverviewUpdateObserver overviewUpdateObserver
    ) {
        this.itemRepository = itemRepository;
        this.overviewUpdateObserver = overviewUpdateObserver;
    }

    public void loadItemOverviews() {
        CDAPlugin.newChain()
        .asyncFirst(() -> {
            return itemRepository.getAllItemOverviews();
        })
        .syncLast(itemOverviews -> {
            this.itemOverviews = itemOverviews;
            overviewUpdateObserver.onOverviewLoad(itemOverviews.values());
        }).execute();
    }

    @Override
    public void onNewOrder(NewOrderDTO newOrderDTO) {
        Long itemId = Objects.requireNonNull(newOrderDTO.getItem().getId());
        System.out.println("New order itemID: " + itemId);
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
