package com.broscraft.cda.dtos.orders.grouped;


import com.broscraft.cda.dtos.orders.grouped.visitors.GroupedOrderVisitor;

public abstract class GroupedOrderDTO {
    private Float price;
    private Integer quantity;

    public GroupedOrderDTO() {
    }

    public GroupedOrderDTO(Float price, Integer quantity) {
        this.price = price;
        this.quantity = quantity;
    }

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

    public GroupedOrderDTO price(Float price) {
        this.price = price;
        return this;
    }

    public GroupedOrderDTO quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
            " price='" + getPrice() + "'" +
            ", quantity='" + getQuantity() + "'" +
            "}";
    }

    public abstract void accept(GroupedOrderVisitor v);

}
