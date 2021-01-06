package com.broscraft.cda.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.broscraft.cda.CDAPlugin;
import com.broscraft.cda.dtos.ItemOverviewDTO;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.orders.OrderDTO;
import com.broscraft.cda.dtos.orders.OrderType;
import com.broscraft.cda.dtos.orders.input.NewOrderDTO;
import com.broscraft.cda.dtos.transaction.TransactionSummaryDTO;
import com.broscraft.cda.observers.OrderObserver;
import com.broscraft.cda.observers.OverviewUpdateObserver;
import com.broscraft.cda.services.comparators.AskPriceComparator;
import com.broscraft.cda.services.comparators.BidPriceComparator;
import com.broscraft.cda.services.utils.FetchOrder;
import com.broscraft.cda.utils.EcoUtils;

public class ItemOverviewService implements OrderObserver {
    private ItemService itemService;
    private OverviewUpdateObserver overviewUpdateObserver;
    private TreeSet<ItemOverviewDTO> orderByBid = new TreeSet<>(new BidPriceComparator());
    private TreeSet<ItemOverviewDTO> orderByAsk = new TreeSet<>(new AskPriceComparator());


    public ItemOverviewService(ItemService itemService, OverviewUpdateObserver overviewUpdateObserver) {
        this.itemService = itemService;
        this.overviewUpdateObserver = overviewUpdateObserver;
        Collection<ItemOverviewDTO> itemOverviews = itemService.getItemOverviews();
        itemOverviews.forEach(o -> {
            orderByBid.add(o);
            orderByAsk.add(o);
        });
        overviewUpdateObserver.onOverviewLoad(itemOverviews);
    }

    public void getByPrice(OrderType orderType, FetchOrder fetchOrder, Consumer<List<ItemDTO>> onComplete) {
        CDAPlugin.newChain().asyncFirst(() -> {
            TreeSet<ItemOverviewDTO> orderedSet;
            if (orderType.equals(OrderType.BID)) orderedSet = this.orderByBid;
            else orderedSet = this.orderByAsk;

            if (fetchOrder.equals(FetchOrder.ASC)) {
                return orderedSet.stream().map(o -> o.getItem()).collect(Collectors.toList());
            } else {
                List<ItemDTO> nullPriceItems = new ArrayList<>();
                List<ItemDTO> allItems = new ArrayList<>();

                if (orderType.equals(OrderType.BID)) {
                    orderedSet.descendingSet().forEach(o -> {
                        if (o.getBestBid() == null) nullPriceItems.add(o.getItem());
                        else allItems.add(o.getItem());
                    });
                } else {
                    orderedSet.descendingSet().forEach(o -> {
                        if (o.getBestAsk() == null) nullPriceItems.add(o.getItem());
                        else allItems.add(o.getItem());
                    });
                }

                allItems.addAll(nullPriceItems);
                return allItems;
            }
        })
        .syncLast(orderedItems -> onComplete.accept(orderedItems))
        .execute();
    }

