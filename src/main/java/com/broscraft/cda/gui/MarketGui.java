package com.broscraft.cda.gui;

import java.util.Objects;

import com.broscraft.cda.dtos.orders.OrderDTO;
import com.broscraft.cda.dtos.orders.grouped.GroupedOrderDTO;
import com.broscraft.cda.gui.screens.ConfirmScreen;
import com.broscraft.cda.gui.screens.fillOrders.AskLiftQuantityInputScreen;
import com.broscraft.cda.gui.screens.fillOrders.BidHitItemInputScreen;
import com.broscraft.cda.gui.screens.item.ItemOrdersScreen;
import com.broscraft.cda.gui.screens.orders.PlayerOrdersScreen;
import com.broscraft.cda.gui.screens.overview.AllItemsScreen;
import com.broscraft.cda.gui.screens.overview.SearchResultsScreen;
import com.broscraft.cda.gui.screens.search.SearchInputScreen;
import com.broscraft.cda.gui.utils.OverviewIconsManager;
import com.broscraft.cda.services.OrderService;
import com.broscraft.cda.utils.EcoUtils;
import com.broscraft.cda.utils.InventoryUtils;
import com.broscraft.cda.utils.ItemUtils;
import com.google.common.base.Function;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.GuiAction;
import net.md_5.bungee.api.ChatColor;

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
            search -> openSearchInputScreen(search.getWhoClicked()),
            myOrders -> openMyOrdersScreen(myOrders.getWhoClicked()),
            itemStack -> e -> openItemOrdersScreen(itemStack, player)
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
            back -> openAllItemsScreen(player)
        );
        loadPlayerOrders(playerOrdersScreen, player);
        orderService.addOrderUpdateObserver(player.getUniqueId(), playerOrdersScreen);
        playerOrdersScreen.setOnClose(
            close -> {
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
                back -> openAllItemsScreen(player),
                itemStack -> e -> openItemOrdersScreen(itemStack, player)
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
            p -> openAllItemsScreen(p)
        ).open(player);
    }

    private void confirmCancelOrder(OrderDTO orderDTO, HumanEntity player) {
        new ConfirmScreen(
            "Cancel Order?",
            confirm -> {
                PlayerOrdersScreen playerOrdersScreen = new PlayerOrdersScreen(
                   back -> openAllItemsScreen(player)
                );
                orderService.cancelOrder(orderDTO, () -> {
                    loadPlayerOrders(playerOrdersScreen, player);
                });
                playerOrdersScreen.open(player);
            },
            cancel -> {
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

    private void openAskLiftQuantityInputScreen(GroupedOrderDTO groupedOrderDTO, ItemStack item, int maxQuantity, HumanEntity player) {
        new AskLiftQuantityInputScreen(
            (e, quantityTxt) -> {
                try {
                    int quantity = Integer.parseInt(quantityTxt);
                    float price = groupedOrderDTO.getPrice();
                    float totalPrice = quantity * quantity;
                    player.sendMessage(
                        ChatColor.RED + "Buying " + quantity + " for " + totalPrice
                    );
                    new ConfirmScreen(
                        "Buy " + quantity + " for " + totalPrice + "?",
                        confirm -> {
                            orderService.fillOrder(quantity, price, () -> {
                                player.sendMessage(
                                    ChatColor.RED + "Bought " + quantity + " for " + totalPrice
                                );
                                openItemOrdersScreen(item, player);
                            });
                        },
                        cancel -> openItemOrdersScreen(item, player)
                        
                    ).open(player);

                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + "Invalid quantity, please try again!");
                    openAskLiftQuantityInputScreen(groupedOrderDTO, item, maxQuantity, player); // TODO: is this safe?
                }
            },
            onClose -> openItemOrdersScreen(item, player)
        ).open(player);
    }

    private void openBidHitItemInputScreen(GroupedOrderDTO groupedOrderDTO, final ItemStack item, int maxQuantity, HumanEntity player) {
        BidHitItemInputScreen inputScreen = new BidHitItemInputScreen(
            groupedOrderDTO,
            item,
            back -> openItemOrdersScreen(item, player),
            insertedItems -> {
                int quantityToSell = insertedItems.getAmount();
                float price = groupedOrderDTO.getPrice();
                String totalPrice = EcoUtils.formatPriceCurrency(price * quantityToSell);
                new ConfirmScreen(
                    "Sell " + insertedItems.getAmount() + " for " + totalPrice + "?",
                    confirm -> {
                        orderService.fillOrder(quantityToSell, price, () -> {
                            player.sendMessage(
                                ChatColor.GREEN + "Sold " + insertedItems.getAmount() + " for " + totalPrice
                            );
                            openItemOrdersScreen(item, player);
                        }); // TODO: on fail return items!
                    },
                    cancel -> {
                        InventoryUtils.dropPlayerItems(player, insertedItems);
                        openItemOrdersScreen(item, player);
                    }
                ).open(player);
                
            }
        );
        
        inputScreen.open(player);
    }

    private void openItemOrdersScreen(ItemStack item, HumanEntity player) {
        Long itemId = Objects.requireNonNull(ItemUtils.getId(item));
        ItemOrdersScreen itemOrdersScreen = new ItemOrdersScreen(
            item,
            back -> openAllItemsScreen(back.getWhoClicked()),
            newBid -> {
                newBid.getWhoClicked().sendMessage("New bid btn clicked for item " + itemId + "!");
            },
            newAsk -> {
                newAsk.getWhoClicked().sendMessage("New ask btn clicked for item " + itemId + "!");
            }
        );
        orderService.getOrders(itemId, orders -> {
            itemOrdersScreen.setOrders(
                orders,
                bid -> e -> openBidHitItemInputScreen(bid, item, 1, player),
                ask -> e -> openAskLiftQuantityInputScreen(ask, item, 1, player)
                
            );
        });

        itemOrdersScreen.open(player);
    }

}
