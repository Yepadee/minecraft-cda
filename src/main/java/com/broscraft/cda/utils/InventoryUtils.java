package com.broscraft.cda.utils;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
    private static int INVENTORY_SIZE = 36;

    public static int getInvSpace(HumanEntity player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) count ++;
        }
        return INVENTORY_SIZE - count;
    }
}
