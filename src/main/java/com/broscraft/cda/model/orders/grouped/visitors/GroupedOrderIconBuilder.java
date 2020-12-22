package com.broscraft.cda.model.orders.grouped.visitors;

import com.broscraft.cda.model.orders.grouped.GroupedAskDTO;
import com.broscraft.cda.model.orders.grouped.GroupedBidDTO;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.mattstudios.mfgui.gui.components.ItemBuilder;

public class GroupedOrderIconBuilder extends GroupedOrderVisitor {
    private static Material GROUPED_ORDER_ICON_MATERIAL = Material.FLOWER_BANNER_PATTERN;
    private ItemStack icon;

    private void hideAttributes() {
        ItemMeta meta = icon.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        icon.setItemMeta(meta);
    }

    @Override
    public void visit(GroupedBidDTO groupedBid) {
        this.icon = ItemBuilder.from(GROUPED_ORDER_ICON_MATERIAL)
        .setName("Bid")
        .setLore(
            "Price: " + groupedBid.getPrice(),
            "Demand: " + groupedBid.getQuantity()
        )
        .build();
        hideAttributes();
    }

    @Override
    public void visit(GroupedAskDTO groupedAsk) {
        this.icon = ItemBuilder.from(GROUPED_ORDER_ICON_MATERIAL)
        .setName("Ask")
        .setLore(
            "Price: " + groupedAsk.getPrice(),
            "Supply: " + groupedAsk.getQuantity()
        )
        .build();
        hideAttributes();
    }

    public ItemStack getIcon() {
        return this.icon;
    }
    
}
