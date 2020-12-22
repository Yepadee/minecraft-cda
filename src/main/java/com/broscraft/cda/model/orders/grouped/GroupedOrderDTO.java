package com.broscraft.cda.model.orders.grouped;

import com.broscraft.cda.model.orders.grouped.visitors.GroupedOrderVisitor;

public abstract class GroupedOrderDTO {
    private Float price;
    private Integer quantity;

    public Float getPrice() {
        return this.price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public abstract void accept(GroupedOrderVisitor v);

}
