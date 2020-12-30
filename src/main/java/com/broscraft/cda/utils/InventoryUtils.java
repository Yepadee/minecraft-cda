package com.broscraft.cda.utils;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {

    public static void dropPlayerItems(HumanEntity player, final ItemStack item, int quantity) {
        ItemStack clone = item.clone();
        clone.setAmount(quantity);
        player.getWorld().dropItem(player.getLocation().add(0, 1, 0), clone);
    }

    public static void dropPlayerItems(HumanEntity player, ItemStack item) {
        int maxStackSize = item.getMaxStackSize();
        int toGive = item.getAmount();
        while (toGive > maxStackSize) {
            item.setAmount(maxStackSize);
            player.getWorld().dropItem(player.getLocation().add(0, 1, 0), item);
            toGive -= maxStackSize;
        }
        item.setAmount(toGive);
        player.getWorld().dropItem(player.getLocation().add(0, 1, 0), item);
    }

}
