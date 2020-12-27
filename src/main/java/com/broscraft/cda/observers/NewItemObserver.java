package com.broscraft.cda.observers;

import com.broscraft.cda.model.items.ItemDTO;

public interface NewItemObserver {
    public void onNewItem(ItemDTO newItem);
}
