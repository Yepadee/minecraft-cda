package com.broscraft.cda.gui.screens.overview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.broscraft.cda.gui.screens.ScrollableScreen;
import com.broscraft.cda.gui.screens.item.ItemScreen;
import com.broscraft.cda.observers.IconUpdateObserver;
import com.broscraft.cda.utils.ItemUitls;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.guis.GuiItem;

public abstract class MarketOverviewScreen extends ScrollableScreen implements IconUpdateObserver {
    int numItems = 0;
    Map<Long, GuiItem> guiItems = new HashMap<>();

    public MarketOverviewScreen(String name, Collection<ItemStack> icons) {
        super(name);
        this.setIcons(icons);
    }

    private void setIcons(Collection<ItemStack> icons) {
        icons.forEach(icon -> {
            Long id = ItemUitls.getId(icon);
            this.guiItems.put(id, createItemButton(icon));
        });
        this.setItems(new ArrayList<>(this.guiItems.values()));
    }

    protected GuiItem createItemButton(ItemStack icon) {
        //TODO: take to item screen
        Long id = ItemUitls.getId(icon);
        return new GuiItem(icon, event -> {
            HumanEntity player = event.getWhoClicked();
            new ItemScreen(
                e -> {
                    this.open(player);
                }
            ).open(player);
            player.sendMessage("Clicked item " + id + "!"); // TEMP
        });
    }

    @Override
    public void onIconUpdate() {
        this.update();
    }
    
}
