package com.broscraft.cda.services.comparators;

import java.math.BigDecimal;
import java.util.Comparator;

import com.broscraft.cda.dtos.ItemOverviewDTO;

public class AskPriceComparator implements Comparator<ItemOverviewDTO> {

    @Override
    public int compare(ItemOverviewDTO a, ItemOverviewDTO b) {
        if (a.getItem().equals(b.getItem())) return 0;
        BigDecimal ask1 = a.getBestAsk();
        BigDecimal ask2 = b.getBestAsk();

        if (ask1 == null) ask1 = BigDecimal.valueOf(1000000000);
        if (ask2 == null) ask2 = BigDecimal.valueOf(1000000000);

        int r = ask1.compareTo(ask2);
        return r != 0 ? r : -1;
    }
    
}
