package com.broscraft.cda.observers;

import com.broscraft.cda.dtos.orders.OrderDTO;

public interface OrderUpdateObserver {
    public void onOrderUpdate(OrderDTO orderDTO);
}
