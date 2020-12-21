package com.broscraft.cda.repositories;

import java.util.ArrayList;
import java.util.List;

import com.broscraft.cda.model.orders.grouped.OrdersDTO;
import com.broscraft.cda.model.orders.input.NewOrderDTO;
import com.broscraft.cda.observers.NewOrderObserver;


public class OrderRepository {
    private List<NewOrderObserver> observers = new ArrayList<>();
    private ItemRepository itemRepository;

    public OrderRepository(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

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
        Long itemId = itemRepository.getItemId(newOrderDTO.getItem());
        newOrderDTO.getItem().setId(itemId);
        notifyObservers(newOrderDTO);
    }
}

