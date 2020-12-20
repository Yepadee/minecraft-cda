package com.broscraft.cda.model;

import com.broscraft.cda.model.items.ItemDTO;

public class ItemOverviewDTO {
    private ItemDTO item;
    private Float bestBid;
    private Float bestAsk;
    private Integer demand;
    private Integer supply;

    public ItemOverviewDTO() {}

    public ItemDTO getItem() {
        return this.item;
    }

    public void setItem(ItemDTO item) {
        this.item = item;
    }

    public Float getBestBid() {
        return this.bestBid;
    }

    public void setBestBid(Float bestBid) {
        this.bestBid = bestBid;
    }

    public Float getBestAsk() {
        return this.bestAsk;
    }

    public void setBestAsk(Float bestAsk) {
        this.bestAsk = bestAsk;
    }
    
    public Integer getDemand() {
        return this.demand;
    }

    public void setDemand(Integer demand) {
        this.demand = demand;
    }

    public Integer getSupply() {
        return this.supply;
    }

    public void setSupply(Integer supply) {
        this.supply = supply;
    }

}
