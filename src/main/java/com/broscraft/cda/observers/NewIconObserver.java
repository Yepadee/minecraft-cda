package com.broscraft.cda.observers;

import com.broscraft.cda.dtos.items.ItemDTO;

import org.bukkit.inventory.ItemStack;

public interface NewIconObserver {
    public void onNewIcon(ItemDTO itemDTO, ItemStack icon);
}
