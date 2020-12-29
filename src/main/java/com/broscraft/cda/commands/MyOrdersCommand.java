package com.broscraft.cda.commands;

import com.broscraft.cda.gui.MarketGui;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class MyOrdersCommand implements CommandExecutor {
    private MarketGui marketGui;

    public MyOrdersCommand(MarketGui marketGui) {
        this.marketGui = marketGui;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED.toString() + "Only players may use this command");
            return false;
        } else {
            marketGui.openMyOrdersScreen((Player) sender);
            return true;
        }
    }
}
