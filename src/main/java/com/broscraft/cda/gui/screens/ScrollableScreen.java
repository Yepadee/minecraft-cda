package com.broscraft.cda.gui.screens;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.components.ScrollType;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import me.mattstudios.mfgui.gui.guis.ScrollingGui;

public abstract class ScrollableScreen {
    protected ScrollingGui gui;

    protected static int WIDTH = 9;
    protected static int HEIGHT = 6;
    protected static Material BACKGROUND_MATERIAL = Material.WHITE_STAINED_GLASS_PANE;
    private static Material NEXT_MATERIAL = Material.GLOWSTONE_DUST;
    private static Material PREV_MATERIAL = Material.REDSTONE;


    public ScrollableScreen(String name, ScrollType scrollType) {
        this.gui = new ScrollingGui(HEIGHT, name, scrollType);
        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
        gui.setDragAction(event -> {
            event.setCancelled(true);
        });

        this.setBorder();
        this.addNavigationButtons(scrollType);
    }

    private void setBorder() {
        GuiItem background = ItemBuilder.from(BACKGROUND_MATERIAL).setName("").asGuiItem();
        for (int row = 1; row <= HEIGHT; row ++) {
            gui.setItem(row, 1, background);
            gui.setItem(row, WIDTH, background);
        }
        for (int col = 1; col <= WIDTH; col ++) {
            gui.setItem(1, col, background);
            gui.setItem(HEIGHT, col, background);
        }
    }

    private void addNavigationButtons(ScrollType scrollType) {
        int prevBtnRow = HEIGHT,
            prevBtnCol = 1,
            nextBtnRow = HEIGHT,
            nextBtnCol = WIDTH;

        String prevBtnTxt = "Previous",
               nextBtnTxt = "Next";

        if (scrollType.equals(ScrollType.VERTICAL)) {
            prevBtnRow = 1;
            prevBtnCol = WIDTH;
            prevBtnTxt = "Scroll Up";
            nextBtnTxt = "Scroll Down";
        }

        gui.setItem(prevBtnRow, prevBtnCol, ItemBuilder.from(PREV_MATERIAL).setName(prevBtnTxt).asGuiItem(this::onPrevBtnClick));
        gui.setItem(nextBtnRow, nextBtnCol, ItemBuilder.from(NEXT_MATERIAL).setName(nextBtnTxt).asGuiItem(this::onNextBtnClick));
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
