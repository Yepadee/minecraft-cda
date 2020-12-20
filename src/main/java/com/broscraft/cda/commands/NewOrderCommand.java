package com.broscraft.cda.commands;

import com.broscraft.cda.model.orders.AskDTO;
import com.broscraft.cda.model.orders.BidDTO;
import com.broscraft.cda.model.orders.OrderDTO;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class NewOrderCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED.toString() + "Only players may use this command");
            return false;
        } else {
            Player player = (Player) sender;

            if (args.length < 2) return false;
            String orderType = args[0];
            OrderDTO orderDto;
    
            switch (orderType) {
                case "bid":
                    orderDto = new BidDTO();
                    break;
                case "ask":
                    orderDto = new AskDTO();
                    break;
                default:
                    sender.sendMessage(ChatColor.RED.toString() + "Invalid order type");
                    return false;
            }
    
            try {
                Float price = Float.parseFloat(args[1]);
                orderDto.setPrice(price);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED.toString() + "Invalid price specified");
                return false;
            }

            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (itemStack.getType() == Material.AIR) {
                sender.sendMessage(ChatColor.RED.toString() + "You must be holding an item to create an order for");
                return false;
            }
            Integer quantity;
            if (args.length > 2) {
                try {
                    quantity = Integer.parseInt(args[2]);
                    orderDto.setQuantity(quantity);
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED.toString() + "Invalid quantity specified");
                    return false;
                }
            } else {
                quantity = itemStack.getAmount();
            }
            orderDto.setQuantity(quantity);
    
            sender.sendMessage(ChatColor.GREEN.toString() + "Created " + ChatColor.BOLD.toString()
            + orderType + ChatColor.RESET.toString() + ChatColor.GREEN.toString() + " order for "
            + quantity + " " + itemStack.getType() + " at " + orderDto.getPrice());
    
            return true;
        } 
    }
}
