package com.broscraft.cda.gui.screens;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MarketOverviewScreen extends ScrollableScreen {

    public MarketOverviewScreen() {
        super("Market Overview");
        this.setUpPages();
    }

    private void setUpPages() {
        OutlinePane page;
        
        page = new OutlinePane(7, 4);
        ItemStack beacon = new ItemStack(Material.BEACON);
        ItemMeta beaconMeta = beacon.getItemMeta();
        beaconMeta.setDisplayName("Hello World!");
        beacon.setItemMeta(beaconMeta);
        page.addItem(new GuiItem(beacon));
        this.addPage(page);

        page = new OutlinePane(7, 4);
        ItemStack bed = new ItemStack(Material.RED_BED);
        ItemMeta bedMeta = bed.getItemMeta();
        bedMeta.setDisplayName("Hello Again!");
        bed.setItemMeta(bedMeta);

        bed.setItemMeta(bedMeta);
        page.addItem(new GuiItem(bed));
        this.addPage(page);
    }
    
}
