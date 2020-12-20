package com.broscraft.cda.gui.screens;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MarketOverviewScreen extends ChestGui {

    public MarketOverviewScreen() {
        super(9, "Items");
        this.setOnGlobalClick(event -> event.setCancelled(true));
        OutlinePane background = new OutlinePane(0, 0, 9, 9, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(new ItemStack(Material.WHITE_STAINED_GLASS_PANE)));
        background.setRepeat(true);
        this.addPane(background);

    }

    
}