    @Override
    public void onNewOrder(NewOrderDTO newOrderDTO) {
        ItemDTO itemDTO = newOrderDTO.getItem();

        Long itemId = itemService.getItemId(itemDTO);
        ItemOverviewDTO itemOverview = itemService.getItemOverview(itemId);

        boolean bestBidUpdated = false;
        boolean bestAskUpdated = false;

        switch (newOrderDTO.getType()) {
            case ASK:
                BigDecimal bestAsk = itemOverview.getBestAsk();
                int supply = itemOverview.getSupply();
                itemOverview.setSupply(supply + newOrderDTO.getQuantity());
                if (bestAsk == null) {
                    itemOverview.setBestAsk(newOrderDTO.getPrice());
                    bestAskUpdated = true;
                } else if (EcoUtils.lessThan(newOrderDTO.getPrice(), bestAsk)) {
                    itemOverview.setBestAsk(newOrderDTO.getPrice());
                    bestAskUpdated = true;
                }
                break;
            case BID:
                BigDecimal bestBid = itemOverview.getBestBid();
                int demand = itemOverview.getDemand();
                itemOverview.setDemand(demand + newOrderDTO.getQuantity());
                if (bestBid == null) {
                    itemOverview.setBestBid(newOrderDTO.getPrice());
                    bestBidUpdated = true;
                } else if (EcoUtils.greaterThan(newOrderDTO.getPrice(), bestBid)) {
                    itemOverview.setBestBid(newOrderDTO.getPrice());
                    bestBidUpdated = true;
                }
                break;
        }

        if (bestBidUpdated) {
            this.orderByBid.remove(itemOverview);
            this.orderByBid.add(itemOverview);
        }

        if (bestAskUpdated) {
            this.orderByAsk.remove(itemOverview);
            this.orderByAsk.add(itemOverview);
        }

        overviewUpdateObserver.onOverviewUpdate(itemOverview);
    }

    @Override
    public void onRemoveOrder(OrderDTO orderDTO, BigDecimal nextBestPrice) {

        Long itemId = Objects.requireNonNull(itemService.getItemId(orderDTO.getItem()));
        ItemOverviewDTO itemOverview = Objects.requireNonNull(itemService.getItemOverview(itemId));
        int orderQuantityRemaining = orderDTO.getQuantityUnfilled();

        boolean bestBidUpdated = false;
        boolean bestAskUpdated = false;

        switch (orderDTO.getType()) {
            case ASK:
                int supply = itemOverview.getSupply();
                itemOverview.setSupply(supply - orderQuantityRemaining);
                itemOverview.setBestAsk(nextBestPrice);

                bestAskUpdated = !itemOverview.getBestAsk().equals(nextBestPrice);

                break;
            case BID:
                int demand = itemOverview.getDemand();
                itemOverview.setDemand(demand - orderQuantityRemaining);
                itemOverview.setBestBid(nextBestPrice);

                bestBidUpdated = !itemOverview.getBestBid().equals(nextBestPrice);
                
                break;
        }

        if (bestBidUpdated) {
            this.orderByBid.remove(itemOverview);
            this.orderByBid.add(itemOverview);
        }

        if (bestAskUpdated) {
            this.orderByAsk.remove(itemOverview);
            this.orderByAsk.add(itemOverview);
        }

        this.overviewUpdateObserver.onOverviewUpdate(itemOverview);
    }

    @Override
    public void onFillOrder(TransactionSummaryDTO transactionSummaryDTO) {
        Long itemId = transactionSummaryDTO.getItemId();
        ItemOverviewDTO itemOverview = Objects.requireNonNull(itemService.getItemOverview(itemId));
        int numFilled = transactionSummaryDTO.getNumFilled();
        BigDecimal nextBestPrice = transactionSummaryDTO.getNewBestPrice();

        boolean bestBidUpdated = false;
        boolean bestAskUpdated = false;

        switch (transactionSummaryDTO.getOrderType()) {
            case ASK:
                int supply = itemOverview.getSupply();
                itemOverview.setSupply(supply - numFilled);
                itemOverview.setBestAsk(nextBestPrice);

                bestAskUpdated = !itemOverview.getBestAsk().equals(nextBestPrice);

                break;
            case BID:
                int demand = itemOverview.getDemand();
                itemOverview.setDemand(demand - numFilled);
                itemOverview.setBestBid(nextBestPrice);

                bestBidUpdated = !itemOverview.getBestBid().equals(nextBestPrice);

                break;
        }

        if (bestBidUpdated) {
            this.orderByBid.remove(itemOverview);
            this.orderByBid.add(itemOverview);
        }

        if (bestAskUpdated) {
            this.orderByAsk.remove(itemOverview);
            this.orderByAsk.add(itemOverview);
        }

        this.overviewUpdateObserver.onOverviewUpdate(itemOverview);
    }

}
