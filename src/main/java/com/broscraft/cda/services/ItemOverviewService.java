package com.broscraft.cda.services;

import java.util.Objects;

import com.broscraft.cda.model.orders.OrderDTO;
import com.broscraft.cda.model.orders.input.NewOrderDTO;
import com.broscraft.cda.observers.OrderObserver;
import com.broscraft.cda.observers.OverviewUpdateObserver;
import com.broscraft.cda.model.ItemOverviewDTO;
import com.broscraft.cda.model.items.ItemDTO;

public class ItemOverviewService implements OrderObserver {
    private ItemService itemService;
    private OverviewUpdateObserver overviewUpdateObserver;

    public ItemOverviewService(
        ItemService itemService,
        OverviewUpdateObserver overviewUpdateObserver
    ) {
        this.itemService = itemService;
        this.overviewUpdateObserver = overviewUpdateObserver;
        
        overviewUpdateObserver.onOverviewLoad(itemService.getItemOverviews());
    }

    @Override
    public void onNewOrder(NewOrderDTO newOrderDTO) {
        ItemDTO itemDTO = newOrderDTO.getItem();
        ItemOverviewDTO itemOverview;
        if (itemService.exists(itemDTO)) {
            Long itemId = itemService.getItemId(itemDTO);
            itemOverview = itemService.getItemOverview(itemId);

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
            Long itemId = itemService.createItem(itemDTO);
            itemDTO.setId(itemId); // Set items id!

            itemOverview = new ItemOverviewDTO();
            itemOverview.setItem(itemDTO);
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
            itemService.addItemOverview(itemOverview);
        }

        overviewUpdateObserver.onOverviewUpdate(itemOverview);
    }

    @Override
    public void onRemoveOrder(OrderDTO orderDTO, Float nextBestPrice) {
        Long itemId = Objects.requireNonNull(orderDTO.getItem().getId());
        ItemOverviewDTO itemOverview = Objects.requireNonNull(itemService.getItemOverview(itemId));
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
