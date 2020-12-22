package com.broscraft.cda.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.broscraft.cda.CDAPlugin;
import com.broscraft.cda.model.orders.grouped.GroupedAskDTO;
import com.broscraft.cda.model.orders.grouped.GroupedBidDTO;
import com.broscraft.cda.model.orders.grouped.GroupedOrdersDTO;
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

    public void getOrders(Long itemId, Consumer<GroupedOrdersDTO> onComplete) {
        CDAPlugin.newChain()
        .asyncFirst(() -> {
            // TODO: Actually load orders
            System.out.println("Loading orders for item " + itemId + "!");
            List<GroupedBidDTO> bids = new ArrayList<>();
            List<GroupedAskDTO> asks = new ArrayList<>();
            for (int i = 1; i <= 20; ++i) {
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

    public void submitOrder(NewOrderDTO newOrderDTO) {
        Long itemId = itemRepository.getItemId(newOrderDTO.getItem());
        newOrderDTO.getItem().setId(itemId);

        // TODO: Submit order request

        notifyObservers(newOrderDTO);
    }
}

