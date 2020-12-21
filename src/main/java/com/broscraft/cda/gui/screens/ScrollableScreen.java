package com.broscraft.cda.gui.screens;

import java.util.List;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class ScrollableScreen extends ChestGui {
    private static int WIDTH = 9;
    private static int HEIGHT = 6;
    private static int ITEM_AREA_SIZE = (WIDTH - 2) * (HEIGHT - 2);
    private Material NEXT_MATERIAL = Material.GLOWSTONE_DUST;
    private Material PREV_MATERIAL = Material.REDSTONE;
    PaginatedPane scrollingPane = new PaginatedPane(1, 1, 7, 4);
    StaticPane prevBtn = new StaticPane(8,0,1,1);
    StaticPane nextBtn = new StaticPane(8,5,1,1);

    public ScrollableScreen(String name) {
        super(HEIGHT, name);
        this.setOnGlobalClick(event -> event.setCancelled(true));
        this.setBorder();
        this.addNavigationButtons();
        this.addPane(scrollingPane);
    }

    private void setBorder() {
        StaticPane background = new StaticPane(0, 0, 9, 6, Pane.Priority.LOWEST);
        for (int i = 0; i < 9; i ++) {
            background.addItem(new GuiItem(new ItemStack(Material.WHITE_STAINED_GLASS_PANE)), i, 0);
            background.addItem(new GuiItem(new ItemStack(Material.WHITE_STAINED_GLASS_PANE)), i, 5);
        }
        for (int i = 0; i < 6; i ++) {
            background.addItem(new GuiItem(new ItemStack(Material.WHITE_STAINED_GLASS_PANE)), 0, i);
            background.addItem(new GuiItem(new ItemStack(Material.WHITE_STAINED_GLASS_PANE)), 8, i);
        }
        this.addPane(background);
    }

    private void addNavigationButtons() {
        ItemStack prevIcon = new ItemStack(PREV_MATERIAL);
        ItemMeta prevIconMeta = prevIcon.getItemMeta();
        prevIconMeta.setDisplayName("prev");
        prevIcon.setItemMeta(prevIconMeta);
        this.prevBtn.addItem(new GuiItem(prevIcon, event -> this.onPrevBtnClick(event)), 0, 0);

        ItemStack nextIcon = new ItemStack(NEXT_MATERIAL);
        ItemMeta nextIconMeta = nextIcon.getItemMeta();
        nextIconMeta.setDisplayName("next");
        nextIcon.setItemMeta(nextIconMeta);
        this.nextBtn.addItem(new GuiItem(nextIcon, event -> this.onNextBtnClick(event)), 0, 0);

        this.addPane(prevBtn);
        this.addPane(nextBtn);

        prevBtn.setVisible(false);
        nextBtn.setVisible(false);
    }

    private void onPrevBtnClick(InventoryClickEvent event) {
        int currentPageNo = this.scrollingPane.getPage();
        int newPageNo = currentPageNo - 1;
        this.scrollingPane.setPage(newPageNo);
        
        if (newPageNo == 0) {
            this.prevBtn.setVisible(false);
        }
        
        nextBtn.setVisible(true);
        this.update();
    }

    private void onNextBtnClick(InventoryClickEvent event) {
        int currentPageNo = this.scrollingPane.getPage();
        int newPageNo = currentPageNo + 1;
        this.scrollingPane.setPage(newPageNo);
        
        if (newPageNo == this.scrollingPane.getPages() - 1) {
            this.nextBtn.setVisible(false);
        }
        
        prevBtn.setVisible(true);
        this.update();
    }

    protected void setItems(List<GuiItem> guiItems) {
        System.out.println("SET ITEMS:" + guiItems.size() + "/" + ITEM_AREA_SIZE);
        this.scrollingPane.populateWithGuiItems(guiItems);
        if (guiItems.size() > ITEM_AREA_SIZE) {
            System.out.println("OVERFLOW!");
            nextBtn.setVisible(true);
        }
        this.update();
    }
}
