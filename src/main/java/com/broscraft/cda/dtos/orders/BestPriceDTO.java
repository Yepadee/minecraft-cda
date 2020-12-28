package com.broscraft.cda.dtos.orders;

import java.util.Objects;

public class BestPriceDTO {
    private Float price;
    private Integer quantity;

    public BestPriceDTO() {
    }

    public BestPriceDTO(Float price, Integer quantity) {
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

    public BestPriceDTO price(Float price) {
        this.price = price;
        return this;
    }

    public BestPriceDTO quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BestPriceDTO)) {
            return false;
        }
        BestPriceDTO bestPriceDTO = (BestPriceDTO) o;
        return Objects.equals(price, bestPriceDTO.price) && Objects.equals(quantity, bestPriceDTO.quantity);
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

}
