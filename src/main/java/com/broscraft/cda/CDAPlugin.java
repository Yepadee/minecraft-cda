package com.broscraft.cda;

import java.util.Objects;

import com.broscraft.cda.commands.OpenMenuCommand;
import com.broscraft.cda.modules.CDABinderModule;
import com.google.inject.Inject;
import com.google.inject.Injector;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Continuous Double Action Plugin
 *
 */
public class CDAPlugin extends JavaPlugin
{
    private Injector injector;

    @Inject
    private OpenMenuCommand openMenuCommand;

    @Override
    public void onEnable() {
        getLogger().info("Enabled CDA!");
        CDABinderModule module = new CDABinderModule(this);
        this.injector = module.createInjector();
        this.injector.injectMembers(this);

        this.setupCommands();
    }

    public void setupCommands() {
        Objects.requireNonNull(this.getCommand("market")).setExecutor(openMenuCommand);
    }
}
