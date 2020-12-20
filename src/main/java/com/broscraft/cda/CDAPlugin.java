package com.broscraft.cda;

import java.util.Objects;

import com.broscraft.cda.commands.OpenMenuCommand;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Continuous Double Action Plugin
 *
 */
public class CDAPlugin extends JavaPlugin
{
    private OpenMenuCommand openMenuCommand;
    
    @Override
    public void onEnable() {
        openMenuCommand = new OpenMenuCommand();
        getLogger().info("Enabled CDA!");
        this.setupCommands();
    }

    public void setupCommands() {
        Objects.requireNonNull(this.getCommand("market")).setExecutor(openMenuCommand);
    }
}
