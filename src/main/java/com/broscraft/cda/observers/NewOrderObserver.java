package com.broscraft.cda.observers;

import com.broscraft.cda.model.orders.input.NewOrderDTO;

public interface NewOrderObserver {
    public void onNewOrder(NewOrderDTO newOrderDTO);
}
