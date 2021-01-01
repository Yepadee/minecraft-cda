package com.broscraft.cda.dtos;

import java.math.BigDecimal;
import java.util.Objects;

import com.broscraft.cda.dtos.items.ItemDTO;

public class ItemOverviewDTO {
    private ItemDTO item;
    private BigDecimal bestBid;
    private BigDecimal bestAsk;
    private int demand;
    private int supply;


    public ItemOverviewDTO() {
    }

    public ItemOverviewDTO(ItemDTO item, BigDecimal bestBid, BigDecimal bestAsk, int demand, int supply) {
        this.item = item;
        this.bestBid = bestBid;
        this.bestAsk = bestAsk;
        this.demand = demand;
        this.supply = supply;
    }

    public ItemDTO getItem() {
        return this.item;
    }

    public void setItem(ItemDTO item) {
        this.item = item;
    }

    public BigDecimal getBestBid() {
        return this.bestBid;
    }

    public void setBestBid(BigDecimal bestBid) {
        this.bestBid = bestBid;
    }

    public BigDecimal getBestAsk() {
        return this.bestAsk;
    }

    public void setBestAsk(BigDecimal bestAsk) {
        this.bestAsk = bestAsk;
    }

    public int getDemand() {
        return this.demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public int getSupply() {
        return this.supply;
    }

    public void setSupply(int supply) {
        this.supply = supply;
    }

    public ItemOverviewDTO item(ItemDTO item) {
        this.item = item;
        return this;
    }

    public ItemOverviewDTO bestBid(BigDecimal bestBid) {
        this.bestBid = bestBid;
        return this;
    }

    public ItemOverviewDTO bestAsk(BigDecimal bestAsk) {
        this.bestAsk = bestAsk;
        return this;
    }

    public ItemOverviewDTO demand(int demand) {
        this.demand = demand;
        return this;
    }

    public ItemOverviewDTO supply(int supply) {
        this.supply = supply;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ItemOverviewDTO)) {
            return false;
        }
        ItemOverviewDTO itemOverviewDTO = (ItemOverviewDTO) o;
        return Objects.equals(item, itemOverviewDTO.item) && Objects.equals(bestBid, itemOverviewDTO.bestBid) && Objects.equals(bestAsk, itemOverviewDTO.bestAsk) && demand == itemOverviewDTO.demand && supply == itemOverviewDTO.supply;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, bestBid, bestAsk, demand, supply);
    }

    @Override
    public String toString() {
        return "{" +
            " item='" + getItem() + "'" +
            ", bestBid='" + getBestBid() + "'" +
            ", bestAsk='" + getBestAsk() + "'" +
            ", demand='" + getDemand() + "'" +
            ", supply='" + getSupply() + "'" +
            "}";
    }

}
