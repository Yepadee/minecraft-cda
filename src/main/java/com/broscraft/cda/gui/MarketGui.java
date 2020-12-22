package com.broscraft.cda.gui;

import com.broscraft.cda.gui.screens.item.ItemOrdersScreen;
import com.broscraft.cda.gui.screens.overview.AllItemsScreen;
import com.broscraft.cda.gui.screens.overview.SearchResultsScreen;
import com.broscraft.cda.gui.utils.OverviewIconsManager;
import com.broscraft.cda.repositories.OrderRepository;
import com.broscraft.cda.utils.ItemUtils;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class MarketGui {
    private OverviewIconsManager overviewIconsManager;
    private OrderRepository orderRepository;

    public MarketGui(
        OverviewIconsManager overviewIconsManager,
        OrderRepository orderRepository
    ) {
        this.overviewIconsManager = overviewIconsManager;
        this.orderRepository = orderRepository;
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
            itemStack -> e -> openItemOrdersScreen(itemStack, e.getWhoClicked())
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
            itemStack -> e -> openItemOrdersScreen(itemStack, e.getWhoClicked())
        );

        overviewIconsManager.addIconUpdateObserver(searchResultsScreen);
        
        searchResultsScreen.setOnClose(event -> {
            overviewIconsManager.removeIconUpdateObserver(searchResultsScreen);
        });
        searchResultsScreen.open(player);
        
    }

    public void openItemOrdersScreen(ItemStack item, HumanEntity player) {
        Long itemId = ItemUtils.getId(item);
        orderRepository.getOrders(itemId, orders -> {
            new ItemOrdersScreen(
                item,
                orders,
                e -> openAllItemsScreen(e.getWhoClicked()),
                e -> {
                    e.getWhoClicked().sendMessage("New bid btn clicked for item " + itemId + "!");
                },
                e -> {
                    e.getWhoClicked().sendMessage("New ask btn clicked for item " + itemId + "!");
                }
            ).open(player);
        });
    }

}
