package com.broscraft.cda.gui;

import java.util.function.Function;

import com.broscraft.cda.gui.screens.item.ItemOrdersScreen;
import com.broscraft.cda.gui.screens.overview.AllItemsScreen;
import com.broscraft.cda.gui.screens.overview.SearchResultsScreen;
import com.broscraft.cda.gui.utils.OverviewIconsManager;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.GuiAction;

public class MarketGui {
    private OverviewIconsManager overviewIconsManager;

    public MarketGui(OverviewIconsManager overviewIconsManager) {
        this.overviewIconsManager = overviewIconsManager;
    }

    public void openSearchMenuScreen(HumanEntity player) {
        player.sendMessage("Search Button Clicked!!!");
    }

    public void openMyOrdersScreen(HumanEntity player) {
        player.sendMessage("MyOrders Button Clicked!!!");
    }

    public void openAllItemsScreen(HumanEntity player) {
        AllItemsScreen allItemsScreen = new AllItemsScreen(
            overviewIconsManager.getAllIcons(),
            e -> openSearchMenuScreen(e.getWhoClicked()),
            e -> openMyOrdersScreen(e.getWhoClicked()),
            openItemOrdersScreen()
        );

        overviewIconsManager.addIconUpdateObserver(allItemsScreen);
        overviewIconsManager.addNewIconObserver(allItemsScreen);
        
        allItemsScreen.setOnClose(event -> {
            overviewIconsManager.removeIconUpdateObserver(allItemsScreen);
            overviewIconsManager.removeNewIconObserver(allItemsScreen);
        });
        allItemsScreen.open(player);
    }

    public void openSearchResultsScreen(String searchQuery, HumanEntity player) {
        SearchResultsScreen searchResultsScreen = new SearchResultsScreen(
            "Items matching '" + searchQuery + "'",
            overviewIconsManager.searchIcons(searchQuery),
            e -> openAllItemsScreen(e.getWhoClicked()),
            openItemOrdersScreen()
        );

        overviewIconsManager.addIconUpdateObserver(searchResultsScreen);
        
        searchResultsScreen.setOnClose(event -> {
            overviewIconsManager.removeIconUpdateObserver(searchResultsScreen);
        });
        searchResultsScreen.open(player);
        
    }

    public Function<ItemStack, GuiAction<InventoryClickEvent>> openItemOrdersScreen() {
        return itemStack -> (ie -> {
            new ItemOrdersScreen(
                itemStack,
                e -> openAllItemsScreen(e.getWhoClicked()),
                itemId -> (e -> {
                    e.getWhoClicked().sendMessage("New bid btn clicked!");
                }),
                itemId -> (e -> {
                    e.getWhoClicked().sendMessage("New ask btn clicked!");
                })
            ).open(ie.getWhoClicked());
        });
    }
}
