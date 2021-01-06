package com.broscraft.cda.services.comparators;

import java.util.Comparator;

import com.broscraft.cda.dtos.ItemOverviewDTO;

public class SupplyComparator implements Comparator<ItemOverviewDTO> {

    @Override
    public int compare(ItemOverviewDTO a, ItemOverviewDTO b) {
        if (a.getItem().equals(b.getItem())) return 0;
        
        int supply1 = a.getSupply();
        int supply2 = b.getSupply();

        return supply1 <= supply2 ? -1 : 1;
    }
}