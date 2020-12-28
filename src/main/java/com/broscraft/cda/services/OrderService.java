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
import com.broscraft.cda.dtos.orders.grouped.GroupedOrderDTO;
import com.broscraft.cda.dtos.orders.grouped.GroupedOrdersDTO;
import com.broscraft.cda.dtos.orders.input.NewOrderDTO;
import com.broscraft.cda.observers.OrderObserver;
import com.broscraft.cda.observers.OrderUpdateObserver;
import com.broscraft.cda.repositories.OrderRepository;
import com.broscraft.cda.utils.ItemUtils;
import com.broscraft.cda.utils.PriceUtils;

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


    public void getOrders(Long itemId, Consumer<GroupedOrdersDTO> onComplete) {
        CDAPlugin.newChain().asyncFirst(() -> {
            return orderRepository.getOrders(itemId);
        })
        .abortIfNull()
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

    public void submitOrder(Player player, NewOrderDTO newOrderDTO) {
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
            OrderType orderType = newOrderDTO.getType();
            switch (orderType) {
                case BID:
                    float totalPrice = newOrderDTO.getPrice() * newOrderDTO.getQuantity();
                    PriceUtils.charge(player, totalPrice);
                    break;
                case ASK:
                    // TODO: take items from player
                    break;
            }
        })
        .execute();

        
    }

    public void cancelOrder(OrderDTO orderDTO, Runnable onComplete) {
        CDAPlugin.newSharedChain("cancelOrder").async(() -> {
            orderRepository.delete(orderDTO.getId());
            notifyRemoveOrderObserver(
                orderDTO,
                orderRepository.getBestPrice(orderDTO.getType())
            );
        })
        .sync(() -> {
            OrderType orderType = Objects.requireNonNull(orderDTO.getType());
            switch (orderType) {
                case BID:
                    float totalPrice = orderDTO.getPrice() * (orderDTO.getQuantity() - orderDTO.getQuantityFilled());
                    PriceUtils.pay(orderDTO.getPlayerUUID(), totalPrice);
                    System.out.println(totalPrice);
                    break;
                case ASK:
                    // TODO: take items from player
                    break;
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
                player.getWorld().dropItem(player.getLocation().add(0, 1, 0), itemsCollected);
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
                PriceUtils.pay(player, moneyCollected);
                
                player.sendMessage(ChatColor.AQUA + "Collected " + ChatColor.GREEN + PriceUtils.formatPriceCurrency(moneyCollected) + ChatColor.GRAY + "!");

                orderDTO.setToCollect(0);
                notifyOrderUpdateObserver(player.getUniqueId(), orderDTO);
            });
        }

        chain.execute();
    }

    public void fillOrder(int quantity, GroupedOrderDTO groupedOrderDTO, Runnable onComplete) {
        
        CDAPlugin.newSharedChain("fillOrder")
        .asyncFirst(() -> {
            List<OrderDTO> affectedOrders = orderRepository.fillOrder(groupedOrderDTO.getPrice(), quantity);
            return affectedOrders;
        })
        .abortIfNull()
        .asyncLast(affectedOrders -> {
            affectedOrders.forEach(order -> {
                UUID playerUUID = Objects.requireNonNull(order.getPlayerUUID());
                notifyOrderUpdateObserver(playerUUID, order);
            });
        })
        .sync(() -> onComplete.run())
        .execute();


    }
}

