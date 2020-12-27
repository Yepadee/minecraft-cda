package com.broscraft.cda.gui.screens.fillOrders;

import com.broscraft.cda.gui.screens.ItemInputScreen;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatColor;

public class BidHitItemInputScreen extends ItemInputScreen {

    public BidHitItemInputScreen(ItemStack acceptedItem, GuiAction<InventoryClickEvent> onBack, GuiAction<InventoryClickEvent> onConfirm) {
        super(
            6,
            ChatColor.BOLD + ChatColor.GOLD.toString() + "Insert Items",
            onBack,
            onConfirm
        );
        setAcceptedItem(acceptedItem);
        setAcceptedItemIcon(acceptedItem);
        
    }

    private void setAcceptedItemIcon(ItemStack acceptedItem) {
        GuiItem itemIcon = ItemBuilder.from(acceptedItem)
        .setLore(
            ChatColor.GOLD + "Accepted Item"
        )
        .asGuiItem();
        this.gui.setItem(4, itemIcon);
    }
    
}
