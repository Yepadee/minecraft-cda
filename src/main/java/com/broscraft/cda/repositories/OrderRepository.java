package com.broscraft.cda.repositories;

import java.util.ArrayList;
import java.util.List;

import com.broscraft.cda.model.orders.grouped.OrdersDTO;
import com.broscraft.cda.model.orders.input.NewOrderDTO;
import com.broscraft.cda.observers.NewOrderObserver;


public class OrderRepository {
    private List<NewOrderObserver> observers = new ArrayList<>();

    //TEMP:
    Long lastId = 3L;

    public void addObserver(NewOrderObserver observer) {
        this.observers.add(observer);
    }

    private void notifyObservers(NewOrderDTO newOrderDTO) {
        observers.forEach(o -> o.onNewOrder(newOrderDTO));
    }

    List<OrdersDTO> getOrders(Long itemId) {
        List<OrdersDTO> orders = new ArrayList<>();
        
        OrdersDTO bid1 = new OrdersDTO();
        bid1.setPrice(15.0f);
        bid1.setQuantity(100);
        orders.add(bid1);

        OrdersDTO ask1 = new OrdersDTO();
        ask1.setPrice(20.0f);
        ask1.setQuantity(120);
        orders.add(ask1);

        return orders;
    }

    public void submitOrder(NewOrderDTO newOrderDTO) {
        // TODO:
        // Check if item exisists in database, if not insert it and retrieve the id.
        // The item in newOrderDTO will have it's id set here.
        newOrderDTO.getItem().setId(lastId); // Temp
        notifyObservers(newOrderDTO);
        lastId ++;
    }
}

