package com.broscraft.cda.observers;

import java.util.Collection;
import com.broscraft.cda.model.ItemOverviewDTO;

public interface OverviewUpdateObserver {
    public void onOverviewUpdate(ItemOverviewDTO itemOverviewDTO);
    public void onOverviewLoad(Collection<ItemOverviewDTO> itemOverviewDTOs);
}
