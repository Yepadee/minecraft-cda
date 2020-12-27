package com.broscraft.cda.observers;

import com.broscraft.cda.dtos.orders.OrderDTO;
import com.broscraft.cda.dtos.orders.input.NewOrderDTO;

public interface OrderObserver {
    public void onNewOrder(NewOrderDTO newOrderDTO);
    public void onRemoveOrder(OrderDTO orderDTO, Float nextBestPrice);
}
