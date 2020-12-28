package com.broscraft.cda.gui.screens.fillOrders;

import java.util.function.Consumer;

import com.broscraft.cda.dtos.orders.grouped.GroupedOrderDTO;
import com.broscraft.cda.gui.screens.ItemInputScreen;
import com.broscraft.cda.utils.EcoUtils;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatColor;

public class BidHitItemInputScreen extends ItemInputScreen {

    public BidHitItemInputScreen(
        GroupedOrderDTO groupedOrderDTO,
        ItemStack acceptedItem,
        GuiAction<InventoryClickEvent> onBack,
        Consumer<ItemStack> onConfirm
    ) {
        super(
            6,
            ChatColor.BOLD + ChatColor.GOLD.toString() + "Insert Items: " + ChatColor.RED + "(MAX: " + groupedOrderDTO.getQuantity() + ")",
            acceptedItem
        );
        setAcceptedItemIcon(groupedOrderDTO, acceptedItem);
        setBackBtn(onBack);
        setConfirmBtn(onConfirm);
    }

    private void setAcceptedItemIcon(GroupedOrderDTO groupedOrderDTO, ItemStack acceptedItem) {
        GuiItem itemIcon = ItemBuilder.from(acceptedItem.clone())
        .setLore(
            ChatColor.GRAY + "Price per unit: " + ChatColor.GREEN + EcoUtils.formatPriceCurrency(groupedOrderDTO.getPrice()),
            ChatColor.GRAY + "Available to sell: " + ChatColor.GOLD + groupedOrderDTO.getQuantity()
        )
        .asGuiItem();
        this.gui.setItem(4, itemIcon);
    }
    
}
