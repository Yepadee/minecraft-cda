package com.broscraft.cda.dtos.orders.input;

import java.util.Objects;
import java.util.UUID;

import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.orders.OrderType;

public class NewOrderDTO {
    private UUID playerUUID;
    private ItemDTO item;
    private Float price;
    private Integer quantity;
    private OrderType type;

    public NewOrderDTO() {
    }

    public NewOrderDTO(UUID playerUUID, ItemDTO item, Float price, Integer quantity, OrderType type) {
        this.playerUUID = playerUUID;
        this.item = item;
        this.price = price;
        this.quantity = quantity;
        this.type = type;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

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

    public NewOrderDTO playerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
        return this;
    }

    public NewOrderDTO item(ItemDTO item) {
        this.item = item;
        return this;
    }

    public NewOrderDTO price(Float price) {
        this.price = price;
        return this;
    }

    public NewOrderDTO quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public NewOrderDTO type(OrderType type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof NewOrderDTO)) {
            return false;
        }
        NewOrderDTO newOrderDTO = (NewOrderDTO) o;
        return Objects.equals(playerUUID, newOrderDTO.playerUUID) && Objects.equals(item, newOrderDTO.item) && Objects.equals(price, newOrderDTO.price) && Objects.equals(quantity, newOrderDTO.quantity) && Objects.equals(type, newOrderDTO.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerUUID, item, price, quantity, type);
    }

    @Override
    public String toString() {
        return "{" +
            " playerUUID='" + getPlayerUUID() + "'" +
            ", item='" + getItem() + "'" +
            ", price='" + getPrice() + "'" +
            ", quantity='" + getQuantity() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }

}
