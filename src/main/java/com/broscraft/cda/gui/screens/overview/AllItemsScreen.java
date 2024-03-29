package com.broscraft.cda.gui.screens.overview;

import java.util.Map;
import java.util.function.Function;

import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.gui.utils.Styles;
import com.broscraft.cda.observers.NewIconObserver;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;

public class AllItemsScreen extends MarketOverviewScreen implements NewIconObserver {
    private Function<ItemDTO, GuiAction<InventoryClickEvent>> onItemClick;

    public AllItemsScreen(Map<ItemDTO, ItemStack> icons, GuiAction<InventoryClickEvent> onSearchBtnClick,
            GuiAction<InventoryClickEvent> onMyOrdersBtnClick,
            Function<ItemDTO, GuiAction<InventoryClickEvent>> onItemClick) {
        super("Market Overview", icons, onItemClick);
        this.createNavbar(onSearchBtnClick, onMyOrdersBtnClick);
        this.onItemClick = onItemClick;
    }

    private void createNavbar(GuiAction<InventoryClickEvent> onSearchBtnClick,
            GuiAction<InventoryClickEvent> onMyOrdersBtnClick) {
        this.gui.setItem(1, 1, ItemBuilder.from(Styles.CLOSE_ICON).asGuiItem(e -> this.gui.close(e.getWhoClicked())));
        this.gui.setItem(1, 4, ItemBuilder.from(Styles.MY_ORDERS_ICON).asGuiItem(onMyOrdersBtnClick));
        this.gui.setItem(1, 6, ItemBuilder.from(Styles.SEARCH_ICON).asGuiItem(onSearchBtnClick));
    }

    @Override
    public void onNewIcon(ItemDTO itemDTO, ItemStack icon) {
        this.addNewItemButton(itemDTO, icon, onItemClick);
    }

}
