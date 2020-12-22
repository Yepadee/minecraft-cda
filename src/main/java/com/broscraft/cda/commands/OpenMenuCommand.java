package com.broscraft.cda.commands;

import com.broscraft.cda.gui.OverviewIconsManager;
import com.broscraft.cda.gui.screens.AllItemsScreen;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

public class OpenMenuCommand implements CommandExecutor {
    OverviewIconsManager overviewIconsManager;

    public OpenMenuCommand(OverviewIconsManager overviewIconsManager) {
        this.overviewIconsManager = overviewIconsManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        AllItemsScreen allItemsScreen = new AllItemsScreen(
            overviewIconsManager.getAllIcons(),
            e -> {
                e.getWhoClicked().sendMessage("Search Button Clicked!!!");
            },
            e -> {
                e.getWhoClicked().sendMessage("MyOrders Button Clicked!!!");
            }
        );

        overviewIconsManager.addIconUpdateObserver(allItemsScreen);
        overviewIconsManager.addNewIconObserver(allItemsScreen);
        
        allItemsScreen.setOnClose(event -> {
            overviewIconsManager.removeIconUpdateObserver(allItemsScreen);
            overviewIconsManager.removeNewIconObserver(allItemsScreen);
        });
        allItemsScreen.open((HumanEntity) sender);
        
        return true;
    }
}
