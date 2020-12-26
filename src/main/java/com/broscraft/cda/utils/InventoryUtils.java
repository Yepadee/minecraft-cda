package com.broscraft.cda.utils;

import java.util.Arrays;
import java.util.Map;

import com.earth2me.essentials.craftbukkit.InventoryWorkaround;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.mattstudios.mfgui.gui.components.ItemBuilder;

public class InventoryUtils {
    private static final int USABLE_PLAYER_INV_SIZE = 36;
    
    private static Inventory makeTruncatedPlayerInventory(final PlayerInventory playerInventory) {
        final Inventory fakeInventory = Bukkit.getServer().createInventory(null, USABLE_PLAYER_INV_SIZE);
        fakeInventory.setContents(Arrays.copyOf(playerInventory.getContents(), fakeInventory.getSize()));
        return fakeInventory;
    }
    
    public static int getItemOverflow(final HumanEntity player, final ItemStack item, int amount) {
        ItemStack itemsToGive = ItemBuilder.from(item).setAmount(amount).build();

        Map<Integer, ItemStack> leftovers = InventoryWorkaround.addItems(
            makeTruncatedPlayerInventory(player.getInventory()),
            itemsToGive
        );

        return leftovers.size();
    }


    public static void givePlayerItems(HumanEntity player, ItemStack item, int amount) {
        ItemStack itemsToGive = ItemBuilder.from(item).setAmount(amount).build();

        item.setAmount(amount);
        InventoryWorkaround.addItems(
            player.getInventory(),
            itemsToGive
        );
    }
}
