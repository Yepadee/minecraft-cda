package com.broscraft.cda.dtos.orders.grouped.visitors;

import com.broscraft.cda.dtos.orders.grouped.GroupedAskDTO;
import com.broscraft.cda.dtos.orders.grouped.GroupedBidDTO;
import com.broscraft.cda.utils.ItemUtils;
import com.broscraft.cda.utils.EcoUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class GroupedOrderIconBuilder extends GroupedOrderVisitor {
    private static Material GROUPED_ORDER_ICON_MATERIAL = Material.FLOWER_BANNER_PATTERN;
    private ItemStack icon;

    @Override
    public void visit(GroupedBidDTO groupedBid) {
        this.icon = ItemUtils.hideAttributes(
            ItemBuilder.from(GROUPED_ORDER_ICON_MATERIAL)
            .setName(ChatColor.GOLD + "Bid")
            .setLore(
                ChatColor.GRAY + "Price: " + ChatColor.GREEN + EcoUtils.formatPriceCurrency(groupedBid.getPrice()),
                ChatColor.GRAY + "Demand: " + ChatColor.GOLD + groupedBid.getQuantity()
            )
            .build()
        );
        
    }

    @Override
    public void visit(GroupedAskDTO groupedAsk) {
        this.icon = ItemUtils.hideAttributes(
            ItemBuilder.from(GROUPED_ORDER_ICON_MATERIAL)
            .setName(ChatColor.AQUA + "Ask")
            .setLore(
                ChatColor.GRAY + "Price: " + ChatColor.GREEN + EcoUtils.formatPriceCurrency(groupedAsk.getPrice()),
                ChatColor.GRAY + "Supply: " + ChatColor.AQUA + groupedAsk.getQuantity()
            )
            .build()
        );
    }

    public ItemStack getIcon() {
        return this.icon;
    }
    
}
