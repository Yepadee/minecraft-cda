package com.broscraft.cda.gui;

import com.broscraft.cda.gui.screens.overview.AllItemsScreen;
import com.broscraft.cda.gui.screens.overview.SearchResultsScreen;
import com.broscraft.cda.gui.utils.OverviewIconsManager;

import org.bukkit.entity.HumanEntity;

public class MarketGui {
    private OverviewIconsManager overviewIconsManager;

    public MarketGui(OverviewIconsManager overviewIconsManager) {
        this.overviewIconsManager = overviewIconsManager;
    }

    public void openAllItemsScreen(HumanEntity player) {
        AllItemsScreen allItemsScreen = new AllItemsScreen(
            overviewIconsManager.getAllIcons(),
            e -> {
                e.getWhoClicked().sendMessage("Search Button Clicked!!!");
            },
            e -> {
                e.getWhoClicked().sendMessage("MyOrders Button Clicked!!!");
            }
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
            e -> {
                e.getWhoClicked().sendMessage("Back Button Clicked!!!");
                openAllItemsScreen(e.getWhoClicked());
            }
        );

        overviewIconsManager.addIconUpdateObserver(searchResultsScreen);
        
        searchResultsScreen.setOnClose(event -> {
            overviewIconsManager.removeIconUpdateObserver(searchResultsScreen);
        });
        searchResultsScreen.open(player);
        
    }
}
