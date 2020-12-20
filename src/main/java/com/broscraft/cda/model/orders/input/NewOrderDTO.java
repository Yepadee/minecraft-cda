package com.broscraft.cda.model.orders.input;

import com.broscraft.cda.model.items.ItemDTO;
import com.broscraft.cda.model.orders.OrderType;

public class NewOrderDTO {
    private ItemDTO item;
    private Float price;
    private Integer quantity;
    private OrderType type;

    public ItemDTO getItem() {
        return this.item;
    }

    public void setItem(ItemDTO item) {
        this.item = item;
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

    public OrderType getType() {
        return this.type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

}
