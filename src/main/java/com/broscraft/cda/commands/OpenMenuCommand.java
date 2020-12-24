package com.broscraft.cda.commands;

import com.broscraft.cda.gui.MarketGui;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

public class OpenMenuCommand implements CommandExecutor {
    private MarketGui marketGui;

    public OpenMenuCommand(MarketGui marketGui) {
        this.marketGui = marketGui;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // TODO: check sender is human.
        marketGui.openAllItemsScreen((HumanEntity) sender);
        return true;
    }
}
