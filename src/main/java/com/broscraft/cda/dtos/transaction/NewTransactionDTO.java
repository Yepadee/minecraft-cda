package com.broscraft.cda.dtos.transaction;

import java.math.BigDecimal;
import java.util.Objects;

public class NewTransactionDTO {
    private Long itemId;
    private BigDecimal price;
    private Integer quantity;

    public NewTransactionDTO() {
    }

    public NewTransactionDTO(Long itemId, BigDecimal price, Integer quantity) {
        this.itemId = itemId;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getItemId() {
        return this.itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
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

    public NewTransactionDTO itemId(Long itemId) {
        this.itemId = itemId;
        return this;
    }

    public NewTransactionDTO price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public NewTransactionDTO quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof NewTransactionDTO)) {
            return false;
        }
        NewTransactionDTO transactionDTO = (NewTransactionDTO) o;
        return Objects.equals(itemId, transactionDTO.itemId) && Objects.equals(price, transactionDTO.price) && Objects.equals(quantity, transactionDTO.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, price, quantity);
    }

    @Override
    public String toString() {
        return "{" +
            " itemId='" + getItemId() + "'" +
            ", price='" + getPrice() + "'" +
            ", quantity='" + getQuantity() + "'" +
            "}";
    }

}
