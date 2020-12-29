package com.broscraft.cda.commands;

import com.broscraft.cda.dtos.orders.OrderType;
import com.broscraft.cda.gui.MarketGui;
import com.broscraft.cda.utils.ItemUtils;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class NewOrderCommand implements CommandExecutor {

    private MarketGui marketGui;

    public NewOrderCommand(MarketGui marketGui) {
        this.marketGui = marketGui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) return false;
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED.toString() + "Only players may use this command");
            return false;
        } else {
            Player player = (Player) sender;
            
            String orderTypeStr = args[0];
            try {
                OrderType orderType = OrderType.valueOf(orderTypeStr.toUpperCase());
                ItemStack itemStack = player.getInventory().getItemInMainHand().clone();
                if (itemStack == null) {
                    sender.sendMessage(ChatColor.RED.toString() + "You must be holding an item to create an order for");
                    return false;
                } else if (itemStack.getType() == Material.AIR) {
                    sender.sendMessage(ChatColor.RED.toString() + "You must be holding an item to create an order for");
                    return false;
                } else if (ItemUtils.isDamaged(itemStack) && orderType.equals(OrderType.ASK)) {
                    sender.sendMessage(ChatColor.RED.toString() + "Cannot create ask orders for damaged items!");
                    return false; 
                } else {
                    itemStack.setAmount(0);
                    switch (orderType) {
                        case BID:
                            marketGui.openNewBidScreen(itemStack, player);
                            break;
                        case ASK:
                            marketGui.openNewAskScreen(itemStack, player);
                            break;  
                    }
                    
                }


            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED.toString() + "Invalid order-type specified");
                return false;
            }

            return true;
        } 
    }

}
