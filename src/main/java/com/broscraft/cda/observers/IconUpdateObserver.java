package com.broscraft.cda.observers;

import java.util.Collection;

import org.bukkit.inventory.ItemStack;

public interface IconUpdateObserver {
    public void onIconUpdate();
    public void onNewIcon(ItemStack icon);
    public void onNewIcons(Collection<ItemStack> icons);
}
