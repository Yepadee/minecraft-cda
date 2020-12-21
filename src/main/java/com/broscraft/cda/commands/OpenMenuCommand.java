package com.broscraft.cda.commands;

import com.broscraft.cda.gui.OverviewIconsManager;
import com.broscraft.cda.gui.screens.MarketOverviewScreen;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import net.md_5.bungee.api.ChatColor;

public class OpenMenuCommand implements CommandExecutor {
    OverviewIconsManager overviewIconsManager;

    public OpenMenuCommand(OverviewIconsManager overviewIconsManager) {
        this.overviewIconsManager = overviewIconsManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Executed the OpenMenu command");
        MarketOverviewScreen marketOverviewScreen = new MarketOverviewScreen(overviewIconsManager.getAllIcons());
        overviewIconsManager.addIconUpdateObserver(marketOverviewScreen);
        marketOverviewScreen.show((HumanEntity) sender);
        marketOverviewScreen.setOnClose(event -> overviewIconsManager.removeIconUpdateObserver(marketOverviewScreen));
        return true;
    }
}
