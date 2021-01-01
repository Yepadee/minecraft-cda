package com.broscraft.cda.services;

import java.math.BigDecimal;
import java.util.Objects;

import com.broscraft.cda.dtos.ItemOverviewDTO;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.orders.OrderDTO;
import com.broscraft.cda.dtos.orders.input.NewOrderDTO;
import com.broscraft.cda.dtos.transaction.TransactionSummaryDTO;
import com.broscraft.cda.observers.OrderObserver;
import com.broscraft.cda.observers.OverviewUpdateObserver;
import com.broscraft.cda.utils.EcoUtils;

public class ItemOverviewService implements OrderObserver {
    private ItemService itemService;
    private OverviewUpdateObserver overviewUpdateObserver;

    public ItemOverviewService(ItemService itemService, OverviewUpdateObserver overviewUpdateObserver) {
        this.itemService = itemService;
        this.overviewUpdateObserver = overviewUpdateObserver;
        overviewUpdateObserver.onOverviewLoad(itemService.getItemOverviews());
    }

    @Override
    public void onNewOrder(NewOrderDTO newOrderDTO) {
        ItemDTO itemDTO = newOrderDTO.getItem();

        Long itemId = itemService.getItemId(itemDTO);
        ItemOverviewDTO itemOverview = itemService.getItemOverview(itemId);

        switch (newOrderDTO.getType()) {
            case ASK:
                BigDecimal bestAsk = itemOverview.getBestAsk();
                int supply = itemOverview.getSupply();
                itemOverview.setSupply(supply + newOrderDTO.getQuantity());
                if (bestAsk == null)
                    itemOverview.setBestAsk(newOrderDTO.getPrice());
                else if (EcoUtils.lessThan(newOrderDTO.getPrice(), bestAsk))
                    itemOverview.setBestAsk(newOrderDTO.getPrice());
                break;
            case BID:
                BigDecimal bestBid = itemOverview.getBestBid();
                int demand = itemOverview.getDemand();
                itemOverview.setDemand(demand + newOrderDTO.getQuantity());
                if (bestBid == null)
                    itemOverview.setBestBid(newOrderDTO.getPrice());
                else if (EcoUtils.greaterThan(newOrderDTO.getPrice(), bestBid))
                    itemOverview.setBestBid(newOrderDTO.getPrice());
                break;
        }

        overviewUpdateObserver.onOverviewUpdate(itemOverview);
    }

    @Override
    public void onRemoveOrder(OrderDTO orderDTO, BigDecimal nextBestPrice) {

        Long itemId = Objects.requireNonNull(itemService.getItemId(orderDTO.getItem()));
        ItemOverviewDTO itemOverview = Objects.requireNonNull(itemService.getItemOverview(itemId));
        int orderQuantityRemaining = orderDTO.getQuantityUnfilled();
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

    @Override
    public void onFillOrder(TransactionSummaryDTO transactionSummaryDTO) {
        Long itemId = transactionSummaryDTO.getItemId();
        ItemOverviewDTO itemOverview = Objects.requireNonNull(itemService.getItemOverview(itemId));
        int numFilled = transactionSummaryDTO.getNumFilled();
        BigDecimal nextBestPrice = transactionSummaryDTO.getNewBestPrice();
        switch (transactionSummaryDTO.getOrderType()) {
            case ASK:
                int supply = itemOverview.getSupply();
                itemOverview.setSupply(supply - numFilled);
                itemOverview.setBestAsk(nextBestPrice);
                break;
            case BID:
                int demand = itemOverview.getDemand();
                itemOverview.setDemand(demand - numFilled);
                itemOverview.setBestBid(nextBestPrice);
                break;
        }

        this.overviewUpdateObserver.onOverviewUpdate(itemOverview);
    }

}
