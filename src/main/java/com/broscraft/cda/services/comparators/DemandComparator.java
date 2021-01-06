package com.broscraft.cda.services.comparators;

import java.util.Comparator;

import com.broscraft.cda.dtos.ItemOverviewDTO;

public class DemandComparator implements Comparator<ItemOverviewDTO> {

    @Override
    public int compare(ItemOverviewDTO a, ItemOverviewDTO b) {
        if (a.getItem().equals(b.getItem())) return 0;
        
        int demand1 = a.getDemand();
        int demand2 = b.getDemand();

        return demand1 <= demand2 ? -1 : 1;
    }
}
