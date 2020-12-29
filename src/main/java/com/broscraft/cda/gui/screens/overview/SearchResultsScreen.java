package com.broscraft.cda.gui.screens.overview;

import java.util.Map;
import java.util.function.Function;

import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.gui.utils.Styles;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;

public class SearchResultsScreen extends MarketOverviewScreen {
    public SearchResultsScreen(
        String name,
        Map<ItemDTO, ItemStack> icons,
        GuiAction<InventoryClickEvent> onBackBtnClick,
        Function<ItemDTO, GuiAction<InventoryClickEvent>> onItemClick
    ) {
        super(name, icons, onItemClick);
        this.createNavbar(onBackBtnClick);
    }

    private void createNavbar(GuiAction<InventoryClickEvent> onBackBtnClick) {
        this.gui.setItem(1, 1, ItemBuilder.from(Styles.BACK_ICON).asGuiItem(onBackBtnClick));
    }
    
}
