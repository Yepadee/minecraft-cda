package com.broscraft.cda.model.orders;

import java.util.Objects;

import com.broscraft.cda.model.items.ItemDTO;

public class OrderDTO {
    private ItemDTO item;
    private OrderType type;
    private Float price;
    private Integer quantity;
    private Integer quantityFilled;

    public OrderDTO() {
    }

    public OrderDTO(ItemDTO item, OrderType type, Float price, Integer quantity, Integer quantityFilled) {
        this.item = item;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.quantityFilled = quantityFilled;
    }

    public ItemDTO getItem() {
        return this.item;
    }

    public void setItem(ItemDTO item) {
        this.item = item;
    }

    public OrderType getType() {
        return this.type;
    }

    public void setType(OrderType type) {
        this.type = type;
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

    public Integer getQuantityFilled() {
        return this.quantityFilled;
    }

    public void setQuantityFilled(Integer quantityFilled) {
        this.quantityFilled = quantityFilled;
    }

    public OrderDTO item(ItemDTO item) {
        this.item = item;
        return this;
    }

    public OrderDTO type(OrderType type) {
        this.type = type;
        return this;
    }

    public OrderDTO price(Float price) {
        this.price = price;
        return this;
    }

    public OrderDTO quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderDTO quantityFilled(Integer quantityFilled) {
        this.quantityFilled = quantityFilled;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof OrderDTO)) {
            return false;
        }
        OrderDTO orderDTO = (OrderDTO) o;
        return Objects.equals(item, orderDTO.item) && Objects.equals(type, orderDTO.type) && Objects.equals(price, orderDTO.price) && Objects.equals(quantity, orderDTO.quantity) && Objects.equals(quantityFilled, orderDTO.quantityFilled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, type, price, quantity, quantityFilled);
    }

    @Override
    public String toString() {
        return "{" +
            " item='" + getItem() + "'" +
            ", type='" + getType() + "'" +
            ", price='" + getPrice() + "'" +
            ", quantity='" + getQuantity() + "'" +
            ", quantityFilled='" + getQuantityFilled() + "'" +
            "}";
    }

}
