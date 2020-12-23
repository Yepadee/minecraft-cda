package com.broscraft.cda.gui;

import com.broscraft.cda.gui.screens.item.ItemOrdersScreen;
import com.broscraft.cda.gui.screens.orders.PlayerOrdersScreen;
import com.broscraft.cda.gui.screens.overview.AllItemsScreen;
import com.broscraft.cda.gui.screens.overview.SearchResultsScreen;
import com.broscraft.cda.gui.screens.search.SearchInputScreen;
import com.broscraft.cda.gui.utils.OverviewIconsManager;
import com.broscraft.cda.services.OrderService;
import com.broscraft.cda.utils.ItemUtils;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MarketGui {
    private OverviewIconsManager overviewIconsManager;
    private OrderService orderService;

    public MarketGui(
        OverviewIconsManager overviewIconsManager,
        OrderService orderService
    ) {
        this.overviewIconsManager = overviewIconsManager;
        this.orderService = orderService;
    }

    public void openSearchInputScreen(HumanEntity player) {
        new SearchInputScreen(
            (p, query) -> {
                openSearchResultsScreen(query, p);
            },
            p -> p.getServer().getScheduler().runTask(JavaPlugin.getProvidingPlugin(this.getClass()), () -> {
                openAllItemsScreen(p);
            })
        ).open(player);
    }

    public void openMyOrdersScreen(HumanEntity player) {
        PlayerOrdersScreen playerOrdersScreen = new PlayerOrdersScreen();
        orderService.getPlayerOrders(player.getUniqueId(), orders -> {
            playerOrdersScreen.setOrders(orders, order -> {
                player.sendMessage("clicked order for item " + order.getItem().getId() + "!");
            });
        });
        playerOrdersScreen.open(player);
    }

    public void openAllItemsScreen(HumanEntity player) {
        AllItemsScreen allItemsScreen = new AllItemsScreen(
            overviewIconsManager.getAllIcons(),
            e -> openSearchInputScreen(e.getWhoClicked()),
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
        overviewIconsManager.searchIcons(searchQuery, searchResults -> {
            SearchResultsScreen searchResultsScreen = new SearchResultsScreen(
                "Items matching '" + searchQuery + "'",
                searchResults,
                e -> openAllItemsScreen(e.getWhoClicked()),
                itemStack -> e -> openItemOrdersScreen(itemStack, e.getWhoClicked())
            );
    
            overviewIconsManager.addIconUpdateObserver(searchResultsScreen);
            
            searchResultsScreen.setOnClose(event -> {
                overviewIconsManager.removeIconUpdateObserver(searchResultsScreen);
            });
            searchResultsScreen.open(player);
        });

        
    }

    public void openItemOrdersScreen(ItemStack item, HumanEntity player) {
        Long itemId = ItemUtils.getId(item);
        ItemOrdersScreen itemOrdersScreen = new ItemOrdersScreen(
            item,
            e -> openAllItemsScreen(e.getWhoClicked()),
            e -> {
                e.getWhoClicked().sendMessage("New bid btn clicked for item " + itemId + "!");
            },
            e -> {
                e.getWhoClicked().sendMessage("New ask btn clicked for item " + itemId + "!");
            }
        );
        orderService.getOrders(itemId, orders -> {
            itemOrdersScreen.setOrders(orders);
        });

        itemOrdersScreen.open(player);
    }

    public void openNewBidScreen(HumanEntity player) {
        player.sendMessage("New Bid Button Clicked!!!");
    }

    public void openNewAskScreen(HumanEntity player) {
        player.sendMessage("New Ask Button Clicked!!!");
    }
}
