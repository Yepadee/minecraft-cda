package com.broscraft.cda.dtos.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.broscraft.cda.dtos.orders.OrderDTO;
import com.broscraft.cda.dtos.orders.OrderType;

public class TransactionSummaryDTO {
    List<OrderDTO> affectedOrders = new ArrayList<>();
    Long itemId;
    OrderType orderType;
    Integer numFilled;
    Float newBestPrice;

    public TransactionSummaryDTO() {
    }

    public TransactionSummaryDTO(List<OrderDTO> affectedOrders, Long itemId, OrderType orderType, Integer numFilled, Float newBestPrice) {
        this.affectedOrders = affectedOrders;
        this.itemId = itemId;
        this.orderType = orderType;
        this.numFilled = numFilled;
        this.newBestPrice = newBestPrice;
    }

    public List<OrderDTO> getAffectedOrders() {
        return this.affectedOrders;
    }

    public void setAffectedOrders(List<OrderDTO> affectedOrders) {
        this.affectedOrders = affectedOrders;
    }

    public Long getItemId() {
        return this.itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public OrderType getOrderType() {
        return this.orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Integer getNumFilled() {
        return this.numFilled;
    }

    public void setNumFilled(Integer numFilled) {
        this.numFilled = numFilled;
    }

    public Float getNewBestPrice() {
        return this.newBestPrice;
    }

    public void setNewBestPrice(Float newBestPrice) {
        this.newBestPrice = newBestPrice;
    }

    public TransactionSummaryDTO affectedOrders(List<OrderDTO> affectedOrders) {
        this.affectedOrders = affectedOrders;
        return this;
    }

    public TransactionSummaryDTO itemId(Long itemId) {
        this.itemId = itemId;
        return this;
    }

    public TransactionSummaryDTO orderType(OrderType orderType) {
        this.orderType = orderType;
        return this;
    }

    public TransactionSummaryDTO numFilled(Integer numFilled) {
        this.numFilled = numFilled;
        return this;
    }

    public TransactionSummaryDTO newBestPrice(Float newBestPrice) {
        this.newBestPrice = newBestPrice;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof TransactionSummaryDTO)) {
            return false;
        }
        TransactionSummaryDTO transactionSummaryDTO = (TransactionSummaryDTO) o;
        return Objects.equals(affectedOrders, transactionSummaryDTO.affectedOrders) && Objects.equals(itemId, transactionSummaryDTO.itemId) && Objects.equals(orderType, transactionSummaryDTO.orderType) && Objects.equals(numFilled, transactionSummaryDTO.numFilled) && Objects.equals(newBestPrice, transactionSummaryDTO.newBestPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(affectedOrders, itemId, orderType, numFilled, newBestPrice);
    }

    @Override
    public String toString() {
        return "{" +
            " affectedOrders='" + getAffectedOrders() + "'" +
            ", itemId='" + getItemId() + "'" +
            ", orderType='" + getOrderType() + "'" +
            ", numFilled='" + getNumFilled() + "'" +
            ", newBestPrice='" + getNewBestPrice() + "'" +
            "}";
    }

}
