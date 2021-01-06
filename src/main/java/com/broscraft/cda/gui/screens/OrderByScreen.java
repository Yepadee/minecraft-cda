package com.broscraft.cda.gui.screens;

import com.broscraft.cda.gui.utils.Styles;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.Gui;
import net.md_5.bungee.api.ChatColor;

public final class OrderByScreen {
    private Gui gui;

    public OrderByScreen(
        GuiAction<InventoryClickEvent> onBackBtnClick,
        GuiAction<InventoryClickEvent> onOrderByBidPriceBtnClick,
        GuiAction<InventoryClickEvent> onOrderByAskPriceBtnClick,
        GuiAction<InventoryClickEvent> onOrderByDemandBtnClick,
        GuiAction<InventoryClickEvent> onOrderBySupplyBtnClick
    ) {
        this.gui = new Gui(1, ChatColor.DARK_PURPLE + "Order Items by:");
        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
        gui.setDragAction(event -> {
            event.setCancelled(true);
        });
        setIcons(onBackBtnClick, onOrderByBidPriceBtnClick, onOrderByAskPriceBtnClick, onOrderByDemandBtnClick, onOrderBySupplyBtnClick);
    }

    private void setIcons(
        GuiAction<InventoryClickEvent> onBackBtnClick,
        GuiAction<InventoryClickEvent> onOrderByBidPriceBtnClick,
        GuiAction<InventoryClickEvent> onOrderByAskPriceBtnClick,
        GuiAction<InventoryClickEvent> onOrderByDemandBtnClick,
        GuiAction<InventoryClickEvent> onOrderBySupplyBtnClick
    ) {
        for (int i = 0; i < 9; ++i)
            this.gui.setItem(i, Styles.BACKGROUND);

        this.gui.setItem(2, ItemBuilder.from(Styles.BACK_ICON).asGuiItem(onBackBtnClick));
        this.gui.setItem(3, ItemBuilder.from(Styles.ORDER_BY_BID_ICON).asGuiItem(onOrderByBidPriceBtnClick));
        this.gui.setItem(4, ItemBuilder.from(Styles.ORDER_BY_ASK_ICON).asGuiItem(onOrderByAskPriceBtnClick));
        this.gui.setItem(5, ItemBuilder.from(Styles.ORDER_BY_DEMAND).asGuiItem(onOrderByDemandBtnClick));
        this.gui.setItem(6, ItemBuilder.from(Styles.ORDER_BY_SUPPLY).asGuiItem(onOrderBySupplyBtnClick));

    }

    public void open(HumanEntity player) {
        this.gui.open(player);
    }
}
