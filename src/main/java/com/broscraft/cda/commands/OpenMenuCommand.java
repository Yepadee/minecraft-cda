package com.broscraft.cda.commands;

import com.broscraft.cda.gui.screens.MarketOverviewScreen;
import com.broscraft.cda.repositories.ItemOverviewRepository;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import net.md_5.bungee.api.ChatColor;

public class OpenMenuCommand implements CommandExecutor {
    ItemOverviewRepository itemOverviewRepository = new ItemOverviewRepository();
    MarketOverviewScreen marketOverviewScreen = new MarketOverviewScreen(itemOverviewRepository);
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Executed the OpenMenu command");
        
        marketOverviewScreen.show((HumanEntity) sender);
        return false;
    }
}
