package com.broscraft.cda.gui;

import java.util.Objects;

import com.broscraft.cda.dtos.orders.OrderDTO;
import com.broscraft.cda.dtos.orders.OrderType;
import com.broscraft.cda.dtos.orders.grouped.GroupedOrderDTO;
import com.broscraft.cda.dtos.orders.input.NewOrderDTO;
import com.broscraft.cda.gui.screens.ConfirmScreen;
import com.broscraft.cda.gui.screens.fillOrders.AskLiftQuantityInputScreen;
import com.broscraft.cda.gui.screens.fillOrders.BidHitItemInputScreen;
import com.broscraft.cda.gui.screens.item.ItemOrdersScreen;
import com.broscraft.cda.gui.screens.neworders.NewAskItemInputScreen;
import com.broscraft.cda.gui.screens.neworders.NewBidQuantityInputScreen;
import com.broscraft.cda.gui.screens.neworders.NewOrderPriceInputScreen;
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
    private float MAX_PRICE = 1000000;
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

    public void openNewAskScreen(ItemStack itemStack, HumanEntity player) {
        NewOrderDTO newOrderDTO = new NewOrderDTO();
        newOrderDTO.setType(OrderType.ASK);
        newOrderDTO.setPlayerUUID(player.getUniqueId());
        newOrderDTO.setItem(ItemUtils.parseItemStack(itemStack));
        orderService.getBestPrice(
            newOrderDTO.getItem(),
            newOrderDTO.getType(),
            bestPrice -> {
                String placeHolder = "_";
                if (bestPrice != null) placeHolder = bestPrice.getPrice() + "_";
                new NewOrderPriceInputScreen(
                    placeHolder,
                    (p, priceTxt) -> {
                        try {
                            float price = Float.parseFloat(priceTxt);
                            if (price > MAX_PRICE) {
                                player.sendMessage(ChatColor.RED + "Exeeded max price, please try again!");
                                openNewAskScreen(itemStack, player); // TODO: is this safe?
                            }
                            newOrderDTO.setPrice(price);
                            openNewAskItemInputScreen(itemStack, newOrderDTO, player);
                        } catch (NumberFormatException ex) {
                            player.sendMessage(ChatColor.RED + "Invalid price, please try again!");
                            openNewAskScreen(itemStack, player); // TODO: is this safe?
                        }
                    },
                    close -> {
                        openItemOrdersScreen(itemStack, player);
                    }
                ).open(player);
            });
        
    }

    public void openNewBidScreen(ItemStack itemStack, HumanEntity player) {
        NewOrderDTO newOrderDTO = new NewOrderDTO();
        newOrderDTO.setType(OrderType.BID);
        newOrderDTO.setPlayerUUID(player.getUniqueId());
        newOrderDTO.setItem(ItemUtils.parseItemStack(itemStack));

        orderService.getBestPrice(
            newOrderDTO.getItem(),
            newOrderDTO.getType(),
            bestPrice -> {
                String placeholder = "_";
                if (bestPrice != null) placeholder = bestPrice.getPrice() + "_";
                new NewOrderPriceInputScreen(
                    placeholder,
                    (p, priceTxt) -> {
                        try {
                            float price = Float.parseFloat(priceTxt);
                            if (price > MAX_PRICE) {
                                player.sendMessage(ChatColor.RED + "Exeeded max price, please try again!");
                                openNewBidScreen(itemStack, player); // TODO: is this safe?
                            }
                            newOrderDTO.setPrice(price);
                            openNewBidQuantitiyInputScreen(itemStack, newOrderDTO, player);
                        } catch (NumberFormatException ex) {
                            player.sendMessage(ChatColor.RED + "Invalid price, please try again!");
                            openNewBidScreen(itemStack, player); // TODO: is this safe?
                        }
                    },
                    close -> openItemOrdersScreen(itemStack, player)
                ).open(player);
            }
        );

    }


    private void openNewAskItemInputScreen(final ItemStack itemStack, NewOrderDTO newOrderDto, HumanEntity player) {
        //ItemDTO itemDTO = ItemUtils.parseItemStack(itemStack);
        orderService.getBestPrice(
            newOrderDto.getItem(),
            newOrderDto.getType(),
            bestPrice -> {
                NewAskItemInputScreen screen = new NewAskItemInputScreen(
                    itemStack,
                    bestPrice,
                    newOrderDto.getPrice()
                );
                screen.setBackBtn(e -> screen.close(player));
                screen.setConfirmBtn(insertedItems -> {
                    if (insertedItems == null) {
                        player.sendMessage(
                            ChatColor.RED + "No items selected!"
                        );
                        return;
                    }
                    newOrderDto.setQuantity(insertedItems.getAmount());
        
                    new ConfirmScreen(
                        "Ask  " + EcoUtils.formatPriceCurrency(newOrderDto.getPrice()) +
                        " for " + newOrderDto.getQuantity() +
                        "?",
                        confirm -> {
                            orderService.submitOrder(player, newOrderDto, () -> {
                                player.sendMessage(
                                    ChatColor.GRAY.toString() + "Created " + ChatColor.AQUA +
                                    "Ask" + ChatColor.RESET.toString() + ChatColor.GRAY.toString() + " for " +
                                    ChatColor.AQUA + newOrderDto.getQuantity() + ChatColor.WHITE + " '" + ItemUtils.getItemName(newOrderDto.getItem()) + "'" +
                                    ChatColor.GRAY + " at " + ChatColor.GREEN + EcoUtils.formatPriceCurrency(newOrderDto.getPrice())
                                );
                                openItemOrdersScreen(itemStack, player);
                            });
        
                        },
                        cancel -> {
                            InventoryUtils.dropPlayerItems(player, insertedItems);
                            openItemOrdersScreen(itemStack, player);
                        }
                    ).open(player);
                    
                });
                screen.open(player);
            }
        );
    }

    private void openNewBidQuantitiyInputScreen(ItemStack itemStack, NewOrderDTO newOrderDto, HumanEntity player) {
        new NewBidQuantityInputScreen(
            (p, quantityTxt) -> {
                try {
                    int quantity = Integer.parseInt(quantityTxt);
                    newOrderDto.setQuantity(quantity);
                    new ConfirmScreen(
                        "Bid " + EcoUtils.formatPriceCurrency(newOrderDto.getPrice()) +
                        " for " + newOrderDto.getQuantity() +
                        "?",
                        confirm -> {
                            orderService.submitOrder(player, newOrderDto, () -> {
                                //screen.close(player);
                                player.sendMessage(
                                    ChatColor.GRAY.toString() + "Created " + ChatColor.GOLD +
                                    "Bid" + ChatColor.RESET.toString() + ChatColor.GRAY.toString() + " for " +
                                    ChatColor.GOLD + newOrderDto.getQuantity() + ChatColor.WHITE + " '" + ItemUtils.getItemName(newOrderDto.getItem()) + "'" +
                                    ChatColor.GRAY + " at " + ChatColor.GREEN + EcoUtils.formatPriceCurrency(newOrderDto.getPrice())
                                );
                                openItemOrdersScreen(itemStack, player);
                            });
                        },
                        cancel -> openItemOrdersScreen(itemStack, player)
                    ).open(player);
                } catch (NumberFormatException e) {
                    player.sendMessage(
                        ChatColor.RED + "Invalid quantity! Please try again."
                    );
                    openNewBidQuantitiyInputScreen(itemStack, newOrderDto, player);
                }
                
            },
            close -> openItemOrdersScreen(itemStack, player)
        ).open(player);
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

    private void openAskLiftQuantityInputScreen(GroupedOrderDTO groupedOrderDTO, final ItemStack item, int maxQuantity, HumanEntity player) {
        new AskLiftQuantityInputScreen(
            (e, quantityTxt) -> {
                try {
                    ItemStack itemsToGive = item.clone();
                    int quantityToBuy = Integer.parseInt(quantityTxt);
                    float price = groupedOrderDTO.getPrice();
                    float totalPrice = price * quantityToBuy;
                    new ConfirmScreen(
                        "Buy " + quantityToBuy + " for " + EcoUtils.formatPriceCurrency(totalPrice) + "?",
                        confirm -> {
                            orderService.fillOrder(quantityToBuy, price, quantityBought -> {
                                float amountToCharge = price * quantityBought;
                                String boughtPriceStr = EcoUtils.formatPriceCurrency(amountToCharge);
                                player.sendMessage(
                                    ChatColor.RED + "Bought " + quantityBought + " for " + boughtPriceStr
                                );
                                itemsToGive.setAmount(quantityBought);
                                EcoUtils.charge(player, amountToCharge);
                                InventoryUtils.dropPlayerItems(player, itemsToGive);
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
                if (insertedItems == null) {
                    player.sendMessage(
                        ChatColor.RED + "No items selected!"
                    );
                    return;
                }
                int quantityToSell = insertedItems.getAmount();
                float price = groupedOrderDTO.getPrice();
                String totalPriceStr = EcoUtils.formatPriceCurrency(price * quantityToSell);
                new ConfirmScreen(
                    "Sell " + insertedItems.getAmount() + " for " + totalPriceStr + "?",
                    confirm -> {
                        orderService.fillOrder(quantityToSell, price, quantitySold -> {
                            float amountToPay = price * quantitySold;
                            String soldPriceStr = EcoUtils.formatPriceCurrency(amountToPay);
                            player.sendMessage(
                                ChatColor.GREEN + "Sold " + quantitySold + " for " + soldPriceStr
                            );
                            
                            EcoUtils.pay(player, amountToPay);
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
            back -> openAllItemsScreen(player),
            newBid -> openNewBidScreen(item, player),
            newAsk -> openNewAskScreen(item, player)
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
