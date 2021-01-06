package com.broscraft.cda.services.comparators;

import java.math.BigDecimal;
import java.util.Comparator;

import com.broscraft.cda.dtos.ItemOverviewDTO;

public class BidPriceComparator implements Comparator<ItemOverviewDTO> {

    @Override
    public int compare(ItemOverviewDTO a, ItemOverviewDTO b) {
        if (a.getItem().equals(b.getItem())) return 0;
        BigDecimal bid1 = a.getBestBid();
        BigDecimal bid2 = b.getBestBid();

        if (bid1 == null) bid1 = BigDecimal.valueOf(1000000000);
        if (bid2 == null) bid2 = BigDecimal.valueOf(1000000000);
        int r = bid1.compareTo(bid2);
        return r != 0 ? r : -1;
    }
    
}
