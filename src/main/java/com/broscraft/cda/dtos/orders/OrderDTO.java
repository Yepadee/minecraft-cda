package com.broscraft.cda.dtos.orders;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import com.broscraft.cda.dtos.items.ItemDTO;

public class OrderDTO {
    private Long id;
    private UUID playerUUID;
    private ItemDTO item;
    private OrderType type;
    private BigDecimal price;
    private Integer quantity;
    private Integer quantityFilled;
    private Integer quantityUnfilled;
    private Integer toCollect;


    public OrderDTO() {
    }

    public OrderDTO(Long id, UUID playerUUID, ItemDTO item, OrderType type, BigDecimal price, Integer quantity, Integer quantityFilled, Integer quantityUnfilled, Integer toCollect) {
        this.id = id;
        this.playerUUID = playerUUID;
        this.item = item;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.quantityFilled = quantityFilled;
        this.quantityUnfilled = quantityUnfilled;
        this.toCollect = toCollect;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public OrderType getType() {
        return this.type;
    }

    public void setType(OrderType type) {
        this.type = type;
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

    public Integer getQuantityFilled() {
        return this.quantityFilled;
    }

    public void setQuantityFilled(Integer quantityFilled) {
        this.quantityFilled = quantityFilled;
    }

    public Integer getQuantityUnfilled() {
        return this.quantityUnfilled;
    }

    public void setQuantityUnfilled(Integer quantityUnfilled) {
        this.quantityUnfilled = quantityUnfilled;
    }

    public Integer getToCollect() {
        return this.toCollect;
    }

    public void setToCollect(Integer toCollect) {
        this.toCollect = toCollect;
    }

    public OrderDTO id(Long id) {
        this.id = id;
        return this;
    }

    public OrderDTO playerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
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

    public OrderDTO price(BigDecimal price) {
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

    public OrderDTO quantityUnfilled(Integer quantityUnfilled) {
        this.quantityUnfilled = quantityUnfilled;
        return this;
    }

    public OrderDTO toCollect(Integer toCollect) {
        this.toCollect = toCollect;
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
        return Objects.equals(id, orderDTO.id) && Objects.equals(playerUUID, orderDTO.playerUUID) && Objects.equals(item, orderDTO.item) && Objects.equals(type, orderDTO.type) && Objects.equals(price, orderDTO.price) && Objects.equals(quantity, orderDTO.quantity) && Objects.equals(quantityFilled, orderDTO.quantityFilled) && Objects.equals(quantityUnfilled, orderDTO.quantityUnfilled) && Objects.equals(toCollect, orderDTO.toCollect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, playerUUID, item, type, price, quantity, quantityFilled, quantityUnfilled, toCollect);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", playerUUID='" + getPlayerUUID() + "'" +
            ", item='" + getItem() + "'" +
            ", type='" + getType() + "'" +
            ", price='" + getPrice() + "'" +
            ", quantity='" + getQuantity() + "'" +
            ", quantityFilled='" + getQuantityFilled() + "'" +
            ", quantityUnFilled='" + getQuantityUnfilled() + "'" +
            ", toCollect='" + getToCollect() + "'" +
            "}";
    }


}
