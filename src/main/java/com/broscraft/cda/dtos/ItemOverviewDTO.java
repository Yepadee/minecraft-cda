package com.broscraft.cda.dtos;

import com.broscraft.cda.dtos.items.ItemDTO;

public class ItemOverviewDTO {
    private ItemDTO item;
    private Float bestBid;
    private Float bestAsk;
    private int demand;
    private int supply;

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

}
