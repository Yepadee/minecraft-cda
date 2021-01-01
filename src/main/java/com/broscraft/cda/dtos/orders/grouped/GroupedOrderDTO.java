package com.broscraft.cda.dtos.orders.grouped;


import java.math.BigDecimal;
import java.util.Objects;

import com.broscraft.cda.dtos.orders.grouped.visitors.GroupedOrderVisitor;

public abstract class GroupedOrderDTO {
    private BigDecimal price;
    private Integer quantity;

    public GroupedOrderDTO() {
    }

    public GroupedOrderDTO(BigDecimal price, Integer quantity) {
        this.price = price;
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public GroupedOrderDTO price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public GroupedOrderDTO quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof GroupedOrderDTO)) {
            return false;
        }
        GroupedOrderDTO groupedOrderDTO = (GroupedOrderDTO) o;
        return Objects.equals(price, groupedOrderDTO.price) && Objects.equals(quantity, groupedOrderDTO.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, quantity);
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
