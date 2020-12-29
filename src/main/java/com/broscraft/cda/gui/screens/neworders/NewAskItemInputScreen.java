package com.broscraft.cda.gui.screens.neworders;

import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.orders.BestPriceDTO;
import com.broscraft.cda.gui.screens.ItemInputScreen;
import com.broscraft.cda.utils.EcoUtils;
import com.broscraft.cda.utils.ItemUtils;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatColor;

public class NewAskItemInputScreen extends ItemInputScreen {

    public NewAskItemInputScreen(
        ItemDTO acceptedItem,
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

    private void setAcceptedItemIcon(ItemDTO acceptedItem, BestPriceDTO bestPrice) {
        int supply = 0;
        String bestPriceStr = ChatColor.RED + "N/A";
        if (bestPrice != null) {
            bestPriceStr = ChatColor.GREEN + EcoUtils.formatPriceCurrency(bestPrice.getPrice());
            supply = bestPrice.getQuantity();
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
