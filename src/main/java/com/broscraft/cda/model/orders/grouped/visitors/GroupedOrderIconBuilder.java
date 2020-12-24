package com.broscraft.cda.model.orders.grouped.visitors;

import com.broscraft.cda.gui.utils.Styles;
import com.broscraft.cda.model.orders.grouped.GroupedAskDTO;
import com.broscraft.cda.model.orders.grouped.GroupedBidDTO;
import com.broscraft.cda.utils.ItemUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.ItemBuilder;

public class GroupedOrderIconBuilder extends GroupedOrderVisitor {
    private static Material GROUPED_ORDER_ICON_MATERIAL = Material.FLOWER_BANNER_PATTERN;
    private ItemStack icon;

    @Override
    public void visit(GroupedBidDTO groupedBid) {
        this.icon = ItemUtils.hideAttributes(
            ItemBuilder.from(GROUPED_ORDER_ICON_MATERIAL)
            .setName("Bid")
            .setLore(
                "Price: " + Styles.formatPrice(groupedBid.getPrice()),
                "Demand: " + groupedBid.getQuantity()
            )
            .build()
        );
        
    }

    @Override
    public void visit(GroupedAskDTO groupedAsk) {
        this.icon = ItemUtils.hideAttributes(
            ItemBuilder.from(GROUPED_ORDER_ICON_MATERIAL)
            .setName("Ask")
            .setLore(
                "Price: " + Styles.formatPrice(groupedAsk.getPrice()),
                "Supply: " + groupedAsk.getQuantity()
            )
            .build()
        );
    }

    public ItemStack getIcon() {
        return this.icon;
    }
    
}
