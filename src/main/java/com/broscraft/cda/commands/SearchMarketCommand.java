package com.broscraft.cda.commands;

import com.broscraft.cda.gui.OverviewIconsManager;
import com.broscraft.cda.gui.screens.SearchResultsScreen;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import net.md_5.bungee.api.ChatColor;

public class SearchMarketCommand implements CommandExecutor {
    OverviewIconsManager overviewIconsManager;

    public SearchMarketCommand(OverviewIconsManager overviewIconsManager) {
        this.overviewIconsManager = overviewIconsManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0) {
            sender.sendMessage(ChatColor.RED.toString() + "No search query specified!");
            return false;
        }

        String itemName = args[0].substring(1, args[0].length() - 1);

        SearchResultsScreen searchResultsScreen = new SearchResultsScreen(
            "Items matching '" + itemName + "'",
            overviewIconsManager.getAllIcons(),
            e -> {
                e.getWhoClicked().sendMessage("Back Button Clicked!!!");
            }
        );

        overviewIconsManager.addIconUpdateObserver(searchResultsScreen);
        
        searchResultsScreen.setOnClose(event -> {
            overviewIconsManager.removeIconUpdateObserver(searchResultsScreen);
            System.out.println("MENU CLOSED");
        });
        searchResultsScreen.open((HumanEntity) sender);
        
        return true;
    }
}