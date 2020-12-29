package com.broscraft.cda.gui.screens.overview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.broscraft.cda.CDAPlugin;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.gui.screens.ScrollableScreen;
import com.broscraft.cda.observers.IconUpdateObserver;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ScrollType;
import me.mattstudios.mfgui.gui.guis.GuiItem;

public abstract class MarketOverviewScreen extends ScrollableScreen implements IconUpdateObserver {
    int numItems = 0;
    Map<ItemDTO, GuiItem> guiItems = new HashMap<>();

    public MarketOverviewScreen(
        String name,
        Map<ItemDTO, ItemStack> icons,
        Function<ItemDTO, GuiAction<InventoryClickEvent>> onItemClick) {
        super(name, ScrollType.VERTICAL);
        this.setIcons(icons, onItemClick);
    }

    private void setIcons(Map<ItemDTO, ItemStack> icons, Function<ItemDTO, GuiAction<InventoryClickEvent>> onItemClick) {
        icons.forEach((itemDTO, icon) -> {
            this.guiItems.put(itemDTO, new GuiItem(icon, onItemClick.apply(itemDTO)));
        });
        this.setItems(new ArrayList<>(this.guiItems.values()));
    }

    
    protected void addNewItemButton(ItemDTO itemDTO, ItemStack icon, Function<ItemDTO, GuiAction<InventoryClickEvent>> onItemClick) {
        GuiItem itemBtn = new GuiItem(icon, onItemClick.apply(itemDTO));
        this.guiItems.put(itemDTO, itemBtn);
        this.addItem(itemBtn);
    }

    @Override
    public void onIconUpdate(ItemDTO itemDTO, ItemStack itemStack) {
        guiItems.get(itemDTO).setItemStack(itemStack);
        CDAPlugin.newChain().async(this::update).execute();
    }
    
}
