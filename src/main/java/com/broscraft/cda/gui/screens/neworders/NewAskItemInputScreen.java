package com.broscraft.cda.gui.screens.neworders;

import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.gui.screens.ItemInputScreen;
import com.broscraft.cda.utils.EcoUtils;
import com.broscraft.cda.utils.ItemUtils;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatColor;

public class NewAskItemInputScreen extends ItemInputScreen {

    public NewAskItemInputScreen(
        ItemDTO acceptedItem,
        Float bestPrice,
        int supply,
        float selectedPrice
    ) {
        super(
            6,
            ChatColor.DARK_AQUA + "Insert items " + ChatColor.DARK_GREEN + "(Price: " + EcoUtils.formatPriceCurrency(selectedPrice) + ") ",
            acceptedItem
        );
        setAcceptedItemIcon(acceptedItem, bestPrice, supply);
    }

    private void setAcceptedItemIcon(ItemDTO acceptedItem, Float bestPrice, int supply) {
        String bestPriceStr = ChatColor.RED + "N/A";
        if (bestPrice != null) {
            bestPriceStr = ChatColor.GREEN + EcoUtils.formatPriceCurrency(bestPrice);
        }
        GuiItem itemIcon = ItemBuilder.from(ItemUtils.buildItemStack(acceptedItem))
        .setLore(
            ChatColor.GRAY + "Best Ask: " + bestPriceStr,
            ChatColor.GRAY + "Best Ask Supply: " + ChatColor.AQUA + supply
        )
        .asGuiItem();
        this.gui.setItem(4, itemIcon);
    }
    
}
