package com.broscraft.cda.repositories;

import java.util.ArrayList;
import java.util.List;

import com.broscraft.cda.model.orders.AskDTO;
import com.broscraft.cda.model.orders.BidDTO;
import com.broscraft.cda.model.orders.OrderDTO;

public class OrderRepository {
    List<OrderDTO> getOrders(Long itemId) {
        List<OrderDTO> orders = new ArrayList<>();
        
        OrderDTO bid1 = new BidDTO();
        bid1.setPrice(15.0f);
        bid1.setQuantity(100);
        orders.add(bid1);

        OrderDTO ask1 = new AskDTO();
        ask1.setPrice(20.0f);
        ask1.setQuantity(120);
        orders.add(ask1);

        return orders;
    }

    public void submitOrder(OrderDTO orderDTO) {

    }
}

