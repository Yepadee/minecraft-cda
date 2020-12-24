package com.broscraft.cda.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.broscraft.cda.CDAPlugin;
import com.broscraft.cda.model.items.ItemDTO;
import com.broscraft.cda.model.orders.OrderDTO;
import com.broscraft.cda.model.orders.OrderType;
import com.broscraft.cda.model.orders.grouped.GroupedAskDTO;
import com.broscraft.cda.model.orders.grouped.GroupedBidDTO;
import com.broscraft.cda.model.orders.grouped.GroupedOrdersDTO;
import com.broscraft.cda.model.orders.input.NewOrderDTO;
import com.broscraft.cda.observers.OrderObserver;

import org.bukkit.Material;


public class OrderService {
    private List<OrderObserver> observers = new ArrayList<>();
    private ItemService itemService;

    public OrderService(ItemService itemService) {
        this.itemService = itemService;
    }

    public void addObserver(OrderObserver observer) {
        this.observers.add(observer);
    }

    private void notifyNewOrderObservers(NewOrderDTO newOrderDTO) {
        observers.forEach(o -> o.onNewOrder(newOrderDTO));
        
    }

    private void notifyRemoveOrderObservers(OrderDTO orderDTO, Float nextBestPrice) {
        observers.forEach(o -> o.onRemoveOrder(orderDTO, nextBestPrice));

    }

    public void getOrders(Long itemId, Consumer<GroupedOrdersDTO> onComplete) {
        CDAPlugin.newChain()
        .asyncFirst(() -> {
            // TODO: Actually load orders
            System.out.println("Loading orders for item " + itemId + "!");
            List<GroupedBidDTO> bids = new ArrayList<>();
            List<GroupedAskDTO> asks = new ArrayList<>();
            for (int i = 1; i <= 15; ++i) {
                GroupedBidDTO bid1 = new GroupedBidDTO();
                bid1.setPrice(i*2.0f);
                bid1.setQuantity(100 / i);
                bids.add(bid1);
        
                GroupedAskDTO ask1 = new GroupedAskDTO();
                ask1.setPrice(i * 3.0f);
                ask1.setQuantity(120 / i);
                asks.add(ask1);
            }

            GroupedAskDTO ask1 = new GroupedAskDTO();
            ask1.setPrice(20.0f);
            ask1.setQuantity(120);
            asks.add(ask1);

            return new GroupedOrdersDTO().groupedBids(bids).groupedAsks(asks);
    
        }).abortIfNull()
        .syncLast(result -> onComplete.accept(result))
        .execute();
    }

    public void getPlayerOrders(UUID playerUUID, Consumer<List<OrderDTO>> onComplete) {
        CDAPlugin.newChain()
        .asyncFirst(() -> {
            // TODO: Actually load orders
            System.out.println("Loading orders for player " + playerUUID + "!");
            List<OrderDTO> orderDTOs = new ArrayList<>();
            orderDTOs.add(
                new OrderDTO()
                .type(OrderType.ASK)
                .price(10.3f)
                .quantity(3)
                .quantityFilled(0)
                .item(
                    new ItemDTO().id(1L).material(Material.STONE)
                )
            );

            orderDTOs.add(
                new OrderDTO()
                .type(OrderType.BID)
                .price(5.5f)
                .quantity(3)
                .quantityFilled(1)
                .item(
                    new ItemDTO().id(2L).material(Material.DIAMOND_BLOCK)
                )
            );

            return orderDTOs;
    
        }).abortIfNull()
        .syncLast(result -> onComplete.accept(result))
        .execute();
    }

    public void submitOrder(NewOrderDTO newOrderDTO) {
        CDAPlugin.newSharedChain("submitOrder").current(() -> {        
            Long itemId = itemService.getItemId(newOrderDTO.getItem());
            newOrderDTO.getItem().setId(itemId);

            // TODO: Submit order request

            notifyNewOrderObservers(newOrderDTO);
        }).execute();
    }

    public void cancelOrder(OrderDTO orderDTO, Runnable onComplete) {
        CDAPlugin.newSharedChain("cancelOrder").current(() -> {
            //TODO: load next best price
            Float nextBestPrice = 3.3f;
            System.out.println("Cancelling order " + orderDTO.getItem().getId());
            notifyRemoveOrderObservers(orderDTO, nextBestPrice);
            onComplete.run();
        }).execute();
    }

    public void collectOrder(OrderDTO orderDTO, Runnable onComplete) {

    }
}

