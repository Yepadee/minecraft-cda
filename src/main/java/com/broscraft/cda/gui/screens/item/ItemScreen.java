package com.broscraft.cda.gui.screens.item;

import com.broscraft.cda.gui.screens.ScrollableScreen;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;

public class ItemScreen extends ScrollableScreen {

    public ItemScreen(GuiAction<InventoryClickEvent> onBackBtnClick) {
        super("Item Orders");
        this.createNavbar(onBackBtnClick);
    }

    private void createNavbar(GuiAction<InventoryClickEvent> onBackBtnClick) {
        this.gui.setItem(1, 5, ItemBuilder.from(Material.BARRIER).setName("Back").asGuiItem(onBackBtnClick));
    }

}
