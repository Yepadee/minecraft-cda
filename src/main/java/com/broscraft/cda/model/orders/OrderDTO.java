package com.broscraft.cda.model.orders;

import java.util.Objects;

import com.broscraft.cda.model.items.ItemDTO;

public class OrderDTO {
    private Long id;
    private ItemDTO item;
    private OrderType type;
    private Float price;
    private Integer quantity;
    private Integer quantityFilled;

    public OrderDTO() {
    }

    public OrderDTO(Long id, ItemDTO item, OrderType type, Float price, Integer quantity, Integer quantityFilled) {
        this.id = id;
        this.item = item;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.quantityFilled = quantityFilled;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public OrderDTO id(Long id) {
        this.id = id;
        return this;
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
        return Objects.equals(id, orderDTO.id) && Objects.equals(item, orderDTO.item) && Objects.equals(type, orderDTO.type) && Objects.equals(price, orderDTO.price) && Objects.equals(quantity, orderDTO.quantity) && Objects.equals(quantityFilled, orderDTO.quantityFilled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, item, type, price, quantity, quantityFilled);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", item='" + getItem() + "'" +
            ", type='" + getType() + "'" +
            ", price='" + getPrice() + "'" +
            ", quantity='" + getQuantity() + "'" +
            ", quantityFilled='" + getQuantityFilled() + "'" +
            "}";
    }

}
