package com.broscraft.cda.gui;

import java.util.Objects;

import com.broscraft.cda.CDAPlugin;
import com.broscraft.cda.gui.screens.ConfirmScreen;
import com.broscraft.cda.gui.screens.item.ItemOrdersScreen;
import com.broscraft.cda.gui.screens.orders.PlayerOrdersScreen;
import com.broscraft.cda.gui.screens.overview.AllItemsScreen;
import com.broscraft.cda.gui.screens.overview.SearchResultsScreen;
import com.broscraft.cda.gui.screens.search.SearchInputScreen;
import com.broscraft.cda.gui.utils.OverviewIconsManager;
import com.broscraft.cda.model.orders.OrderDTO;
import com.broscraft.cda.services.OrderService;
import com.broscraft.cda.utils.ItemUtils;
import com.google.common.base.Function;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.GuiAction;

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

    public void openMyOrdersScreen(HumanEntity player) {
        PlayerOrdersScreen playerOrdersScreen = new PlayerOrdersScreen(
            e -> openAllItemsScreen(player)
        );
        loadPlayerOrders(playerOrdersScreen, player);
        orderService.addOrderUpdateObserver(player.getUniqueId(), playerOrdersScreen);
        playerOrdersScreen.setOnClose(
            e -> {
                orderService.removeOrderUpdateObserver(player.getUniqueId());
            }
        );
        playerOrdersScreen.open(player);
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

    public void openNewBidScreen(HumanEntity player) {
        player.sendMessage("New Bid Button Clicked!!!");
    }

    public void openNewAskScreen(HumanEntity player) {
        player.sendMessage("New Ask Button Clicked!!!");
    }


    private void openSearchInputScreen(HumanEntity player) {
        new SearchInputScreen(
            (p, query) -> {
                openSearchResultsScreen(query, p);
            },
            p -> CDAPlugin.runTask(() -> {
                openAllItemsScreen(p);
            })
        ).open(player);
    }

    private void confirmCancelOrder(OrderDTO orderDTO, HumanEntity player) {
        new ConfirmScreen(
            "Cancel Order?",
            e -> {
                PlayerOrdersScreen playerOrdersScreen = new PlayerOrdersScreen(
                    ee -> openAllItemsScreen(player)
                );
                orderService.cancelOrder(orderDTO, () -> {
                    loadPlayerOrders(playerOrdersScreen, player);
                });
                playerOrdersScreen.open(player);
            },
            e -> {
                openMyOrdersScreen(player);
            }
        ).open(player);
    }

    private Function<OrderDTO, GuiAction<InventoryClickEvent>> onOrderClick(HumanEntity player) {
        return order -> e -> {
            if (e.getClick().isLeftClick()) {
                orderService.collectOrder(player, order);
            }
            if (e.getClick().isRightClick()) {
                confirmCancelOrder(order, player);
            }
        };
    }

    private void loadPlayerOrders(PlayerOrdersScreen playerOrdersScreen, HumanEntity player) {
        orderService.getPlayerOrders(player.getUniqueId(), orders -> {
            playerOrdersScreen.setOrders(
                orders,
                onOrderClick(player)
            );
        });
    }

    private void openItemOrdersScreen(ItemStack item, HumanEntity player) {
        Long itemId = Objects.requireNonNull(ItemUtils.getId(item));
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

}
