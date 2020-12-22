package com.broscraft.cda.commands;

import com.broscraft.cda.gui.MarketGui;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import net.md_5.bungee.api.ChatColor;

public class SearchMarketCommand implements CommandExecutor {
    private MarketGui marketGui;

    public SearchMarketCommand(MarketGui marketGui) {
        this.marketGui = marketGui;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0) {
            sender.sendMessage(ChatColor.RED.toString() + "No search query specified!");
            return false;
        }

        String arg = args[0];
        String searchQuery;
        if (arg.startsWith("'")) searchQuery = arg.substring(1, arg.length() - 1);
        else searchQuery = arg;

        marketGui.openSearchResultsScreen(searchQuery, (HumanEntity) sender);
        
        return true;
    }
    
}