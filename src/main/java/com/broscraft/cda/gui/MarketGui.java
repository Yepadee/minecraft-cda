package com.broscraft.cda.gui;

import java.math.BigDecimal;

import com.broscraft.cda.dtos.ItemOverviewDTO;
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
import com.broscraft.cda.services.ItemService;
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
    private static BigDecimal MAX_PRICE = BigDecimal.valueOf(1000000);
    private static int MAX_ITEM_STACKS = 8;
    private IconsManager iconsManager;
    private OrderService orderService;
    private ItemService itemService;

    public MarketGui(
        IconsManager iconsManager,
        OrderService orderService,
        ItemService itemService
    ) {
        this.iconsManager = iconsManager;
        this.orderService = orderService;
        this.itemService = itemService;
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

        String placeholder = getPricePlaceholder(itemDTO, OrderType.ASK);

        new NewOrderPriceInputScreen(
            placeholder,
            (p, priceTxt) -> {
                try {
                    BigDecimal price = EcoUtils.parseMoney(priceTxt);
                    if (EcoUtils.greaterThan(price, MAX_PRICE)) {
                        player.sendMessage(ChatColor.RED + "Exeeded max price, please try again!");
                        openNewAskScreen(itemDTO, player);
                        return;
                    }

                    newOrderDTO.setPrice(price);
                    openNewAskItemInputScreen(newOrderDTO, player);

                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + "Invalid price, please try again!");
                    openNewAskScreen(itemDTO, player);
                }
            },
            close -> {
                openItemOrdersScreenIfExists(itemDTO, player);
            }
        ).open(player);
    }

    public void openNewBidScreen(ItemDTO itemDTO, HumanEntity player) {
        NewOrderDTO newOrderDTO = new NewOrderDTO();
        newOrderDTO.setType(OrderType.BID);
        newOrderDTO.setPlayerUUID(player.getUniqueId());
        newOrderDTO.setItem(itemDTO);

        String placeholder = getPricePlaceholder(itemDTO, OrderType.BID);

        new NewOrderPriceInputScreen(
            placeholder,
            (p, priceTxt) -> {
                try {
                    BigDecimal price = EcoUtils.parseMoney(priceTxt);
                    if (EcoUtils.greaterThan(price, MAX_PRICE)) {
                        player.sendMessage(ChatColor.RED + "Exeeded max price, please try again!");
                        openNewBidScreen(itemDTO, player);
                        return;
                    }

                    newOrderDTO.setPrice(price);
                    openNewBidQuantitiyInputScreen(itemDTO, newOrderDTO, player);
                    
                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + "Invalid price, please try again!");
                    openNewBidScreen(itemDTO, player);
                }
            },
            close -> {
                openItemOrdersScreenIfExists(itemDTO, player);
            }
        ).open(player);
        
        

    }

    public void openItemOrdersScreen(ItemDTO itemDTO, HumanEntity player) {
        ItemOrdersScreen itemOrdersScreen = new ItemOrdersScreen(
            iconsManager.getItemIcon(itemDTO),
            back -> openAllItemsScreen(player),
            newBid -> openNewBidScreen(itemDTO, player),
            newAsk -> openNewAskScreen(itemDTO, player)
        );
        orderService.getItemOrders(itemDTO, orders -> {
            itemOrdersScreen.setOrders(
                orders,
                bid -> e -> openBidHitItemInputScreen(bid, itemDTO, player),
                ask -> e -> openAskLiftQuantityInputScreen(ask, itemDTO, player)
            );
        });

        itemOrdersScreen.open(player);
    }

    public boolean itemHasOrders(ItemDTO itemDTO) {
        return iconsManager.hasIcon(itemDTO);
    }


    private String getPricePlaceholder(ItemDTO itemDTO, OrderType orderType) {
        String placeholder = "_";
        ItemOverviewDTO itemOverviewDto = itemService.getItemOverview(itemDTO);

        if (itemOverviewDto != null) {
            BigDecimal bestPrice;
            if (orderType.equals(OrderType.BID)) {
                bestPrice = itemOverviewDto.getBestBid();
            } else {
                bestPrice = itemOverviewDto.getBestAsk();
            }

            if (bestPrice != null) {
                placeholder = bestPrice + "_";
            }

        }
        return placeholder;
    }

    private void openNewAskItemInputScreen(NewOrderDTO newOrderDto, HumanEntity player) {
        ItemDTO itemDTO = newOrderDto.getItem();
        ItemOverviewDTO itemOverview = itemService.getItemOverview(itemDTO);

        NewAskItemInputScreen screen = new NewAskItemInputScreen(
            itemDTO,
            itemOverview,
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
                },
                close -> InventoryUtils.dropPlayerItems(player, insertedItems)
            ).open(player);
            
        });
        screen.open(player);
    }

    private void openNewBidQuantitiyInputScreen(ItemDTO itemDTO, NewOrderDTO newOrderDto, HumanEntity player) {
        new NewBidQuantityInputScreen(
            (p, quantityTxt) -> {
                try {
                    int quantity = Integer.parseInt(quantityTxt);
                    newOrderDto.setQuantity(quantity);
                    BigDecimal totalPrice = EcoUtils.multiply(newOrderDto.getPrice(), quantity);
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
                orderService.addOrderUpdateObserver(player.getUniqueId(), playerOrdersScreen);
                playerOrdersScreen.setOnClose(
                    close -> {
                        orderService.removeOrderUpdateObserver(player.getUniqueId());
                    }
                );
                playerOrdersScreen.open(player);
                orderService.cancelOrder(player, orderDTO, () -> {
                    loadPlayerOrders(playerOrdersScreen, player);
                });
            },
            cancel -> {
                openMyOrdersScreen(player);
            }
        ).open(player);
    }

    private Function<OrderDTO, GuiAction<InventoryClickEvent>> onOrderClick(HumanEntity player) {
        return order -> e -> {
            if (e.getClick().isLeftClick()) {
                orderService.collectOrder(
                    player,
                    order,
                    () -> {}//openMyOrdersScreen(player);
                );
            }
            if (e.getClick().isRightClick()) {
                if (order.getToCollect() > 0) {
                    player.sendMessage(ChatColor.RED + "Please collect your order before deleting it");
                } else {
                    confirmCancelOrder(order, player);
                }
                
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

    private void openAskLiftQuantityInputScreen(GroupedOrderDTO groupedOrderDTO, ItemDTO itemDTO, HumanEntity player) {
        new AskLiftQuantityInputScreen(
            (e, quantityTxt) -> {
                try {
                    ItemStack itemsToGive = iconsManager.getItemIcon(itemDTO).clone();

                    int quantityToBuy = Integer.parseInt(quantityTxt);
                    int maxQuantity = MAX_ITEM_STACKS * itemsToGive.getMaxStackSize();
                    if (quantityToBuy > maxQuantity) {
                        player.sendMessage(
                            ChatColor.RED + "Quantity too large! (MAX: " + maxQuantity + ")"
                        );
                        openAskLiftQuantityInputScreen(groupedOrderDTO, itemDTO, player);
                    } else {
                        BigDecimal price = groupedOrderDTO.getPrice();
                        BigDecimal totalPrice = EcoUtils.multiply(price, quantityToBuy);
                        if (EcoUtils.hasMoney(player, totalPrice)) {
                            new ConfirmScreen(
                                "Buy " + quantityToBuy + " for " + EcoUtils.formatPriceCurrency(totalPrice) + "?",
                                confirm -> {
                                    orderService.fillOrder(OrderType.ASK, itemDTO, quantityToBuy, price, quantityBought -> {
                                        BigDecimal amountToCharge = EcoUtils.multiply(price, quantityBought);
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
                                        int numUnsuccessful = quantityToBuy - quantityBought;
                                        if (numUnsuccessful > 0) {
                                            player.sendMessage(
                                                ChatColor.RED + "Unable to complete transaction for " + numUnsuccessful + " units!"
                                            );
                                        }
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
                            openAskLiftQuantityInputScreen(groupedOrderDTO, itemDTO, player);
                        }
                    }
                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + "Invalid quantity, please try again!");
                    openAskLiftQuantityInputScreen(groupedOrderDTO, itemDTO, player); // TODO: is this safe?
                }
            },
            onClose -> openItemOrdersScreen(itemDTO, player)
        ).open(player);
    }

    private void openBidHitItemInputScreen(GroupedOrderDTO groupedOrderDTO, ItemDTO itemDTO, HumanEntity player) {
        BidHitItemInputScreen inputScreen = new BidHitItemInputScreen(
            groupedOrderDTO,
            itemDTO,
            back -> openItemOrdersScreen(itemDTO, player),
            insertedItems -> {
                if (insertedItems == null) {
                    player.sendMessage(
                        ChatColor.RED + "No items selected!"
                    );
                    return;
                }
                int quantityToSell = insertedItems.getAmount();
                BigDecimal price = groupedOrderDTO.getPrice();
                String totalPriceStr = EcoUtils.formatPriceCurrency(EcoUtils.multiply(price, quantityToSell));
                new ConfirmScreen(
                    "Sell " + insertedItems.getAmount() + " for " + totalPriceStr + "?",
                    confirm -> {
                        orderService.fillOrder(OrderType.BID, itemDTO, quantityToSell, price, quantitySold -> {
                            BigDecimal amountToPay = EcoUtils.multiply(price, quantitySold);
                            String soldPriceStr = EcoUtils.formatPriceCurrency(amountToPay);
                            player.sendMessage(
                                ChatColor.GOLD + "Sold " +
                                ChatColor.GRAY + quantitySold +
                                ChatColor.WHITE + " '" + ItemUtils.getItemName(itemDTO) + "'" + 
                                ChatColor.GRAY + " for " + ChatColor.GREEN + soldPriceStr
                            );
                            int numUnsuccessful = quantityToSell - quantitySold;
                            EcoUtils.pay(player, amountToPay);
                            insertedItems.setAmount(numUnsuccessful);
                            InventoryUtils.dropPlayerItems(player, insertedItems);
                            if (numUnsuccessful > 0) {
                                player.sendMessage(
                                    ChatColor.RED + "Unable to complete transaction for " + numUnsuccessful + " units"
                                );
                            }
                            openItemOrdersScreen(itemDTO, player); // Item should exist here TODO: test
                        }); // TODO: on fail return items!
                    },
                    cancel -> {
                        InventoryUtils.dropPlayerItems(player, insertedItems);
                        openItemOrdersScreen(itemDTO, player);
                    },
                    close -> InventoryUtils.dropPlayerItems(player, insertedItems)
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
