package com.broscraft.cda.gui.screens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.broscraft.cda.gui.components.Navbar;
import com.broscraft.cda.observers.IconUpdateObserver;
import com.broscraft.utils.ItemUitls;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MarketOverviewScreen extends ScrollableScreen implements IconUpdateObserver {
    int numItems = 0;
    Map<Long, GuiItem> guiItems = new HashMap<>();
    Player player;

    public MarketOverviewScreen(Collection<ItemStack> icons) {
        super("Market Overview");
        this.addPane(new Navbar());
        this.onNewIcons(icons);
    }

    private GuiItem createItemButton(ItemStack icon) {
        //TODO: take to item screen
        Long id = ItemUitls.getId(icon);
        return new GuiItem(icon, event -> {
            HumanEntity human = event.getWhoClicked();
            human.sendMessage("Clicked item " + id + "!"); // TEMP
        });
    }

    @Override
    public void onNewIcon(ItemStack icon) {
        Long id = ItemUitls.getId(icon);
        this.guiItems.put(id, createItemButton(icon));
        this.setItems(new ArrayList<>(this.guiItems.values()));
        this.update();
    }

    @Override
    public void onIconUpdate() {
        this.update();
    }

    @Override
    public void onNewIcons(Collection<ItemStack> icons) {
        icons.forEach(icon -> {
            Long id = ItemUitls.getId(icon);
            this.guiItems.put(id, createItemButton(icon));
        });
        this.setItems(new ArrayList<>(this.guiItems.values()));
        this.update();
    }
    
}
