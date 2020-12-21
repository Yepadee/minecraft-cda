package com.broscraft.cda.gui.screens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.broscraft.cda.observers.IconUpdateObserver;
import com.broscraft.utils.ItemUitls;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.GuiItem;

public class MarketOverviewScreen extends ScrollableScreen implements IconUpdateObserver {
    int numItems = 0;
    Map<Long, GuiItem> guiItems = new HashMap<>();
    Player player;

    Material SEARCH_ICON = Material.COMPASS;
    Material MY_ORDERS_ICON = Material.WRITABLE_BOOK;

    public MarketOverviewScreen(Collection<ItemStack> icons) {
        super("Market Overview");
        this.addNavbar();
        this.onNewIcons(icons);
    }

    private void addNavbar() {
        this.gui.setItem(1, 4, ItemBuilder.from(MY_ORDERS_ICON).asGuiItem(e -> {
            e.getWhoClicked().sendMessage("MyOrders btn clicked");
        }));

        this.gui.setItem(1, 6, ItemBuilder.from(SEARCH_ICON).asGuiItem(e -> {
            e.getWhoClicked().sendMessage("Search btn clicked");
        }));
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
        GuiItem newIcon = createItemButton(icon);
        this.guiItems.put(id, newIcon);
        this.addItem(newIcon);
    }

    @Override
    public void onIconUpdate() {
    }

    @Override
    public void onNewIcons(Collection<ItemStack> icons) {
        icons.forEach(icon -> {
            Long id = ItemUitls.getId(icon);
            this.guiItems.put(id, createItemButton(icon));
        });
        this.setItems(new ArrayList<>(this.guiItems.values()));
    }
    
}
