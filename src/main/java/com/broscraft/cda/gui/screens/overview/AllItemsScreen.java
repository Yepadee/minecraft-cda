package com.broscraft.cda.gui.screens.overview;

import java.util.Collection;
import java.util.function.Function;

import com.broscraft.cda.observers.NewIconObserver;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;

public class AllItemsScreen extends MarketOverviewScreen implements NewIconObserver {
    private static Material SEARCH_ICON = Material.COMPASS;
    private static Material MY_ORDERS_ICON = Material.WRITABLE_BOOK;

    private Function<ItemStack, GuiAction<InventoryClickEvent>> onItemClick;

    public AllItemsScreen(
        Collection<ItemStack> icons,
        GuiAction<InventoryClickEvent> onSearchBtnClick,
        GuiAction<InventoryClickEvent> onMyOrdersBtnClick,
        Function<ItemStack, GuiAction<InventoryClickEvent>> onItemClick
    ) {
        super("All Items", icons, onItemClick);
        this.createNavbar(onSearchBtnClick, onMyOrdersBtnClick);
        this.onItemClick = onItemClick;
    }

    private void createNavbar(
        GuiAction<InventoryClickEvent> onSearchBtnClick,
        GuiAction<InventoryClickEvent> onMyOrdersBtnClick
    ) {
        this.gui.setItem(1, 6, ItemBuilder.from(SEARCH_ICON).setName("Search").asGuiItem(onSearchBtnClick));
        this.gui.setItem(1, 4, ItemBuilder.from(MY_ORDERS_ICON).setName("My Orders").asGuiItem(onMyOrdersBtnClick));
    }

    @Override
    public void onNewIcon(ItemStack icon) {
        this.addNewItemButton(icon, onItemClick);
    }

}
