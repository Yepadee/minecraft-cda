package com.broscraft.cda.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.broscraft.cda.observers.OrderUpdateObserver;
import com.broscraft.cda.utils.InventoryUtils;
import com.earth2me.essentials.api.Economy;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;

public class OrderService {
    private OrderObserver orderObserver;
    private Map<UUID, OrderUpdateObserver> orderUpdateObservers = new HashMap<>();
    private ItemService itemService;

    public OrderService(ItemService itemService, OrderObserver orderObserver) {
        this.itemService = itemService;
        this.orderObserver = orderObserver;
    }

    public void addOrderUpdateObserver(UUID playerUUID, OrderUpdateObserver orderUpdateObserver) {
        orderUpdateObservers.put(playerUUID, orderUpdateObserver);
    }

    public void removeOrderUpdateObserver(UUID playerUUID) {
        orderUpdateObservers.remove(playerUUID);
    }

    private void notifyOrderUpdateObserver(UUID playerUUID, OrderDTO orderDTO) {
        OrderUpdateObserver o = orderUpdateObservers.get(playerUUID);
        if (o != null) {
            o.onOrderUpdate(orderDTO);
        }
    }

    private void notifyNewOrderObserver(NewOrderDTO newOrderDTO) {
        orderObserver.onNewOrder(newOrderDTO);
    }

    private void notifyRemoveOrderObserver(OrderDTO orderDTO, Float nextBestPrice) {
        orderObserver.onRemoveOrder(orderDTO, nextBestPrice);
    }

    public void getOrders(Long itemId, Consumer<GroupedOrdersDTO> onComplete) {
        CDAPlugin.newChain().asyncFirst(() -> {
            // TODO: Actually load orders
            System.out.println("Loading orders for item " + itemId + "!");
            List<GroupedBidDTO> bids = new ArrayList<>();
            List<GroupedAskDTO> asks = new ArrayList<>();
            for (int i = 1; i <= 15; ++i) {
                GroupedBidDTO bid1 = new GroupedBidDTO();
                bid1.setPrice(3.0f / i);
                bid1.setQuantity(100 / i);
                bids.add(bid1);

                GroupedAskDTO ask1 = new GroupedAskDTO();
                ask1.setPrice(i * 3.0f + 0.1f);
                ask1.setQuantity(120 / i);
                asks.add(ask1);
            }

            GroupedAskDTO ask1 = new GroupedAskDTO();
            ask1.setPrice(100.0f);
            ask1.setQuantity(120);
            asks.add(ask1);

            return new GroupedOrdersDTO().groupedBids(bids).groupedAsks(asks);

        }).abortIfNull().syncLast(result -> onComplete.accept(result)).execute();
    }

    public void getPlayerOrders(UUID playerUUID, Consumer<List<OrderDTO>> onComplete) {
        CDAPlugin.newChain().asyncFirst(() -> {
            // TODO: Actually load orders
            System.out.println("Loading orders for player " + playerUUID + "!");
            List<OrderDTO> orderDTOs = new ArrayList<>();
            orderDTOs.add(new OrderDTO().type(OrderType.ASK).price(10.3f).quantity(3).quantityFilled(2).toCollect(2)
                    .item(new ItemDTO().id(1L).material(Material.STONE)));

            orderDTOs.add(new OrderDTO().type(OrderType.BID).price(5.5f).quantity(3).quantityFilled(3).toCollect(1)
                    .item(new ItemDTO().id(2L).material(Material.DIAMOND_BLOCK)));

            return orderDTOs;

        }).abortIfNull().syncLast(result -> onComplete.accept(result)).execute();
    }

    public void submitOrder(NewOrderDTO newOrderDTO) {
        CDAPlugin.newSharedChain("submitOrder").async(() -> {
            Long itemId = itemService.getItemId(newOrderDTO.getItem());
            newOrderDTO.getItem().setId(itemId);

            // TODO: Submit order request

            notifyNewOrderObserver(newOrderDTO);
        }).execute();
    }

    public void cancelOrder(OrderDTO orderDTO, Runnable onComplete) {
        CDAPlugin.newSharedChain("cancelOrder").async(() -> {
            // TODO: load next best price
            Float nextBestPrice = 3.3f;
            System.out.println("Cancelling order " + orderDTO.getItem().getId());
            notifyRemoveOrderObserver(orderDTO, nextBestPrice);
            onComplete.run();
        }).execute();
    }

    public void collectOrder(HumanEntity player, OrderDTO orderDTO, Runnable onComplete) {
        // NOTE: Inventory must be checked before submitting request or else items will
        // be lost.
        CDAPlugin.newChain().async(() -> {
            player.sendMessage("Collecting order...");
            int numToGive;
            int numToCollect = orderDTO.getToCollect();
            boolean failure = false;
            if (orderDTO.getType().equals(OrderType.BID)) {
                int invSpace = InventoryUtils.getInvSpace(player);
                numToGive = invSpace > numToCollect ? numToCollect : invSpace;
            } else {
                numToGive = numToCollect;
                BigDecimal toPay = BigDecimal.valueOf(numToGive * orderDTO.getPrice());
                try {
                    Economy.add(player.getUniqueId(), toPay);
                } catch (Exception e) {
                    // TODO: undo collection on payment fail.
                    player.sendMessage("Error collecting money");
                    failure = true;
                }
            }

            if (!failure) {
                orderDTO.setToCollect(numToCollect - numToGive);
                notifyOrderUpdateObserver(player.getUniqueId(), orderDTO);
                onComplete.run();
            } 
            
            // TOOD: submit request collecting items

        }).execute();
    }
}

