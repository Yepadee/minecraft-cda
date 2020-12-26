package com.broscraft.cda.observers;

import org.bukkit.inventory.ItemStack;

public interface IconUpdateObserver {
    public void onIconUpdate(Long itemId, ItemStack icon);
}
