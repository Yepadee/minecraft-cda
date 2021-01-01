package com.broscraft.cda.observers;

import java.math.BigDecimal;

import com.broscraft.cda.dtos.orders.OrderDTO;
import com.broscraft.cda.dtos.orders.input.NewOrderDTO;
import com.broscraft.cda.dtos.transaction.TransactionSummaryDTO;

public interface OrderObserver {
    public void onNewOrder(NewOrderDTO newOrderDTO);
    public void onFillOrder(TransactionSummaryDTO transactionSummaryDTO);
    public void onRemoveOrder(OrderDTO orderDTO, BigDecimal nextBestPrice);
}
