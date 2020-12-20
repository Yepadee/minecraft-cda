package com.broscraft.cda.gui.screens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    public MarketOverviewScreen() {
        super("Market Overview");
    }

    private GuiItem createGuiItem(ItemStack icon) {
        //TODO: take to item screen
        Long id = ItemUitls.getId(icon);
        return new GuiItem(icon, event -> {
            HumanEntity human = event.getWhoClicked();
            human.sendMessage("Clicked item " + id + "!");
        });
    }

    @Override
    public void onNewIcon(ItemStack icon) {
        Long id = ItemUitls.getId(icon);
        this.guiItems.put(id, createGuiItem(icon));
        this.setItems(new ArrayList<>(this.guiItems.values()));
        this.update();
    }

    @Override
    public void onIconUpdate() {
        System.out.println("Icon updated!");
        this.update();
    }

    @Override
    public void onNewIcons(Collection<ItemStack> icons) {
        icons.forEach(icon -> {
            Long id = ItemUitls.getId(icon);
            this.guiItems.put(id, createGuiItem(icon));
        });
        this.setItems(new ArrayList<>(this.guiItems.values()));
        this.update();
    }
    
}
