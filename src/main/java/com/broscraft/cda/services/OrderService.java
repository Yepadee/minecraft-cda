package com.broscraft.cda.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import com.broscraft.cda.CDAPlugin;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.orders.OrderDTO;
import com.broscraft.cda.dtos.orders.OrderType;
import com.broscraft.cda.dtos.orders.grouped.GroupedOrdersDTO;
import com.broscraft.cda.dtos.orders.input.NewOrderDTO;
import com.broscraft.cda.dtos.transaction.TransactionSummaryDTO;
import com.broscraft.cda.observers.OrderObserver;
import com.broscraft.cda.observers.OrderUpdateObserver;
import com.broscraft.cda.repositories.OrderRepository;
import com.broscraft.cda.utils.EcoUtils;
import com.broscraft.cda.utils.InventoryUtils;
import com.broscraft.cda.utils.ItemUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import net.md_5.bungee.api.ChatColor;

public class OrderService {
    private OrderRepository orderRepository;

    private OrderObserver orderObserver;
    private ItemService itemService;
    private Map<UUID, OrderUpdateObserver> orderUpdateObservers = new HashMap<>();

    public OrderService(OrderRepository orderRepository, ItemService itemService, OrderObserver orderObserver) {
        this.orderRepository = orderRepository;
        this.itemService = itemService;
        this.orderObserver = orderObserver;
    }

    public void addOrderUpdateObserver(UUID playerUUID, OrderUpdateObserver orderUpdateObserver) {
        orderUpdateObservers.put(playerUUID, orderUpdateObserver);
    }

    public void removeOrderUpdateObserver(UUID playerUUID) {
        orderUpdateObservers.remove(playerUUID);
    }

    private void notifyOrderUpdateObserver(UUID playerUUID, OrderDTO orderDTO) {
        OrderUpdateObserver o = orderUpdateObservers.get(playerUUID);
        if (o != null) {
            o.onOrderUpdate(orderDTO);
        }
    }

    private void notifyNewOrderObserver(NewOrderDTO newOrderDTO) {
        orderObserver.onNewOrder(newOrderDTO);
    }

    private void notifyNewOrderNewItemObserver(NewOrderDTO newOrderDTO) {
        orderObserver.onNewOrder(newOrderDTO);
    }

    private void notifyRemoveOrderObserver(OrderDTO orderDTO, Float nextBestPrice) {
        orderObserver.onRemoveOrder(orderDTO, nextBestPrice);
    }

    private String getOrderOperationThreadName(ItemDTO itemDTO, float price) {
        Long itemId = itemService.getItemId(itemDTO);
        return "order " + itemId + " " + price;
    }

    public void getItemOrders(ItemDTO itemDTO, Consumer<GroupedOrdersDTO> onComplete) {
        Long itemId = Objects.requireNonNull(itemService.getItemId(itemDTO));
        CDAPlugin.newChain().asyncFirst(() -> {
            return orderRepository.getItemOrders(itemId);
        })
        .abortIfNull() // TODO: handle error
        .syncLast(result -> onComplete.accept(result))
        .execute();
    }

    public void getPlayerOrders(UUID playerUUID, Consumer<List<OrderDTO>> onComplete) {
        CDAPlugin.newChain().asyncFirst(() -> {
            return orderRepository.getPlayerOrders(playerUUID);
        })
        .abortIfNull()
        .syncLast(result -> onComplete.accept(result))
        .execute();
    }

    public void submitOrder(NewOrderDTO newOrderDTO, Runnable onComplete) {  
        CDAPlugin.newSharedChain("submitOrder").async(() -> {
            ItemDTO itemDTO = newOrderDTO.getItem();
            if (itemService.exists(itemDTO)) {
                Long itemId = itemService.getItemId(itemDTO);
                itemDTO.setId(itemId);
                orderRepository.createOrder(newOrderDTO);
                notifyNewOrderObserver(newOrderDTO);
            } else {
                Long itemId = itemService.createItem(itemDTO);
                itemDTO.setId(itemId);
                orderRepository.createOrder(newOrderDTO);
                notifyNewOrderNewItemObserver(newOrderDTO);
            }
        })
        .sync(() -> {
            onComplete.run();
        })
        .execute();   
    }

