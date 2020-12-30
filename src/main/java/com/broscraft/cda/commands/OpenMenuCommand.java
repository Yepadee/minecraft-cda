package com.broscraft.cda.commands;

import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.gui.MarketGui;
import com.broscraft.cda.utils.ItemUtils;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class OpenMenuCommand implements CommandExecutor {
    private MarketGui marketGui;

    public OpenMenuCommand(MarketGui marketGui) {
        this.marketGui = marketGui;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // TODO: check sender is human.
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command");
            return false;
        } else {
            Player player = (Player) sender;
            if (args.length > 0) {
                if (args[0].equals("this")) {
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    if (itemStack == null) {
                        sender.sendMessage(ChatColor.RED + "You must be holding the item you want to create an order for");
                        return false;
                    } else if (itemStack.getType() == Material.AIR) {
                        sender.sendMessage(ChatColor.RED + "You must be holding the item you want to create an order for");
                        return false;
                    } else {
                        ItemDTO itemDTO = ItemUtils.parseItemStack(itemStack);
                        if (marketGui.itemHasOrders(itemDTO)) {
                            marketGui.openItemOrdersScreen(itemDTO, player);
                        } else {
                            sender.sendMessage(
                                ChatColor.RED + "No orders to show for " +
                                ChatColor.WHITE + "'" + ItemUtils.getItemName(itemDTO) + "'"
                            );
                        }
                        
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + "Invalid argument.");
                    return false;
                }
            } else {
                marketGui.openAllItemsScreen((HumanEntity) sender);
                return true;
            }

        }

    }
}
