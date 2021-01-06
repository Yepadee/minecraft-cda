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
import com.broscraft.cda.services.comparators.DemandComparator;
import com.broscraft.cda.services.comparators.SupplyComparator;
import com.broscraft.cda.services.utils.FetchOrder;
import com.broscraft.cda.utils.EcoUtils;

public class ItemOverviewService implements OrderObserver {
    private ItemService itemService;
    private OverviewUpdateObserver overviewUpdateObserver;
    private TreeSet<ItemOverviewDTO> orderByBid = new TreeSet<>(new BidPriceComparator());
    private TreeSet<ItemOverviewDTO> orderByAsk = new TreeSet<>(new AskPriceComparator());

    private TreeSet<ItemOverviewDTO> orderByDemand = new TreeSet<>(new DemandComparator());
    private TreeSet<ItemOverviewDTO> orderBySupply = new TreeSet<>(new SupplyComparator());


    public ItemOverviewService(ItemService itemService, OverviewUpdateObserver overviewUpdateObserver) {
        this.itemService = itemService;
        this.overviewUpdateObserver = overviewUpdateObserver;
        Collection<ItemOverviewDTO> itemOverviews = itemService.getItemOverviews();
        itemOverviews.forEach(o -> {
            orderByBid.add(o);
            orderByAsk.add(o);
            orderByDemand.add(o);
            orderBySupply.add(o);
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

    public void getByQuantity(OrderType orderType, FetchOrder fetchOrder, Consumer<List<ItemDTO>> onComplete) {
        CDAPlugin.newChain().asyncFirst(() -> {
            TreeSet<ItemOverviewDTO> orderedSet;
            if (orderType.equals(OrderType.BID)) orderedSet = this.orderByDemand;
            else orderedSet = this.orderBySupply;

            if (fetchOrder.equals(FetchOrder.ASC)) {
                return orderedSet.stream().map(o -> o.getItem()).collect(Collectors.toList());
            } else {
                return orderedSet.descendingSet().stream().map(o -> o.getItem()).collect(Collectors.toList());
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

        switch (newOrderDTO.getType()) {
            case ASK:
                this.orderBySupply.remove(itemOverview);
                BigDecimal bestAsk = itemOverview.getBestAsk();
                int supply = itemOverview.getSupply();
                itemOverview.setSupply(supply + newOrderDTO.getQuantity());
                this.orderBySupply.add(itemOverview);

                this.orderByAsk.remove(itemOverview);
                if (bestAsk == null) {
                    itemOverview.setBestAsk(newOrderDTO.getPrice());
                } else if (EcoUtils.lessThan(newOrderDTO.getPrice(), bestAsk)) {
                    itemOverview.setBestAsk(newOrderDTO.getPrice());
                }
                this.orderByAsk.add(itemOverview);
                
                break;
            case BID:
                this.orderByDemand.remove(itemOverview);
                BigDecimal bestBid = itemOverview.getBestBid();
                int demand = itemOverview.getDemand();
                itemOverview.setDemand(demand + newOrderDTO.getQuantity());
                this.orderByDemand.add(itemOverview);

                this.orderByBid.remove(itemOverview);
                if (bestBid == null) {
                    itemOverview.setBestBid(newOrderDTO.getPrice());
                } else if (EcoUtils.greaterThan(newOrderDTO.getPrice(), bestBid)) {
                    itemOverview.setBestBid(newOrderDTO.getPrice());
                }
                this.orderByBid.add(itemOverview);
 
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
                this.orderBySupply.remove(itemOverview);
                int supply = itemOverview.getSupply();
                itemOverview.setSupply(supply - orderQuantityRemaining);
                this.orderBySupply.add(itemOverview);

                this.orderByAsk.remove(itemOverview);
                itemOverview.setBestAsk(nextBestPrice);
                this.orderByAsk.add(itemOverview);

                break;
            case BID:
                this.orderByDemand.remove(itemOverview);
                int demand = itemOverview.getDemand();
                itemOverview.setDemand(demand - orderQuantityRemaining);
                this.orderByDemand.add(itemOverview);

                this.orderByBid.remove(itemOverview);
                itemOverview.setBestBid(nextBestPrice);
                this.orderByBid.add(itemOverview);
                
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
                this.orderBySupply.remove(itemOverview);
                int supply = itemOverview.getSupply();
                itemOverview.setSupply(supply - numFilled);
                this.orderBySupply.add(itemOverview);

                this.orderByAsk.remove(itemOverview); 
                itemOverview.setBestAsk(nextBestPrice);
                this.orderByAsk.add(itemOverview);
                break;
            case BID:
                this.orderByDemand.remove(itemOverview);
                int demand = itemOverview.getDemand();
                itemOverview.setDemand(demand - numFilled);
                this.orderByDemand.add(itemOverview);
                
                this.orderByBid.remove(itemOverview);
                itemOverview.setBestBid(nextBestPrice);
                this.orderByBid.add(itemOverview);
                break;
        }

        this.overviewUpdateObserver.onOverviewUpdate(itemOverview);
    }

}
