package com.broscraft.cda.utils;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {

    public static void dropPlayerItems(HumanEntity player, ItemStack item) {
        int toGive = item.getAmount();
        if (toGive > 0) {
            int maxStackSize = item.getMaxStackSize();
            while (toGive > maxStackSize) {
                item.setAmount(maxStackSize);
                player.getWorld().dropItem(player.getLocation().add(0, 1, 0), item);
                toGive -= maxStackSize;
            }
            item.setAmount(toGive);
            player.getWorld().dropItem(player.getLocation().add(0, 1, 0), item);
        }

    }

}
