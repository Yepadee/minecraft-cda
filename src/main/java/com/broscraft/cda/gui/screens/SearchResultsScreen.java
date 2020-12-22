package com.broscraft.cda.gui.screens;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;

public class SearchResultsScreen extends MarketOverviewScreen {

    public SearchResultsScreen(String name, Collection<ItemStack> icons, GuiAction<InventoryClickEvent> onBackBtnClick) {
        super(name, icons);
        this.createNavbar(onBackBtnClick);
    }

    protected void createNavbar(GuiAction<InventoryClickEvent> onBackBtnClick) {
        this.gui.setItem(1, 5, ItemBuilder.from(Material.BARRIER).asGuiItem(onBackBtnClick));
    }
    
}
