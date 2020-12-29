package com.broscraft.cda.observers;

import com.broscraft.cda.dtos.items.ItemDTO;

import org.bukkit.inventory.ItemStack;

public interface IconUpdateObserver {
    public void onIconUpdate(ItemDTO itemDTO, ItemStack icon);
}
