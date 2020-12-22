package com.broscraft.cda.gui.screens.overview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.broscraft.cda.CDAPlugin;
import com.broscraft.cda.gui.screens.ScrollableScreen;
import com.broscraft.cda.observers.IconUpdateObserver;
import com.broscraft.cda.utils.ItemUtils;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ScrollType;
import me.mattstudios.mfgui.gui.guis.GuiItem;

public abstract class MarketOverviewScreen extends ScrollableScreen implements IconUpdateObserver {
    int numItems = 0;
    Map<Long, GuiItem> guiItems = new HashMap<>();

    public MarketOverviewScreen(String name, Collection<ItemStack> icons, Function<ItemStack, GuiAction<InventoryClickEvent>> onItemClick) {
        super(name, ScrollType.VERTICAL);
        this.setIcons(icons, onItemClick);
    }

    private void setIcons(Collection<ItemStack> icons, Function<ItemStack, GuiAction<InventoryClickEvent>> onItemClick) {
        icons.forEach(icon -> {
            Long id = ItemUtils.getId(icon);
            this.guiItems.put(id, new GuiItem(icon, onItemClick.apply(icon)));
        });
        this.setItems(new ArrayList<>(this.guiItems.values()));
    }

    
    protected void addNewItemButton(ItemStack icon, Function<ItemStack, GuiAction<InventoryClickEvent>> onItemClick) {
        Long id = ItemUtils.getId(icon);
        GuiItem itemBtn = new GuiItem(icon, onItemClick.apply(icon));
        this.guiItems.put(id, itemBtn);
        this.addItem(itemBtn);
    }

    @Override
    public void onIconUpdate() {
        CDAPlugin.newChain().async(this::update).execute();
    }
    
}