    public void cancelOrder(OrderDTO orderDTO, Runnable onComplete) {
        HumanEntity player = Bukkit.getPlayer(orderDTO.getPlayerUUID());
        CDAPlugin.newSharedChain(getOrderOperationThreadName(orderDTO.getItem(), orderDTO.getPrice())).asyncFirst(() -> {
            Float nextBestPrice = orderRepository.delete(orderDTO.getId());
            notifyRemoveOrderObserver(
                orderDTO,
                nextBestPrice
            );
            return true;
        })
        .abortIfNull(BukkitTaskChainFactory.MESSAGE, (Player) player, "Sorry, something failed!") //TODO handel failed request.
        .sync(() -> {
            OrderType orderType = Objects.requireNonNull(orderDTO.getType());
            int quantityUnfilled = orderDTO.getQuantity() - orderDTO.getQuantityFilled();
            if (quantityUnfilled > 0) {
                switch (orderType) {
                    case BID:
                        float totalPrice = orderDTO.getPrice() * quantityUnfilled;
                        EcoUtils.pay(player, totalPrice);
                        player.sendMessage(
                            ChatColor.RED + "refunded " + ChatColor.GREEN + EcoUtils.formatPriceCurrency(totalPrice) + ChatColor.RED + " from cancelled " +
                            ChatColor.GOLD + "bid."
                        );
                        break;
                    case ASK:
                        ItemDTO itemDTO = orderDTO.getItem();
                        ItemStack itemsToDrop = ItemUtils.buildItemStack(itemDTO);
                        itemsToDrop.setAmount(quantityUnfilled);
                        InventoryUtils.dropPlayerItems(player, itemsToDrop);
                        player.sendMessage(
                            ChatColor.RED + "returned " + ChatColor.GREEN + quantityUnfilled + ChatColor.RED +
                            ChatColor.WHITE + " '" + ItemUtils.getItemName(itemDTO) + "'" + ChatColor.RED + " from cancelled " +
                            ChatColor.AQUA + "ask."
                        );
                        break;
                }
            } else {
                player.sendMessage(ChatColor.RED + "Order successfully cancelled!");
            }
        })
        .sync(() -> onComplete.run())
        .execute();
    }

    public void collectOrder(HumanEntity player, OrderDTO orderDTO) {
        int availableToCollect = orderDTO.getToCollect();
        if (availableToCollect == 0) return;

        Long orderId = orderDTO.getId();

        TaskChain<?> chain = CDAPlugin.newChain();

        if (orderDTO.getType().equals(OrderType.BID)) {
            chain.asyncFirst(() -> {
                ItemStack itemsToGive = ItemUtils.buildItemStack(orderDTO.getItem());
                int maxStackSize = itemsToGive.getMaxStackSize();
                int numToCollect = availableToCollect > maxStackSize ? maxStackSize : availableToCollect;
                orderRepository.collectOrder(orderId, numToCollect);
                itemsToGive.setAmount(numToCollect);
                return itemsToGive;
            }).abortIfNull(BukkitTaskChainFactory.MESSAGE, (Player) player, "Sorry, something failed!")
            .syncLast(itemsCollected -> {
                InventoryUtils.dropPlayerItems(player, itemsCollected);

                int numCollected = itemsCollected.getAmount();
                player.sendMessage(
                    ChatColor.GOLD + "Collected " +
                    ChatColor.GREEN + numCollected +
                    ChatColor.GRAY + " items!"
                );
                orderDTO.setToCollect(availableToCollect - numCollected);
                notifyOrderUpdateObserver(player.getUniqueId(), orderDTO);
            });
        } else {
            chain.asyncFirst(() -> {
                orderRepository.collectOrder(orderId, availableToCollect);
                return availableToCollect;
            }).abortIfNull(BukkitTaskChainFactory.MESSAGE, (Player) player, "Sorry, something failed!")
            .asyncLast(numCollected -> {
                float moneyCollected = orderDTO.getPrice() * numCollected;
                EcoUtils.pay(player, moneyCollected);
                
                player.sendMessage(ChatColor.AQUA + "Collected " + ChatColor.GREEN + EcoUtils.formatPriceCurrency(moneyCollected) + ChatColor.GRAY + "!");

                orderDTO.setToCollect(0);
                notifyOrderUpdateObserver(player.getUniqueId(), orderDTO);
            });
        }

        chain.execute();
    }

    public void fillOrder(ItemDTO itemDTO, int quantity, float price, Consumer<Integer> onComplete) {
        CDAPlugin.newSharedChain(getOrderOperationThreadName(itemDTO, price))
        .asyncFirst(() -> {
            TransactionSummaryDTO transactionSummary = orderRepository.fillOrder(
                itemService.getItemId(itemDTO),
                price,
                quantity
            );
            return transactionSummary;
        })
        .async(transactionSummary -> {
            transactionSummary.getAffectedOrders().forEach(order -> {
                UUID playerUUID = Objects.requireNonNull(order.getPlayerUUID());
                notifyOrderUpdateObserver(playerUUID, order);
            });
            return transactionSummary.getNumFilled();
        })
        .syncLast(numFilled -> onComplete.accept(numFilled))
        .execute();


    }
}

