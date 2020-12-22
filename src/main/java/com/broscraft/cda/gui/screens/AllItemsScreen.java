package com.broscraft.cda.gui.screens;

import java.util.Collection;

import com.broscraft.cda.observers.NewIconObserver;
import com.broscraft.utils.ItemUitls;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.GuiItem;

public class AllItemsScreen extends MarketOverviewScreen implements NewIconObserver {
    Material SEARCH_ICON = Material.COMPASS;
    Material MY_ORDERS_ICON = Material.WRITABLE_BOOK;

    public AllItemsScreen(
        Collection<ItemStack> icons,
        GuiAction<InventoryClickEvent> onSearchBtnClick,
        GuiAction<InventoryClickEvent> onMyOrdersBtnClick
    ) {
        super("All Items", icons);
        this.createNavbar(onSearchBtnClick, onMyOrdersBtnClick);
    }

    private void createNavbar(
        GuiAction<InventoryClickEvent> onSearchBtnClick,
        GuiAction<InventoryClickEvent> onMyOrdersBtnClick
    ) {
        this.gui.setItem(1, 6, ItemBuilder.from(SEARCH_ICON).asGuiItem(onSearchBtnClick));
        this.gui.setItem(1, 4, ItemBuilder.from(MY_ORDERS_ICON).asGuiItem(onMyOrdersBtnClick));
    }

    @Override
    public void onNewIcon(ItemStack icon) {
        Long id = ItemUitls.getId(icon);
        GuiItem newIcon = createItemButton(icon);
        this.guiItems.put(id, newIcon);
        this.addItem(newIcon);
    }

}
