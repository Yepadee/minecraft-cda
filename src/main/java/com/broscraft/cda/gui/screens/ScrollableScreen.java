package com.broscraft.cda.gui.screens;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class ScrollableScreen extends ChestGui {

    private Material NEXT_MATERIAL = Material.GLOWSTONE_DUST;
    private Material PREV_MATERIAL = Material.REDSTONE;

    public ScrollableScreen(String name) {
        super(9, name);
        this.setOnGlobalClick(event -> event.setCancelled(true));
        this.setBackground();
        this.addScrollBarPane();
    }

    private void setBackground() {
        
        OutlinePane background = new OutlinePane(0, 0, 9, 9, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(new ItemStack(Material.WHITE_STAINED_GLASS_PANE)));
        background.setRepeat(true);
        this.addPane(background);
    }

    private void addScrollBarPane() {
        StaticPane scrollBarPane = new StaticPane(8, 0, 1, 9);

        ItemStack prevIcon = new ItemStack(PREV_MATERIAL);
        ItemMeta prevIconMeta = prevIcon.getItemMeta();
        prevIconMeta.setDisplayName("prev");
        prevIcon.setItemMeta(prevIconMeta);
        scrollBarPane.addItem(new GuiItem(prevIcon, event -> {
            this.onScrollUp(event);
        }), 0, 0);

        ItemStack nextIcon = new ItemStack(NEXT_MATERIAL);
        ItemMeta nextIconMeta = nextIcon.getItemMeta();
        nextIconMeta.setDisplayName("next");
        nextIcon.setItemMeta(nextIconMeta);
        scrollBarPane.addItem(new GuiItem(nextIcon, event -> {
            this.onScrollDown(event);
        }), 0, 8);

        this.addPane(scrollBarPane);
    }

    protected abstract void onScrollDown(InventoryClickEvent event);
    protected abstract void onScrollUp(InventoryClickEvent event);

    
}
