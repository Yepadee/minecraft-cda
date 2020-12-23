package com.broscraft.cda.gui.screens;

import com.broscraft.cda.gui.utils.Styles;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.Gui;
import net.md_5.bungee.api.ChatColor;

public class ConfirmScreen {
    
    
    protected Gui gui;

    private ItemStack confirmIcon = ItemBuilder.from(Material.LIME_STAINED_GLASS_PANE).setName(ChatColor.GREEN + "Yes").build();
    private ItemStack cancelIcon = ItemBuilder.from(Material.RED_STAINED_GLASS_PANE).setName(ChatColor.RED + "No").build();

    public ConfirmScreen(String name, GuiAction<InventoryClickEvent> onConfirm, GuiAction<InventoryClickEvent> onCancel) {
        this.gui = new Gui(1, name);
        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
        gui.setDragAction(event -> {
            event.setCancelled(true);
        });
        setIcons(onConfirm, onCancel);
    }

    private void setIcons(GuiAction<InventoryClickEvent> onConfirm, GuiAction<InventoryClickEvent> onCancel) {
        for (int i = 0; i < 9; ++i)
            this.gui.setItem(i, Styles.BACKGROUND);

        this.gui.setItem(2, ItemBuilder.from(cancelIcon).asGuiItem(onCancel));
        this.gui.setItem(6, ItemBuilder.from(confirmIcon).asGuiItem(onConfirm));

    }

    public void open(HumanEntity player) {
        gui.open(player);
    }

}
