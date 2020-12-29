package com.broscraft.cda.gui;

import com.broscraft.cda.dtos.items.ItemDTO;
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
import com.broscraft.cda.gui.utils.IconsManager;
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
    private static float MAX_PRICE = 1000000;
    private IconsManager iconsManager;
    private OrderService orderService;

    public MarketGui(
        IconsManager iconsManager,
        OrderService orderService
    ) {
        this.iconsManager = iconsManager;
        this.orderService = orderService;
    }

    public void openAllItemsScreen(HumanEntity player) {
        AllItemsScreen allItemsScreen = new AllItemsScreen(
            iconsManager.getAllOverviewIcons(),
            search -> openSearchInputScreen(search.getWhoClicked()),
            myOrders -> openMyOrdersScreen(myOrders.getWhoClicked()),
            itemDTO -> e -> openItemOrdersScreen(itemDTO, player)
        );

        iconsManager.addIconUpdateObserver(allItemsScreen);
        iconsManager.addNewIconObserver(allItemsScreen);
        
        allItemsScreen.setOnClose(event -> {
            iconsManager.removeIconUpdateObserver(allItemsScreen);
            iconsManager.removeNewIconObserver(allItemsScreen);
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
        iconsManager.searchIcons(searchQuery, searchResults -> {
            SearchResultsScreen searchResultsScreen = new SearchResultsScreen(
                "Items matching '" + searchQuery + "'",
                searchResults,
                back -> openAllItemsScreen(player),
                itemDTO -> e -> openItemOrdersScreen(itemDTO, player)
            );
    
            iconsManager.addIconUpdateObserver(searchResultsScreen);
            
            searchResultsScreen.setOnClose(event -> {
                iconsManager.removeIconUpdateObserver(searchResultsScreen);
            });
            searchResultsScreen.open(player);
        });

        
    }

    public void openNewAskScreen(ItemDTO itemDTO, HumanEntity player) {
        NewOrderDTO newOrderDTO = new NewOrderDTO();
        newOrderDTO.setType(OrderType.ASK);
        newOrderDTO.setPlayerUUID(player.getUniqueId());
        newOrderDTO.setItem(itemDTO);
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
                                openNewAskScreen(itemDTO, player); // TODO: is this safe?
                            }
                            newOrderDTO.setPrice(price);
                            openNewAskItemInputScreen(itemDTO, newOrderDTO, player);
                        } catch (NumberFormatException ex) {
                            player.sendMessage(ChatColor.RED + "Invalid price, please try again!");
                            openNewAskScreen(itemDTO, player); // TODO: is this safe?
                        }
                    },
                    close -> {
                        openItemOrdersScreenIfExists(itemDTO, player);
                    }
                ).open(player);
            });
        
    }

    public void openNewBidScreen(ItemDTO itemDTO, HumanEntity player) {
        NewOrderDTO newOrderDTO = new NewOrderDTO();
        newOrderDTO.setType(OrderType.BID);
        newOrderDTO.setPlayerUUID(player.getUniqueId());
        newOrderDTO.setItem(itemDTO);

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
                                openNewBidScreen(itemDTO, player); // TODO: is this safe?
                            }
                            newOrderDTO.setPrice(price);
                            openNewBidQuantitiyInputScreen(itemDTO, newOrderDTO, player);
                        } catch (NumberFormatException ex) {
                            player.sendMessage(ChatColor.RED + "Invalid price, please try again!");
                            openNewBidScreen(itemDTO, player); // TODO: is this safe?
                        }
                    },
                    close -> {
                        openItemOrdersScreenIfExists(itemDTO, player);
                    }
                ).open(player);
            }
        );

    }

    public void openItemOrdersScreen(ItemDTO itemDTO, HumanEntity player) {
        ItemOrdersScreen itemOrdersScreen = new ItemOrdersScreen(
            iconsManager.getItemIcon(itemDTO),
            back -> openAllItemsScreen(player),
            newBid -> openNewBidScreen(itemDTO, player),
            newAsk -> openNewAskScreen(itemDTO, player)
        );
        orderService.getOrders(itemDTO, orders -> {
            itemOrdersScreen.setOrders(
                orders,
                bid -> e -> openBidHitItemInputScreen(bid, itemDTO, 1, player),
                ask -> e -> openAskLiftQuantityInputScreen(ask, itemDTO, 1, player)
            );
        });

        itemOrdersScreen.open(player);
    }

    public boolean itemHasOrders(ItemDTO itemDTO) {
        return iconsManager.hasIcon(itemDTO);
    }


    private void openNewAskItemInputScreen(ItemDTO itemDTO, NewOrderDTO newOrderDto, HumanEntity player) {
        //ItemDTO itemDTO = ItemUtils.parseItemStack(itemStack);
        orderService.getBestPrice(
            newOrderDto.getItem(),
            newOrderDto.getType(),
            bestPrice -> {
                NewAskItemInputScreen screen = new NewAskItemInputScreen(
                    iconsManager.getItemIcon(itemDTO),
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
                            orderService.submitOrder(newOrderDto, () -> {
                                player.sendMessage(
                                    ChatColor.GRAY.toString() + "Created " + ChatColor.AQUA +
                                    "Ask" + ChatColor.RESET.toString() + ChatColor.GRAY.toString() + " for " +
                                    ChatColor.AQUA + newOrderDto.getQuantity() + ChatColor.WHITE + " '" + ItemUtils.getItemName(newOrderDto.getItem()) + "'" +
                                    ChatColor.GRAY + " at " + ChatColor.GREEN + EcoUtils.formatPriceCurrency(newOrderDto.getPrice())
                                );
                                openItemOrdersScreenIfExists(itemDTO, player);
                            });
        
                        },
                        cancel -> {
                            InventoryUtils.dropPlayerItems(player, insertedItems);
                            openItemOrdersScreenIfExists(itemDTO, player);
                        }
                    ).open(player);
                    
                });
                screen.open(player);
            }
        );
    }

    private void openNewBidQuantitiyInputScreen(ItemDTO itemDTO, NewOrderDTO newOrderDto, HumanEntity player) {
        new NewBidQuantityInputScreen(
            (p, quantityTxt) -> {
                try {
                    int quantity = Integer.parseInt(quantityTxt);
                    newOrderDto.setQuantity(quantity);
                    float totalPrice = newOrderDto.getPrice() * quantity;
                    if (EcoUtils.hasMoney(player, totalPrice)) {
                        new ConfirmScreen(
                            "Bid " + EcoUtils.formatPriceCurrency(newOrderDto.getPrice()) +
                            " for " + newOrderDto.getQuantity() +
                            "?",
                            confirm -> {
                                orderService.submitOrder(newOrderDto, () -> {
                                    //screen.close(player);
                                    player.sendMessage(
                                        ChatColor.GRAY.toString() + "Created " + ChatColor.GOLD +
                                        "Bid" + ChatColor.RESET.toString() + ChatColor.GRAY.toString() + " for " +
                                        ChatColor.GOLD + newOrderDto.getQuantity() + ChatColor.WHITE + " '" + ItemUtils.getItemName(newOrderDto.getItem()) + "'" +
                                        ChatColor.GRAY + " at " + ChatColor.GREEN + EcoUtils.formatPriceCurrency(newOrderDto.getPrice())
                                    );
                                    EcoUtils.charge(player, totalPrice);
                                    openItemOrdersScreen(itemDTO, player);
                                });
                            },
                            cancel -> openItemOrdersScreenIfExists(itemDTO, player)
                        ).open(player);
                    } else {
                        player.sendMessage(
                            ChatColor.RED + "You do not have enough money! (Total cost " +
                            EcoUtils.formatPriceCurrency(totalPrice) + ")"
                        );
                        openNewBidQuantitiyInputScreen(itemDTO, newOrderDto, player);
                    }
                    
                } catch (NumberFormatException e) {
                    player.sendMessage(
                        ChatColor.RED + "Invalid quantity! Please try again."
                    );
                    openNewBidQuantitiyInputScreen(itemDTO, newOrderDto, player);
                }
            },
            close -> openItemOrdersScreenIfExists(itemDTO, player)
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

    private void openAskLiftQuantityInputScreen(GroupedOrderDTO groupedOrderDTO, ItemDTO itemDTO, int maxQuantity, HumanEntity player) {
        new AskLiftQuantityInputScreen(
            (e, quantityTxt) -> {
                try {
                    ItemStack itemsToGive = iconsManager.getItemIcon(itemDTO).clone();
                    int quantityToBuy = Integer.parseInt(quantityTxt);
                    float price = groupedOrderDTO.getPrice();
                    float totalPrice = price * quantityToBuy;
                    if (EcoUtils.hasMoney(player, totalPrice)) {
                        new ConfirmScreen(
                            "Buy " + quantityToBuy + " for " + EcoUtils.formatPriceCurrency(totalPrice) + "?",
                            confirm -> {
                                orderService.fillOrder(itemDTO, quantityToBuy, price, quantityBought -> {
                                    float amountToCharge = price * quantityBought;
                                    String boughtPriceStr = EcoUtils.formatPriceCurrency(amountToCharge);
                                    player.sendMessage(
                                        ChatColor.AQUA + "Bought " +
                                        ChatColor.GRAY + quantityBought +
                                        ChatColor.WHITE + " '" + ItemUtils.getItemName(itemDTO) + "'" +
                                        ChatColor.GRAY + " for " + ChatColor.GREEN + boughtPriceStr
                                    );
                                    itemsToGive.setAmount(quantityBought);
                                    EcoUtils.charge(player, amountToCharge);
                                    InventoryUtils.dropPlayerItems(player, itemsToGive);
                                    openItemOrdersScreen(itemDTO, player); //Item should exist now!! TODO: test
                                });
                            },
                            cancel -> openItemOrdersScreenIfExists(itemDTO, player)
                            
                        ).open(player);
                    } else {
                        player.sendMessage(
                            ChatColor.RED + "You do not have enough money! (Total cost " +
                            EcoUtils.formatPriceCurrency(totalPrice) + ")"
                        );
                        openAskLiftQuantityInputScreen(groupedOrderDTO, itemDTO, maxQuantity, player);
                    }
                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + "Invalid quantity, please try again!");
                    openAskLiftQuantityInputScreen(groupedOrderDTO, itemDTO, maxQuantity, player); // TODO: is this safe?
                }
            },
            onClose -> openItemOrdersScreen(itemDTO, player)
        ).open(player);
    }

    private void openBidHitItemInputScreen(GroupedOrderDTO groupedOrderDTO, ItemDTO itemDTO, int maxQuantity, HumanEntity player) {
        BidHitItemInputScreen inputScreen = new BidHitItemInputScreen(
            groupedOrderDTO,
            iconsManager.getItemIcon(itemDTO),
            back -> openItemOrdersScreen(itemDTO, player),
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
                        orderService.fillOrder(itemDTO, quantityToSell, price, quantitySold -> {
                            float amountToPay = price * quantitySold;
                            String soldPriceStr = EcoUtils.formatPriceCurrency(amountToPay);
                            player.sendMessage(
                                ChatColor.GOLD + "Sold " +
                                ChatColor.GRAY + quantitySold +
                                ChatColor.WHITE + " '" + ItemUtils.getItemName(itemDTO) + "'" + 
                                ChatColor.GRAY + " for " + ChatColor.GREEN + soldPriceStr
                            );
                            
                            EcoUtils.pay(player, amountToPay);
                            openItemOrdersScreen(itemDTO, player); // Item should exist here TODO: test
                        }); // TODO: on fail return items!
                    },
                    cancel -> {
                        InventoryUtils.dropPlayerItems(player, insertedItems);
                        openItemOrdersScreen(itemDTO, player);
                    }
                ).open(player);
                
            }
        );
        
        inputScreen.open(player);
    }

    private void openItemOrdersScreenIfExists(ItemDTO itemDTO, HumanEntity player) {
        if (iconsManager.hasIcon(itemDTO)) openItemOrdersScreen(itemDTO, player);
        else player.closeInventory();
    }
}
