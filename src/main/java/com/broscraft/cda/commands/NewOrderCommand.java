package com.broscraft.cda.commands;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.orders.BestPriceDTO;
import com.broscraft.cda.dtos.orders.OrderType;
import com.broscraft.cda.dtos.orders.input.NewOrderDTO;
import com.broscraft.cda.gui.screens.ConfirmScreen;
import com.broscraft.cda.gui.screens.neworders.NewAskItemInputScreen;
import com.broscraft.cda.services.ItemService;
import com.broscraft.cda.services.OrderService;
import com.broscraft.cda.utils.ItemUtils;
import com.broscraft.cda.utils.EcoUtils;
import com.broscraft.cda.utils.InventoryUtils;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class NewOrderCommand implements CommandExecutor {

    private OrderService orderService;
    private ItemService itemService;

    private float MAX_PRICE = 1000000;

    public NewOrderCommand(OrderService orderService, ItemService itemService) {
        this.orderService = orderService;
        this.itemService = itemService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED.toString() + "Only players may use this command");
            return false;
        } else {
            if (args.length < 2) return false;
            NewOrderDTO newOrderDto = new NewOrderDTO();

            Player player = (Player) sender;
            newOrderDto.setPlayerUUID(player.getUniqueId());
            
            String orderType = args[0];
            
            try {
                newOrderDto.setType(OrderType.valueOf(orderType.toUpperCase()));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED.toString() + "Invalid order-type specified");
                return false;
            }
            

            try {
                Float price = EcoUtils.formatPrice(args[1]);
                if (price > MAX_PRICE) {
                    sender.sendMessage(ChatColor.RED.toString() + "Max price exceeded!");
                }
                newOrderDto.setPrice(price);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED.toString() + "Invalid price specified");
                return false;
            }

            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (itemStack == null) {
                sender.sendMessage(ChatColor.RED.toString() + "You must be holding an item to create an order for");
                return false;
            }
            if (itemStack.getType() == Material.AIR) {
                sender.sendMessage(ChatColor.RED.toString() + "You must be holding an item to create an order for");
                return false;
            }

            if (ItemUtils.isDamaged(itemStack) && newOrderDto.getType().equals(OrderType.ASK)) {
                sender.sendMessage(ChatColor.RED.toString() + "Cannot create ask orders for damaged items!");
                return false; 
            }

            ItemDTO itemDTO = ItemUtils.parseItemStack(itemStack);
            newOrderDto.setItem(itemDTO);



            if (newOrderDto.getType().equals(OrderType.BID)) {
                Integer quantity;
                if (args.length > 2) {
                    try {
                        quantity = Integer.parseInt(args[2]);
                        newOrderDto.setQuantity(quantity);
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.RED.toString() + "Invalid quantity specified");
                        return false;
                    }
                } else {
                    quantity = itemStack.getAmount();
                }
                newOrderDto.setQuantity(quantity);
                orderService.submitOrder(player, newOrderDto);
                sendOnCompleteMessage(newOrderDto, player);
            } else {
                Long itemId = itemService.getItemId(itemDTO);
                if (itemId != null) orderService.getBestPrice(
                    itemId,
                    newOrderDto.getType(),
                    bestPrice -> openNewAskItemInputScreen(bestPrice, itemStack, newOrderDto, player));
                else openNewAskItemInputScreen(null, itemStack, newOrderDto, player);
            }

            return true;
        } 
    }

    private void sendOnCompleteMessage(NewOrderDTO newOrderDTO, Player player) {
        ChatColor orderTypeColor;
        if (newOrderDTO.getType().equals(OrderType.ASK)) {
            orderTypeColor = ChatColor.AQUA;
        } else {
            orderTypeColor = ChatColor.GOLD;
        }
        player.sendMessage(ChatColor.GRAY.toString() + "Created " + orderTypeColor +
        newOrderDTO.getType().toString().toLowerCase() + ChatColor.RESET.toString() + ChatColor.GRAY.toString() + " for " +
        orderTypeColor + newOrderDTO.getQuantity() + ChatColor.WHITE + " '" + ItemUtils.getItemName(newOrderDTO.getItem()) + "'" +
        ChatColor.GRAY + " at " + ChatColor.GREEN + EcoUtils.formatPriceCurrency(newOrderDTO.getPrice()));
    }

    private void openNewAskItemInputScreen(BestPriceDTO bestPrice, ItemStack itemStack, NewOrderDTO newOrderDto, Player player) {
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
                "Sell " + newOrderDto.getQuantity() +
                " at " + EcoUtils.formatPriceCurrency(newOrderDto.getPrice()) +
                " each?",
                confirm -> {
                    orderService.submitOrder(player, newOrderDto);
                    screen.close(player);
                    sendOnCompleteMessage(newOrderDto, player);
                },
                cancel -> {
                    InventoryUtils.dropPlayerItems(player, insertedItems);
                    screen.close(player);
                }
            ).open(player);
            

            
            
        });
        screen.open(player);
    }
}
