package com.broscraft.cda.gui.screens.neworders;

import com.broscraft.cda.dtos.orders.BestPriceDTO;
import com.broscraft.cda.gui.screens.ItemInputScreen;
import com.broscraft.cda.utils.EcoUtils;

import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatColor;

public class NewAskItemInputScreen extends ItemInputScreen {

    public NewAskItemInputScreen(
        ItemStack acceptedItem,
        BestPriceDTO bestPrice,
        float selectedPrice
    ) {
        super(
            6,
            ChatColor.DARK_AQUA + "Insert items " + ChatColor.DARK_GREEN + "(Price: " + EcoUtils.formatPriceCurrency(selectedPrice) + ") ",
            acceptedItem
        );
        setAcceptedItemIcon(acceptedItem, bestPrice);
    }

    private void setAcceptedItemIcon(ItemStack acceptedItem, BestPriceDTO bestPrice) {
        int supply = 0;
        String bestPriceStr = ChatColor.RED + "N/A";
        if (bestPrice != null) {
            bestPriceStr = ChatColor.GREEN + EcoUtils.formatPriceCurrency(bestPrice.getPrice());
            supply = bestPrice.getQuantity();
        }
        ItemStack icon = acceptedItem.clone();
        icon.setAmount(1);
        GuiItem itemIcon = ItemBuilder.from(icon)
        .setLore(
            ChatColor.GRAY + "Best Ask: " + bestPriceStr,
            ChatColor.GRAY + "Best Ask Supply: " + ChatColor.AQUA + supply
        )
        .asGuiItem();
        this.gui.setItem(4, itemIcon);
    }
    
}
