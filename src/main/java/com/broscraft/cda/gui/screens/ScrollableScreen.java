package com.broscraft.cda.gui.screens;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import me.mattstudios.mfgui.gui.guis.ScrollingGui;

public abstract class ScrollableScreen {
    protected ScrollingGui gui;

    private static int WIDTH = 9;
    private static int HEIGHT = 6;
    private Material NEXT_MATERIAL = Material.GLOWSTONE_DUST;
    private Material PREV_MATERIAL = Material.REDSTONE;


    public ScrollableScreen(String name) {
        this.gui = new ScrollingGui(HEIGHT, name);
        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
        gui.setDragAction(event -> {
            event.setCancelled(true);
        });

        this.setBorder();
        this.addNavigationButtons();
    }

    private void setBorder() {
        GuiItem background = ItemBuilder.from(Material.WHITE_STAINED_GLASS_PANE).asGuiItem();
        for (int i = 1; i <= HEIGHT; i ++) {
            gui.setItem(i, 1, background);
            gui.setItem(i, WIDTH, background);
        }
        for (int i = 1; i <= WIDTH; i ++) {
            gui.setItem(1, i, background);
            gui.setItem(HEIGHT, i, background);
        }
    }

    private void addNavigationButtons() {
        gui.setItem(1, WIDTH, ItemBuilder.from(PREV_MATERIAL).setName("Scroll Up").asGuiItem(this::onPrevBtnClick));
        gui.setItem(HEIGHT, WIDTH, ItemBuilder.from(NEXT_MATERIAL).setName("Scroll Down").asGuiItem(this::onNextBtnClick));
    }

    private void onPrevBtnClick(InventoryClickEvent event) {
        this.gui.previous();
    }

    private void onNextBtnClick(InventoryClickEvent event) {
        this.gui.next();
    }

    protected void setItems(List<GuiItem> guiItems) {
        guiItems.forEach(gui::addItem);
    }

    protected void addItem(GuiItem guiItem) {
        gui.addItem(guiItem);
        this.update();
    }

    protected void update() {
        this.gui.update();
    }

    public void open(HumanEntity player) {
        gui.open(player);
    }

    public void setOnClose(GuiAction<InventoryCloseEvent> closeGuiAction) {
        gui.setCloseGuiAction(closeGuiAction);
    }
}
