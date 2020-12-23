package com.broscraft.cda.commands;
import com.broscraft.cda.model.items.ItemDTO;
import com.broscraft.cda.model.orders.OrderType;
import com.broscraft.cda.model.orders.input.NewOrderDTO;
import com.broscraft.cda.services.OrderService;
import com.broscraft.cda.utils.ItemUtils;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class NewOrderCommand implements CommandExecutor {

    private OrderService orderService;

    public NewOrderCommand(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED.toString() + "Only players may use this command");
            return false;
        } else {
            Player player = (Player) sender;

            if (args.length < 2) return false;
            String orderType = args[0];
            NewOrderDTO newOrderDto = new NewOrderDTO();
            try {
                newOrderDto.setType(OrderType.valueOf(orderType.toUpperCase()));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED.toString() + "Invalid order-type specified");
                return false;
            }
            

            try {
                Float price = Float.parseFloat(args[1]);
                newOrderDto.setPrice(price);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED.toString() + "Invalid price specified");
                return false;
            }

            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (itemStack.getType() == Material.AIR) {
                sender.sendMessage(ChatColor.RED.toString() + "You must be holding an item to create an order for");
                return false;
            }
            ItemDTO itemDTO = ItemUtils.parseItemStack(itemStack);
            newOrderDto.setItem(itemDTO);

            Integer quantity;
            if (args.length > 2) {
                // TODO: count number of items in player inventory to validate they have enough
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
    
            sender.sendMessage(ChatColor.GREEN.toString() + "Created " + ChatColor.BOLD.toString()
            + orderType + ChatColor.RESET.toString() + ChatColor.GREEN.toString() + " order for "
            + quantity + " " + itemStack.getType() + " at " + newOrderDto.getPrice());
            
            //orderService.submitOrder(newOrderDto);
            try {
                orderService.submitOrder(newOrderDto);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED.toString() + e.getMessage());
            }

            return true;
        } 
    }
}
