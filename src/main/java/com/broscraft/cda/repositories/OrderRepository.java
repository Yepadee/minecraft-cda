package com.broscraft.cda.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.broscraft.cda.model.items.ItemDTO;
import com.broscraft.cda.model.orders.OrderDTO;
import com.broscraft.cda.model.orders.OrderType;
import com.broscraft.cda.model.orders.grouped.GroupedAskDTO;
import com.broscraft.cda.model.orders.grouped.GroupedBidDTO;
import com.broscraft.cda.model.orders.grouped.GroupedOrdersDTO;
import com.broscraft.cda.model.orders.input.NewOrderDTO;

import org.bukkit.Material;

public class OrderRepository {
    public GroupedOrdersDTO getOrders(Long itemId) {
        // TODO: Actually load orders for the item
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
    }

    public List<OrderDTO> getPlayerOrders(UUID playerUUID) {
        // TODO: Actually load orders
        System.out.println("Loading orders for player " + playerUUID + "!");
        List<OrderDTO> orderDTOs = new ArrayList<>();
        orderDTOs.add(new OrderDTO().id(1L).type(OrderType.ASK).price(10.3f).quantity(3).quantityFilled(2)
            .playerUUID(UUID.fromString("ff5b7624-5859-455e-b708-e7cb227e114d"))
            .toCollect(2).item(new ItemDTO().id(1L).material(Material.STONE)));

        orderDTOs.add(new OrderDTO().id(2L).type(OrderType.BID).price(5.5f).quantity(3).quantityFilled(3)
            .playerUUID(UUID.fromString("ff5b7624-5859-455e-b708-e7cb227e114d"))
            .toCollect(1).item(new ItemDTO().id(2L).material(Material.DIAMOND_BLOCK)));

        return orderDTOs;
    }

    public void createOrder(NewOrderDTO newOrderDTO) {
        System.out.println("Created new order:" + newOrderDTO);
    }
}
